package technbolts.compta.server.support;

import swoop.server.SwoopServer;
import swoop.server.SwoopServerListenerAdapter;

import java.util.concurrent.CountDownLatch;

public class SwoopServerCountDownOnceStartedListener extends SwoopServerListenerAdapter {

    private CountDownLatch latch;
    
    public SwoopServerCountDownOnceStartedListener(CountDownLatch latch) {
        this.latch = latch;
    }
    
    @Override
    public void serverStarted(SwoopServer server) {
        latch.countDown();
    }
}
