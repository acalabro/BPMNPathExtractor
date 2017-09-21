package it.cnr.isti.labsedc.bpmnpathextractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BPMNProperties {

    private Properties properties;

    public BPMNProperties() {

        properties = new Properties();
        InputStream inputStream = getClass().getResourceAsStream("/bpmnpathextractor.properties");

        try {
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getProperty(String key) { return properties.getProperty(key); }

}
