package mmobots.bots;

import com.datastax.driver.core.Session;
import mmobots.mapping.Log;
import mmobots.mapping.Place;

import java.util.*;

public class Bot implements Runnable{

    private String botID;
    private int posX;
    private int posY;
    private int gold;

    private int backpackLimit;
    private int collectingSpeed;
    private int travelSpeed;

    private Session session;
    private Date timeLimit;
    private Set<Place> cities;
    private Set<Place> resources;

    public Bot(int mapSize, Date timeLimit, int backpack, int collectingSpeed, int travelSpeed, Session session){
        Random rand = new Random();

        this.timeLimit = timeLimit;
        this.session = session;
        this.gold = 0;
        this.posX = rand.nextInt(mapSize-1)+1;
        this.posY = rand.nextInt(mapSize-1)+1;
        this.botID = UUID.randomUUID().toString();
        this.backpackLimit = backpack;
        this.travelSpeed = travelSpeed;
        this.collectingSpeed = collectingSpeed;

        this.cities = new HashSet<>();
        this.resources = new HashSet<>();

        List<Place> places = Place.GetAllPlaces(session);
        for (Place p: places) {
            if (p.getType().equals(Place.TYPE_CITY)) this.cities.add(p);
            else this.resources.add(p);
        }
    }

    @Override
    public void run() {
        System.out.println("Bot "+this.botID+" running on \t"+this.posX+"\t"+this.posY);
        try {
            while (timeLimit.compareTo(new Date()) > 0) {
                if (this.gold < this.backpackLimit) {


                    this.gold += 20;
                    Thread.sleep(1000);
                } else deliverGold();
            }
            deliverGold();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Place findBestPlace() {
        // TODO implement this
        return null;
    }

    private Boolean lockPlace(Place place) {
        // TODO
        return false;
    }

    /*
        Bot travels to city and sleep. It do not insert log into logs table!
     */
    private void deliverGold() throws InterruptedException {
        if (this.gold <= 0) return;

        Double closestDistance = null;
        Place closestCity = null;

        for (Place c : this.cities) {
            double distance = calculateDistance(c);
            if (closestDistance == null || closestDistance > distance) {
                closestCity = c;
                closestDistance = distance;
            }
        }
        if (closestCity == null) return;

        System.out.println(String.format("Bot %s travelling to city %s: %d;%d", this.botID, closestCity.getId(), closestCity.getPosX(), closestCity.getPosY()));
        Thread.sleep((long)(closestDistance/this.travelSpeed*1000));
        System.out.println(String.format("Bot %s delivering %d gold to City %s", this.botID, this.gold, closestCity.getId()));
        this.gold = 0;
        this.posX = closestCity.getPosX();
        this.posY = closestCity.getPosY();
    }

    private double calculateDistance(Place p) {
        double dx = Math.abs(this.posX - p.getPosX());
        double dy = Math.abs(this.posY - p.getPosY());

        return Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));
    }

    private void createLog(String placeID, Date start, Date end, int collectedGold) {
        Log l = new Log(this.botID, start, end, placeID, collectedGold);
        l.save(this.session);
    }

    public String getBotID() {
        return botID;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }
}
