package config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class PropertiesGetter {

    private Properties prop;


    public PropertiesGetter(String resource) throws IOException {
        this.prop = new Properties();

        String propFileName = resource;
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

        try {
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } finally {
            if (inputStream != null) inputStream.close();
        }


    }


    public Properties getProperties() {
        return prop;
    }

    public String getProperty(String name) {
        return this.prop.getProperty(name);
    }

    public String[] getNodesList() {
        String nodes = this.prop.getProperty("nodes");
        return nodes.split(",");
    }

    public int getBotsNumber() {
        return Integer.parseInt(this.prop.getProperty("botsNumber"));
    }

    public int getMapSize() {
        return Integer.parseInt(this.prop.getProperty("mapSize"));
    }

    public Date getTimeLimit() {
        String time = this.prop.getProperty("collectingTime");
        int seconds = Integer.parseInt(time);
        Date date = new Date();
        date.setTime(date.getTime() + seconds*1000);
        return date;
    }

    public int getBackpackLimit() {
        return Integer.parseInt(this.prop.getProperty("backpackLimit"));
    }

    public int getCollectingSpeed() {
        return Integer.parseInt(this.prop.getProperty("collectingSpeed"));
    }

    public int getTravelSpeed() {
        return Integer.parseInt(this.prop.getProperty("travelSpeed"));
    }

    public double getWaitingTime() { return  Double.parseDouble(this.prop.getProperty("waitingTime")); }

}
