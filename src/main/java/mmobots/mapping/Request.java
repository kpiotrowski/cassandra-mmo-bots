package mmobots.mapping;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

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

    public Request(String botID, Session session) {
        this.botID = botID;

        MappingManager manager = new MappingManager(session);

        Request r = manager.mapper(Request.class).get(botID);
        if (r != null) {
            this.requests = r.getRequests();
        } else this.requests = 0L;
    }

    public String getBotID() {
        return botID;
    }

    public Long getRequests() {
        return requests;
    }

    public void setRequests(Long requests) {
        this.requests = requests;
    }

    public void increment(Session session){
        this.requests ++;
        session.execute("UPDATE mmobots.requests SET requests = requests + 1 WHERE botid = '"+this.botID+"'").wasApplied();
    }
}