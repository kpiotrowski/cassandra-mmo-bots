package mmobots.bots;

import com.datastax.driver.mapping.MappingManager;
import mmobots.mapping.Lock;
import mmobots.mapping.Log;
import mmobots.mapping.Place;
import mmobots.mapping.Request;

import java.util.*;
import java.util.stream.Collectors;

public class Bot implements Runnable{
    private String botID;
    private int posX;
    private int posY;
    private int gold;

    private int backpackLimit;
    private int collectingSpeed;
    private int travelSpeed;

    private MappingManager manager;
    private Date timeLimit;
    private Set<Place> cities;
    private Set<Place> resources;

    private Request request;

    private double waitingTime;


    public Bot(int mapSize, Date timeLimit, int backpack, int collectingSpeed, int travelSpeed, MappingManager manager, double waitingTime){
        Random rand = new Random();

        this.waitingTime = waitingTime;
        this.timeLimit = timeLimit;
        this.manager = manager;
        this.gold = 0;
        this.posX = rand.nextInt(mapSize-1)+1;
        this.posY = rand.nextInt(mapSize-1)+1;
        this.botID = UUID.randomUUID().toString();
        this.backpackLimit = backpack;
        this.travelSpeed = travelSpeed;
        this.collectingSpeed = collectingSpeed;

        this.cities = new HashSet<>();
        this.resources = new HashSet<>();
        this.request = new Request(this.botID,this.manager);
        List<Place> places = Place.GetAllPlaces(this.manager,this.request);
        for (Place p: places) {
            if (p.getType().equals(Place.TYPE_CITY)) this.cities.add(p);
            else this.resources.add(p);
        }
    }

    @Override
    public void run() {
        System.out.println("Bot "+this.botID+" running on \t"+this.posX+"\t"+this.posY);
        try {
            int previousPlace = 0;
            List<Place> rankingPlaces = null;
            while (timeLimit.compareTo(new Date()) > 0) {
                if (this.gold < this.backpackLimit) {
                    if (previousPlace == 0) rankingPlaces = rankPlaces();
                    if (rankingPlaces.size() == 0) {
                        System.out.println(String.format("No more gold, bot %s stop running",this.botID));
                        break; // No gold left - bot run stop
                    }
                    Place foundPlace = null;
                    int i;
                    for (i = previousPlace; i < rankingPlaces.size() && foundPlace == null; i++) {
                        Place place = rankingPlaces.get(i);
                        int result = checkPlaceLocked(place);
                        switch (result){
                            case 0: // Lock place
                                this.lockPlace(place);
                                foundPlace = place;
                                break;
                            case 1: // Sync missing
                                Thread.sleep((long)this.waitingTime * 1000); // Wait for sync
                                i--;
                                break;
                        }
                    }
                    if (foundPlace == null) {
                        Thread.sleep((long)this.waitingTime * 1000); // Wait for places to free
                        previousPlace = 0;
                        continue; // Continue if no place found
                    }
                    int result = 0;
                    do{
                        Thread.sleep((long)this.waitingTime * 1000); // Wait for sync
                        result = this.checkPlaceLocked(foundPlace);
                    } while (result == 0); // Partition problem

                    int remainingGold = 0;
                    switch (result){
                        case 1: // Check remaining gold in place
                            Map<Place, Integer> remainingGoldPlaces = getPlacesRemainingGold(Log.GetAllLogs(this.manager,this.request));
                            remainingGold = remainingGoldPlaces.getOrDefault(foundPlace,0);
                            if ( remainingGold == 0 ){
                                System.out.println(String.format("No gold left, bot $s resign from %s place",this.botID, foundPlace.getId()));
                                this.releasePlace(foundPlace);
                                foundPlace = null;
                            }
                            break;
                        case 2:
                            System.out.println(String.format("Bot $s resign from %s place",this.botID, foundPlace.getId()));
                            this.releasePlace(foundPlace);
                            foundPlace = null;
                            break;
                    }
                    if (foundPlace == null) {
                        previousPlace = i;
                        continue;
                    }
                    this.collectGold(foundPlace,remainingGold);
                    previousPlace = 0;
                } else deliverGold();
            }
            deliverGold();
            this.request.UpdateCounter(this.request.getRequests(),this,this.manager);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<Place> rankPlaces() {
        List<Log> allLogs = Log.GetAllLogs(this.manager,this.request);
        Map<Place, Integer> placesRemainingGold = getPlacesRemainingGold(allLogs);
        List<PlaceValue> placeValueList = new LinkedList<>();
        for(Map.Entry<Place, Integer> entry : placesRemainingGold.entrySet()) {
            Place place = entry.getKey();
            double distance = calculateDistance(place);
            double placeValue = (double) Math.min(entry.getValue(),this.backpackLimit-this.gold) / ((double) distance/this.travelSpeed);
            placeValueList.add(new PlaceValue(placeValue,place));
        }
        Collections.sort(placeValueList);

        List<Place> ranking = new LinkedList<>();
        for (PlaceValue p: placeValueList) {
            ranking.add(p.place);
        }
        return ranking;
    }

    private Map<Place, Integer> getPlacesRemainingGold(List<Log> allLogs) {
        Map<String,Integer> placesTakenGold = new HashMap<>();
        for (Log log: allLogs) {
            int takenGold = placesTakenGold.getOrDefault(log.getPlace(),0);
            takenGold += log.getGold();
            placesTakenGold.put(log.getPlace(),takenGold);
        }
        Map<Place, Integer> placesRemainingGold = new HashMap<>();
        for (Place place:resources) {
            int remainingGold = place.getGold() - placesTakenGold.getOrDefault(place.getId(),0);
            if (remainingGold <= 0) continue;
            placesRemainingGold.put(place,remainingGold);
        }
        return placesRemainingGold;
    }

    private void lockPlace(Place place) {
        Lock l = new Lock(this.botID, new Date(), place.getId(), Lock.TYPE_LOCK);
        l.save(this.manager,this.request);
    }

    /*
        Returns 0 if place is not locked
        Returns 1 if place is locked by this bot
        Returns 2 if place is locked by someone else
     */
    private int checkPlaceLocked(Place place) {
        List<Lock> allLocks = Lock.GetAllLocks(this.manager,this.request);
        Map<String, List<Lock>> placeLocks = allLocks
                .stream()
                .filter(l -> l.getPlace().equals(place.getId()))
                .collect(
                        Collectors.groupingBy(Lock::getBotID)
                );

        boolean lockedByThisBot = placeLocks.get(this.botID) != null && placeLocks.get(this.botID).stream().mapToInt(Lock::typeValue).sum() > 0;

        for (Map.Entry<String, List<Lock>> entry : placeLocks.entrySet()) {
            int lockSummary = entry.getValue().stream().mapToInt(Lock::typeValue).sum();
            if (lockSummary <= 0) continue;

            if (!entry.getKey().equals(this.botID)) {
                if (!lockedByThisBot) return 2;

                Lock maxLock = Collections.max(entry.getValue(), Comparator.comparing(l -> l.getTime().getTime()));
                Lock maxBotLock = Collections.max(placeLocks.get(this.botID), Comparator.comparing(l -> l.getTime().getTime()));

                if (maxLock.getTime().getTime() < maxBotLock.getTime().getTime()) return 2;
            }
        }
        return lockedByThisBot ? 1 : 0;
    }

    private void releasePlace(Place place){
        Lock l = new Lock(this.botID, new Date(), place.getId(), Lock.TYPE_RELEASE);
        l.save(this.manager, this.request);
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

    private void collectGold(Place p, int remainingGold) throws InterruptedException {
        System.out.println(String.format("Bot %s travelling to resource %s: %d;%d", this.botID, p.getId(), p.getPosX(), p.getPosY()));
        double distance = calculateDistance(p);
        Thread.sleep((long)(distance/this.travelSpeed*1000));
        this.posX = p.getPosX();
        this.posY = p.getPosY();

        int goldToCollect = Math.min(remainingGold,this.backpackLimit - this.gold);
        double collectingTime = (double) goldToCollect/this.collectingSpeed;
        Date start = new Date();
        System.out.println(String.format("Bot %s collecting %d gold from resource %s: %d;%d", this.botID, goldToCollect, p.getId(), p.getPosX(), p.getPosY()));
        Thread.sleep((long)collectingTime*1000);
        Date end = new Date();
        end.setTime(start.getTime() + (long)collectingTime*1000);
        this.gold += goldToCollect;
        createLog(p.getId(), start, end, goldToCollect);
        releasePlace(p);
    }

    private double calculateDistance(Place p) {
        double dx = Math.abs(this.posX - p.getPosX());
        double dy = Math.abs(this.posY - p.getPosY());

        return Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));
    }

    private void createLog(String placeID, Date start, Date end, int collectedGold) {
        Log l = new Log(this.botID, start, end, placeID, collectedGold);
        l.save(this.manager, this.request);
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

    private class PlaceValue implements Comparable<PlaceValue> {
        private Double value;
        private Place place;

        public PlaceValue(double value, Place place) {
            this.value = value;
            this.place = place;
        }

        public double getValue() {
            return value;
        }

        public Place getPlace() {
            return place;
        }

        @Override
        public int compareTo(PlaceValue o) {
            return o.value.compareTo(this.value);
        }
    }

}
