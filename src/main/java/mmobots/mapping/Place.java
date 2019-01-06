package mmobots.mapping;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.*;

import java.util.List;
import java.util.Set;

@Accessor
interface PlaceAccessor {
    @Query("SELECT * FROM mmobots.places")
    Result<Place> getAll();
}

@Accessor
interface CityAccessor {
    @Query("SELECT * FROM mmobots.places WHERE type = 'CITY' ALLOW FILTERING")
    Result<Place> getAll();
}


@Table(keyspace="mmobots", name="places",
        readConsistency = "ONE",
        writeConsistency = "ONE")
public class Place {

    @PartitionKey
    @Column(name = "id")
    private String id;

    @Column(name = "posx")
    private int posX;

    @Column(name = "posy")
    private int posY;

    @Column(name = "type")
    private String type;

    @Column(name = "gold")
    private int gold;

    @Column(name = "locks")
    private List<String> locks;

    @Column(name = "bots")
    private Set<String> bots;

    public static String TYPE_CITY = "CITY";
    public static String TYPE_RESOURCE = "RESOURCE";

    public static List<Place> GetAllPlaces(Session session) {
        MappingManager manager = new MappingManager(session);

        PlaceAccessor placeAccessor = manager.createAccessor(PlaceAccessor.class);
        return placeAccessor.getAll().all();
    }

    public static List<Place> GetAllCities(Session session) {
        MappingManager manager = new MappingManager(session);

        CityAccessor cityAccessor = manager.createAccessor(CityAccessor.class);
        return cityAccessor.getAll().all();
    }

    /*
        Updates gold with conditional
     */
    public void updateGold(int valueChange, Session session) {
        String query = String.format("UPDATE mmobots.places SET gold = %d WHERE id = '%s' IF gold = %d",
                this.gold+valueChange,
                this.id,
                this.gold);

        boolean applied = session.execute(query).wasApplied();
        if (applied) this.gold += valueChange;
        else {
            MappingManager manager = new MappingManager(session);

            Place p = manager.mapper(Place.class).get(this.id);
            if (p != null) {
                this.gold = p.gold;
                this.updateGold(valueChange, session);
            }
        }
    }


    public String getId() {
        return id;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public String getType() {
        return type;
    }

    public int getGold() {
        return gold;
    }

    public List<String> getLocks() {
        return locks;
    }

    public Set<String> getBots() {
        return bots;
    }
}
