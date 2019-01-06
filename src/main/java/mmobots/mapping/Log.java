package mmobots.mapping;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.*;

import java.util.Date;
import java.util.List;

@Accessor
interface LogAccessor {
    @Query("SELECT * FROM mmobots.logs")
    Result<Log> getAll();
}

@Table(keyspace="mmobots", name="logs",
        readConsistency = "ONE",
        writeConsistency = "ONE")
public class Log {

    @PartitionKey(0)
    @Column(name = "botid")
    private String botID;

    @PartitionKey(1)
    @Column(name = "start")
    private Date start;

    @PartitionKey(2)
    @Column(name = "end")
    private Date end;

    @Column(name = "place")
    private String place;

    @Column(name = "gold")
    private int gold;

    public Log() {}

    public Log(String botID, Date start, Date end, String place, int gold) {
        this.botID = botID;
        this.start = start;
        this.end = end;
        this.place = place;
        this.gold = gold;
    }

    public void save(Session session) {
        MappingManager manager = new MappingManager(session);
        manager.mapper(Log.class).save(this);
    }

    public static List<Log> GetAllLogs(Session session) {
        MappingManager manager = new MappingManager(session);
        LogAccessor logAccessor = manager.createAccessor(LogAccessor.class);
        return logAccessor.getAll().all();
    }

    public String getBotID() {
        return botID;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public String getPlace() {
        return place;
    }

    public int getGold() {
        return gold;
    }
}
