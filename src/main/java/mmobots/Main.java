package mmobots;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import config.PropertiesGetter;
import mmobots.bots.Bot;
import mmobots.mapping.Log;
import mmobots.mapping.Place;
import mmobots.mapping.Request;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        BasicConfigurator.configure();
        PropertiesGetter props = new PropertiesGetter();
        String[] nodes = props.getNodesList();
        int botsNumber = props.getBotsNumber();
        int mapSize = props.getMapSize();

        try (Cluster cluster = Cluster.builder()
                .addContactPoints(nodes)
                .build()) {

            Session session = cluster.connect();

            Thread[] threads = new Thread[botsNumber];
            Bot[] bots = new Bot[botsNumber];
            for (int i = 0; i < botsNumber; i++) {
                bots[i] = new Bot(
                        mapSize,props.getTimeLimit(),
                        props.getBackpackLimit(), props.getCollectingSpeed(), props.getTravelSpeed(), session);
                threads[i] = new Thread(bots[i]);
                threads[i].start();
            }

            for (int i = 0; i < botsNumber; i++) {
                threads[i].join();
            }

            // SOME TEST CODE TO DELETE
            Request r = new Request("B1", session);
            r.increment(session);

            Log l = new Log("B10", new Date(), new Date(), "PlaceX");
            l.save(session);

            System.out.println("Hello Bots" + r.getRequests());

            List<Place> places = Place.GetAllPlaces(session);
            places.forEach(v -> System.out.println(v.getId()));

        }
    }
}
