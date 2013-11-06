package technbolts.compta.infrastructure.support;

import org.h2.jdbcx.JdbcConnectionPool;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class JdbcConnectionPools {
    private static AtomicInteger idGen = new AtomicInteger();

    public static JdbcConnectionPool acquire() {
        return acquire("jdbc:h2:mem:test" + idGen.incrementAndGet());
    }

    public static JdbcConnectionPool acquire(String url) {
        return JdbcConnectionPool.create(url, "sa", "sa");
    }
}
