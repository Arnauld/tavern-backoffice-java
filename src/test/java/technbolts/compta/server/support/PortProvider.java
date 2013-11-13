package technbolts.compta.server.support;

import swoop.util.New;

import java.util.concurrent.LinkedBlockingQueue;

public class PortProvider {
    private static LinkedBlockingQueue<Integer> availables = New.linkedBlockingQueue();
    static {
        for(int i=0;i<100;i++)
            availables.add(4567 + i);
    }
    
    /**
     * Retrieves and removes the head of this queue, waiting if necessary until an element becomes available.
     */
    public static Integer acquire() throws InterruptedException {
        return availables.take();
    }
    
    /**
     */
    public static void release(Integer port) throws InterruptedException {
        availables.put(port);
    }
}
