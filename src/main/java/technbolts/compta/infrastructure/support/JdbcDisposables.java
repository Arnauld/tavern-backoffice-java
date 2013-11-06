package technbolts.compta.infrastructure.support;

import com.google.common.collect.Lists;
import technbolts.compta.infrastructure.Disposable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static technbolts.compta.infrastructure.support.JdbcUtils.closeQuietly;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class JdbcDisposables implements Disposable {

    private List<Disposable> disposables = Lists.newArrayList();

    public Connection push(Connection what) {
        disposables.add(toDisposable(what));
        return what;
    }

    public PreparedStatement push(PreparedStatement what) {
        disposables.add(toDisposable(what));
        return what;
    }

    public ResultSet push(ResultSet what) {
        disposables.add(toDisposable(what));
        return what;
    }

    @Override
    public void dispose() {
        for (Disposable disposable : disposables)
            disposable.dispose();
    }

    public static Disposable toDisposable(final Connection connection) {
        return new Disposable() {
            @Override
            public void dispose() {
                closeQuietly(connection);
            }
        };
    }

    public static Disposable toDisposable(final Statement statement) {
        return new Disposable() {
            @Override
            public void dispose() {
                closeQuietly(statement);
            }
        };
    }

    public static Disposable toDisposable(final ResultSet resultSet) {
        return new Disposable() {
            @Override
            public void dispose() {
                closeQuietly(resultSet);
            }
        };
    }
}
