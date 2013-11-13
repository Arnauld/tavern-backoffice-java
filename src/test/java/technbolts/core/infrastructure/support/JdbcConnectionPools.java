package technbolts.core.infrastructure.support;

import org.h2.jdbcx.JdbcConnectionPool;
import technbolts.core.infrastructure.Disposable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class JdbcConnectionPools {
    private static AtomicInteger idGen = new AtomicInteger();

    public static String acquireUrl() {
        return "jdbc:h2:mem:test" + idGen.incrementAndGet();
    }

    public static JdbcConnectionPool acquire() {
        return acquire(acquireUrl());
    }

    public static JdbcConnectionPool acquire(String url) {
        return JdbcConnectionPool.create(url, "sa", "sa");
    }

    public static Disposable toDisposable(final JdbcConnectionPool pool) {
        return new Disposable() {
            @Override
            public void dispose() {
                pool.dispose();
            }
        };
    }
}
