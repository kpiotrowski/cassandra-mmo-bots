package mmobots.mapping;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.ReadTimeoutException;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.*;
import mmobots.bots.Bot;


@Accessor
interface RequestAccessor {
    @Query("UPDATE mmobots.requests SET requests = requests + ? WHERE botid = ?")
    void update(Long value, String botId);
}

@Table(keyspace="mmobots", name="requests",
        readConsistency = "ONE",
        writeConsistency = "ONE")
public class Request {
    @PartitionKey
    @Column(name = "botid")
    private String botID;

    @Column(name = "requests")
    private Long requests;

    public Request(){}

    public Request(String botID, MappingManager manager) {
        this.botID = botID;
        Request r = null;
        while (true){
            try {
                r = manager.mapper(Request.class).get(botID);
            } catch (ReadTimeoutException e){
                System.err.println("Timeout, trying again");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException interupt){
                    interupt.printStackTrace();
                }
            }
            break;
        }
        if (r != null) this.requests = r.getRequests();
        if (this.requests == null) this.requests = 0L;
    }

    public String getBotID() {
        return botID;
    }

    public Long getRequests() {
        return requests;
    }

    public void addValue(int value){
        this.requests += value;
    }

    public void UpdateCounter(Long value, Bot bot, MappingManager manager){
        RequestAccessor requestAccessor = manager.createAccessor(RequestAccessor.class);
        requestAccessor.update(value,bot.getBotID());
    }
}
