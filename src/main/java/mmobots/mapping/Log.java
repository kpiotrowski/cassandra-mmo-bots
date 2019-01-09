package mmobots.mapping;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.OperationTimedOutException;
import com.datastax.driver.core.exceptions.ReadTimeoutException;
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

    @PartitionKey
    @Column(name = "botid")
    private String botID;

    @Column(name = "start")
    private Date start;

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

    public void save(MappingManager manager, Request requestCount) {
        requestCount.addValue(1);
        while (true){
            try{
                manager.mapper(Log.class).save(this);
                break;
            } catch (Exception e){
                System.err.println("Timeout, trying again");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException interupt){
                    interupt.printStackTrace();
                }
            }
        }
    }

    public static List<Log> GetAllLogs(MappingManager manager, Request requestCount) {
        requestCount.addValue(1);
        while (true){
            try {
                LogAccessor logAccessor = manager.createAccessor(LogAccessor.class);
                return logAccessor.getAll().all();
            } catch (Exception e){
                System.err.println("Timeout, trying again");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException interupt){
                    interupt.printStackTrace();
                }
            }
        }
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
