package mmobots.models;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.Date;

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

    public Log() {}

    public Log(String botID, Date start, Date end, String place) {
        this.botID = botID;
        this.start = start;
        this.end = end;
        this.place = place;
    }

    public void save(Session session) {
        MappingManager manager = new MappingManager(session);
        manager.mapper(Log.class).save(this);
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
}
