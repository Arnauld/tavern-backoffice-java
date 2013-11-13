package technbolts.compta.price.view;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import technbolts.core.infrastructure.DataAccessException;
import technbolts.core.infrastructure.Id;
import technbolts.core.infrastructure.support.JdbcDisposables;
import technbolts.core.infrastructure.support.JdbcExecutor;
import technbolts.core.infrastructure.support.JdbcRunnable;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CatalogViews {

    private Logger logger = LoggerFactory.getLogger(CatalogViews.class);

    private final DataSource dataSource;
    private final JdbcExecutor executor;

    public CatalogViews(DataSource dataSource) {
        this.dataSource = dataSource;
        this.executor = new JdbcExecutor(dataSource);
    }

    public List<CatalogView> findCatalogsByLabel(final String searchLabel) throws DataAccessException {
        return executor.executeWithinTransaction(new JdbcRunnable<List<CatalogView>>() {
            @Override
            public List<CatalogView> execute(Connection connection, JdbcDisposables disposables) throws SQLException {
                String sql = "SELECT catalog_id, version, label FROM catalog_views WHERE label LIKE ?";
                PreparedStatement pStmt = disposables.push(connection.prepareStatement(sql));
                pStmt.setString(1, searchLabel.replace("*", "%"));
                ResultSet resultSet = disposables.push(pStmt.executeQuery());

                List<CatalogView> views = Lists.newArrayList();
                while (resultSet.next()) {
                    CatalogView catalogView = readCatalog(resultSet);
                    views.add(catalogView);
                }
                return views;
            }
        });
    }

    private CatalogView readCatalog(ResultSet resultSet) throws SQLException {
        Id id = Id.create(resultSet.getString(1));
        long version = resultSet.getLong(2);
        String label = resultSet.getString(3);
        return new CatalogView(id, version, label);
    }

    public List<CatalogEntryView> getCatalogEntriesForCatalog(final Id catalogId) throws DataAccessException {
        return executor.executeWithinTransaction(new JdbcRunnable<List<CatalogEntryView>>() {
            @Override
            public List<CatalogEntryView> execute(Connection connection, JdbcDisposables disposables) throws SQLException {
                String sql = "SELECT entry_id, version, label, price FROM catalog_entry_views WHERE catalog_id LIKE ?";
                PreparedStatement pStmt = disposables.push(connection.prepareStatement(sql));
                pStmt.setString(1, catalogId.asString());
                ResultSet resultSet = disposables.push(pStmt.executeQuery());

                List<CatalogEntryView> views = Lists.newArrayList();
                while (resultSet.next()) {
                    Id id = Id.create(resultSet.getString(1));
                    long version = resultSet.getLong(2);
                    String label = resultSet.getString(3);
                    BigDecimal price = resultSet.getBigDecimal(4);
                    views.add(new CatalogEntryView(catalogId, id, version, label, price));
                }
                return views;
            }
        });
    }

    public List<CatalogView> listCatalogs() throws DataAccessException {
        return executor.executeWithinTransaction(new JdbcRunnable<List<CatalogView>>() {
            @Override
            public List<CatalogView> execute(Connection connection, JdbcDisposables disposables) throws SQLException {
                String sql = "SELECT catalog_id, version, label FROM catalog_views ORDER BY label";
                PreparedStatement pStmt = disposables.push(connection.prepareStatement(sql));
                ResultSet resultSet = disposables.push(pStmt.executeQuery());

                List<CatalogView> views = Lists.newArrayList();
                while (resultSet.next()) {
                    CatalogView catalogView = readCatalog(resultSet);
                    views.add(catalogView);
                }
                return views;
            }
        });
    }
}
