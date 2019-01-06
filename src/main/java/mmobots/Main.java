package mmobots;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import config.PropertiesGetter;
import mmobots.mapping.Log;
import mmobots.mapping.Place;
import mmobots.mapping.Request;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        PropertiesGetter props = new PropertiesGetter();
        String[] nodes = props.getNodesList();

        try (Cluster cluster = Cluster.builder()
                .addContactPoints(nodes)
                .build()) {
            Session session = cluster.connect();

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
