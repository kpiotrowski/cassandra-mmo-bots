package mmobots;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import config.PropertiesGetter;
import mmobots.models.Log;
import mmobots.models.Request;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        PropertiesGetter props = new PropertiesGetter();
        String[] nodes = props.getNodesList();

        Cluster cluster = null;
        try{
            cluster = Cluster.builder()
                    .addContactPoints(nodes)
                    .build();
            Session session = cluster.connect();

            Request r = new Request("B1", session);
            r.increment(session);

            Log l = new Log("B10", new Date(), new Date(), "PlaceX");
            l.save(session);

            System.out.println("Hello Bots" + r.getRequests());

        } finally {
            if (cluster != null) cluster.close();                                          // (5)
        }
    }
}
