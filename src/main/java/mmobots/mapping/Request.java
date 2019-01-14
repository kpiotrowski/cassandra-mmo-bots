package mmobots.mapping;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.OperationTimedOutException;
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
    private int requests;

    public Request(){}

    public Request(String botID, MappingManager manager) {
        this.botID = botID;
//        Request r = null;
//        while (true){
//            try {
//                r = manager.mapper(Request.class).get(botID);
//                break;
//            } catch (Exception e){
//                System.err.println("Timeout get request, trying again");
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException interupt){
//                    interupt.printStackTrace();
//                }
//            }
//        }
//        if (r != null) this.requests = r.getRequests();
        this.requests = 0;
    }

    public String getBotID() {
        return botID;
    }

    public int getRequests() {
        return requests;
    }

    public void addValue(int value){
        this.requests += value;
    }

    public void UpdateCounter(int value, Bot bot, MappingManager manager){
        while (true){
            try{
                manager.mapper(Request.class).save(this);
                break;
            }catch (Exception e){
                System.err.println("Timeout save request, trying again");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException interupt){
                    interupt.printStackTrace();
                }
            }
        }
    }
}
