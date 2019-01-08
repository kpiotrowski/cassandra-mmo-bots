package mmobots;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import config.PropertiesGetter;
import mmobots.bots.Bot;
import mmobots.mapping.Place;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        BasicConfigurator.configure();

        String propFileName = "config.properties";
        if (args.length > 0) propFileName = args[0];
        PropertiesGetter props = new PropertiesGetter(propFileName);
        String[] nodes = props.getNodesList();
        int botsNumber = props.getBotsNumber();
        int mapSize = props.getMapSize();
        double waitingTime = props.getWaitingTime();

        try (Cluster cluster = Cluster.builder()
                .addContactPoints(nodes)
                .build()) {

            Session session = cluster.connect();
            MappingManager manager = new MappingManager(session);
            Thread[] threads = new Thread[botsNumber];
            Bot[] bots = new Bot[botsNumber];
            for (int i = 0; i < botsNumber; i++) {
                bots[i] = new Bot(
                        mapSize,props.getTimeLimit(),
                        props.getBackpackLimit(), props.getCollectingSpeed(), props.getTravelSpeed(), manager,waitingTime);
                threads[i] = new Thread(bots[i]);
                threads[i].start();
            }

            for (int i = 0; i < botsNumber; i++) {
                threads[i].join();
            }
        }
    }
}
