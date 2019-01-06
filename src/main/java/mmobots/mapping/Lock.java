package mmobots.mapping;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.*;

import java.util.Date;
import java.util.List;

@Accessor
interface LockAccessor {
    @Query("SELECT * FROM mmobots.locks")
    Result<Lock> getAll();
}

@Table(keyspace="mmobots", name="locks",
        readConsistency = "ONE",
        writeConsistency = "ONE")
public class Lock {

    @PartitionKey(0)
    @Column(name = "botid")
    private String botID;

    @PartitionKey(1)
    @Column(name = "place")
    private String place;

    @PartitionKey(2)
    @Column(name = "time")
    private Date time;

    @Column(name = "type")
    private String type;

    public static String TYPE_LOCK = "LOCK";
    public static String TYPE_RELEASE = "RELEASE";

    public Lock(){}

    public Lock(String botID, Date time, String place, String type){
        this.botID = botID;
        this.time = time;
        this.place = place;
        this.type = type;
    }

    public void save(Session session) {
        MappingManager manager = new MappingManager(session);
        manager.mapper(Lock.class).save(this);
    }

    public static List<Lock> GetAllLocks(Session session) {
        MappingManager manager = new MappingManager(session);
        LockAccessor lockAccessor = manager.createAccessor(LockAccessor.class);
        return lockAccessor.getAll().all();
    }

    public String getBotID() {
        return botID;
    }

    public Date getTime() {
        return time;
    }

    public String getPlace() {
        return place;
    }

    public String getType() {
        return type;
    }
}
