package config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class PropertiesGetter {

    private Properties prop;


    public PropertiesGetter() throws IOException {
        this.prop = new Properties();

        String propFileName = "config.properties";
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
}
