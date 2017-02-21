package ee.ttu.idu0080.raamatupood.server;

import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.Logger;

public final class EmbeddedBroker {
    private static final Logger log = Logger.getLogger(EmbeddedBroker.class);
    public static final String PORT = "61618";
    public static final String URL = "tcp://localhost:" + PORT;

    private EmbeddedBroker() {
    }

    public static void main(String[] args) throws Exception {
        BrokerService broker = new BrokerService();
        broker.setBrokerName("JMS_BROKER");
        broker.addConnector(URL);
        broker.start();
        log.info("Start JMS Broker on " + URL);
        
        Object lock = new Object();
        synchronized (lock) {
            lock.wait();
        }
    }
}
