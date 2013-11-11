package technbolts.compta.server;

import org.apache.commons.cli.*;
import org.h2.jdbcx.JdbcConnectionPool;
import swoop.Action;
import swoop.Request;
import swoop.Response;
import swoop.Swoop;
import technbolts.compta.price.PriceCatalog;
import technbolts.core.infrastructure.*;
import technbolts.core.infrastructure.support.JdbcEventStore;
import technbolts.core.infrastructure.support.JdbcStoreUpdate;

import javax.sql.DataSource;

import static swoop.Swoop.get;
import static technbolts.core.util.MapBuilder.mapBuilderOO;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class Server {


    public static void main(String[] args) throws ParseException {
        new Server()
                .parseOptions(args)
                .initializeDataSource()
                .initializeEventStore()
                .initializeServices()
                .createRoutes(new JsonIO());
    }


    private String dbUrl = "jdbc:h2:~/.compta/db";
    private String dbUsername = "sa";
    private String dbPassword = "sa";
    private Id priceCatalogId = Id.create("price-catalog-001");
    //
    private EventStore entriesStore;
    private EventStore catalogStore;
    private DataSource dataSource;

    public Server() {
    }

    private Server initializeDataSource() {
        dataSource = JdbcConnectionPool.create(dbUrl, dbUsername, dbPassword);
        return this;
    }

    private Server initializeEventStore() {
        new JdbcStoreUpdate("sql/h2", dataSource).migrate();
        return this;
    }

    private Server initializeServices() {
        catalogStore = new JdbcEventStore(dataSource, "catalogs");
        entriesStore = new JdbcEventStore(dataSource, "catalog_entries");
        createCatalogIfMissing();
        return this;
    }

    private void createCatalogIfMissing() {

    }


    private Server createRoutes(final JsonIO jsonIO) {
        get(new Action("/about") {
            @Override
            public void handle(Request request, Response response) {
                jsonIO.writeJson(response, mapBuilderOO().with("name", "compta").build());
            }
        });

        get(new Action("/price/all") {
            @Override
            public void handle(Request request, Response response) {

            }
        });

        get(new Action("/catalog/:name/all") {
            @Override
            public void handle(Request request, Response response) {
                String catalogName = request.queryParam("name");
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

        Options options = new Options();
        options.addOption(help);
        options.addOption(port);
        return options;
    }
}
