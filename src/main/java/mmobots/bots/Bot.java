package mmobots.bots;

public class Bot implements Runnable{

    private String botID;
    private int posX;
    private int posY;
    private int gold;

    public Bot(String botID){
        this.botID = botID;
    }

    @Override
    public void run() {

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
