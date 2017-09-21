package it.cnr.isti.labsedc.bpmnpathextractor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class BPMNProperties {

    private Properties properties;

    public BPMNProperties() {

        properties = new Properties();

        try {
            FileInputStream fileInputStream =
                    new FileInputStream(System.class.getResource("/bpmnpathextractor.properties").getFile());
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getProperty(String key) { return properties.getProperty(key); }

}
