package technbolts.compta.server;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import swoop.*;
import technbolts.compta.price.command.CatalogCommandHandler;
import technbolts.compta.price.command.CreateCatalogCommand;
import technbolts.compta.price.view.CatalogEntryView;
import technbolts.compta.price.view.CatalogView;
import technbolts.compta.price.view.CatalogViews;
import technbolts.compta.price.view.CatalogViewsUpdater;
import technbolts.core.infrastructure.*;
import technbolts.core.infrastructure.support.JdbcEventStore;
import technbolts.core.infrastructure.support.JdbcStoreUpdate;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

import static swoop.Swoop.get;
import static swoop.Swoop.post;
import static technbolts.core.util.MapBuilder.mapBuilderOO;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class Server {

    public static void main(String[] args) throws ParseException {
        new Server().start(args);
    }

    private Logger log = LoggerFactory.getLogger(Server.class);

    private SwoopBuilder swoopServer;
    //
    private String dbMigrations = "sql/h2";
    private String dbUrl = "jdbc:h2:~/.compta/db";
    private String dbUsername = "sa";
    private String dbPassword = "sa";
    //
    private DefaultEventBus<VersionedDomainEvent> eventBus;
    private CatalogViews catalogViews;
    private DataSource dataSource;
    private CatalogCommandHandler catalogCommandHandler;

    public Server() {
        swoopServer = Swoop.context();
    }

    public int getPort() {
        return swoopServer.getPort();
    }

    public void start(String[] args) throws ParseException {
        parseOptions(args)
                .initializeDataSource()
                .migrateDatabase()
                .initializeServices()
                .createRoutes(new JsonIO());
    }

    public void stop() {
        swoopServer.stop();
    }

    private Server initializeDataSource() {
        dataSource = JdbcConnectionPool.create(dbUrl, dbUsername, dbPassword);
        return this;
    }

    private Server migrateDatabase() {
        new JdbcStoreUpdate(dbMigrations, dataSource).migrate();
        return this;
    }

    private Server initializeServices() {
        catalogViews = new CatalogViews(dataSource);
        CatalogViewsUpdater viewsUpdater = new CatalogViewsUpdater(dataSource);

        eventBus = new DefaultEventBus<VersionedDomainEvent>();
        eventBus.addListener(viewsUpdater.asListener());

        EventStore eventStore = new JdbcEventStore(dataSource);
        catalogCommandHandler = new CatalogCommandHandler(eventStore, eventBus);
        return this;
    }

    private Server createRoutes(final JsonIO jsonIO) {
        get(new Action("/about") {
            @Override
            public void handle(Request request, Response response) {
                jsonIO.writeJson(response, mapBuilderOO().with("name", "compta").build());
            }
        });

        get(new Action("/catalogs") {
            @Override
            public void handle(Request request, Response response) {
                try {
                    List<CatalogView> views = catalogViews.listCatalogs();
                    jsonIO.writeJson(response, views);
                } catch (DataAccessException e) {
                    log.error("catalog_list_error", e);

                    response.status(500);
                    jsonIO.writeJson(response, mapBuilderOO()
                            .with("error", "catalog_list_error")
                            .with("message", e.getMessage())
                            .with("stacktrace", ExceptionUtils.getStackTrace(e)).build());
                }
            }
        });

        post(new Action("/catalog") {
            @Override
            public void handle(Request request, Response response) {
                //jsonIO.readJson(request, new TypeReference<>());
                CreateCatalogCommand command;
                try {
                    command = jsonIO.readJson(request, CreateCatalogCommand.class);
                } catch (IOException e) {
                    log.error("catalog_io_error", e);

                    response.status(400);
                    jsonIO.writeJson(response, mapBuilderOO()
                            .with("error", "catalog_io_error")
                            .with("message", e.getMessage())
                            .with("stacktrace", ExceptionUtils.getStackTrace(e)).build());
                    return;
                }

                try {
                    Id catalogId = catalogCommandHandler.handle(command);
                    jsonIO.writeJson(response, mapBuilderOO()
                            .with("catalog_id", catalogId.asString()).build());
                } catch (Exception e) {
                    log.error("catalog_creation_error", e);

                    response.status(500);
                    jsonIO.writeJson(response, mapBuilderOO()
                            .with("error", "catalog_creation_error")
                            .with("message", e.getMessage())
                            .with("stacktrace", ExceptionUtils.getStackTrace(e)).build());
                }
            }
        });

        get(new Action("/catalog/:name/entries") {
            @Override
            public void handle(Request request, Response response) {
                try {
                    String catalogName = request.queryParam("name");
                    List<CatalogView> catalogs = catalogViews.findCatalogsByLabel(catalogName);
                    if (catalogs.isEmpty())
                        jsonIO.writeJson(response, mapBuilderOO().with("error", "catalog_not_found").build());
                    else if (catalogs.size() > 1)
                        jsonIO.writeJson(response, mapBuilderOO().with("error", "catalog_not_unique").build());
                    else {
                        List<CatalogEntryView> entryViews = catalogViews.getCatalogEntriesForCatalog(catalogs.get(0).getCatalogId());
                        jsonIO.writeJson(response, entryViews);
                    }
                } catch (DataAccessException e) {
                    log.error("catalog_access_error", e);

                    response.status(500);
                    jsonIO.writeJson(response, mapBuilderOO()
                            .with("error", "catalog_access_error")
                            .with("message", e.getMessage())
                            .with("stacktrace", ExceptionUtils.getStackTrace(e)).build());
                }
            }
        });
        return this;
    }

    private Server parseOptions(String[] args) throws ParseException {
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(createOptions(), args);
        if (cmd.hasOption("help")) {
            printUsage();
        }
        if (cmd.hasOption("port")) {
            String port = cmd.getOptionValue("port");
            Swoop.setPort(Integer.parseInt(port));
        }
        if (cmd.hasOption("dbMigrationsPath")) {
            this.dbMigrations = cmd.getOptionValue("dbMigrationsPath");
        }
        if (cmd.hasOption("dbUrl")) {
            this.dbUrl = cmd.getOptionValue("dbUrl");
        }
        if (cmd.hasOption("dbUsername")) {
            this.dbUsername = cmd.getOptionValue("dbUsername");
        }
        if (cmd.hasOption("dbPassword")) {
            this.dbPassword = cmd.getOptionValue("dbPassword");
        }
        return this;
    }

    private static void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("server", createOptions());
    }

    @SuppressWarnings("AccessStaticViaInstance")
    private static Options createOptions() {
        Option help = new Option("help", "print this message");
        Option port = OptionBuilder.withArgName("port")
                .hasArg()
                .withDescription("port on which the server is bound to")
                .create("port");

        Option dbUrl = OptionBuilder.withArgName("url")
                .hasArg()
                .withDescription("jdbc url to connect on the database")
                .create("dbUrl");

        Option dbMigrationsPath = OptionBuilder.withArgName("path")
                .hasArg()
                .withDescription("path to the migration scripts of the database")
                .create("dbMigrationsPath");

        Options options = new Options();
        options.addOption(help);
        options.addOption(port);
        options.addOption(dbUrl);
        options.addOption(dbMigrationsPath);
        return options;
    }

}
