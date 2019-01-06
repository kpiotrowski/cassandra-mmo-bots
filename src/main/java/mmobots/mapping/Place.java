package mmobots.mapping;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Query;
import com.datastax.driver.mapping.annotations.Table;

import java.util.List;

@Accessor
interface PlaceAccessor {
    @Query("SELECT * FROM mmobots.places")
    Result<Place> getAll();
}


@Table(keyspace="mmobots", name="places",
        readConsistency = "ONE",
        writeConsistency = "ONE")
public class Place {

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

    public static String TYPE_CITY = "CITY";
    public static String TYPE_RESOURCE = "RESOURCE";

    public static List<Place> GetAllPlaces(Session session) {
        MappingManager manager = new MappingManager(session);

        PlaceAccessor placeAccessor = manager.createAccessor(PlaceAccessor.class);
        return placeAccessor.getAll().all();
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
}
