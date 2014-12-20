package util.application;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.InputStream;
import java.util.Properties;

@Stateless
public class BackendServiceBean {

    private static final String P_PROPERTIES = "catalog.properties";

    private Properties properties;

    @PostConstruct
    private void init() {
        InputStream propertiesResource = null;
        try {
            propertiesResource = getClass().getClassLoader().getResourceAsStream(P_PROPERTIES);
            properties = new Properties();
            properties.load(propertiesResource);
        } catch (Exception e) {
            // TODO: logger
        } finally {
            if (propertiesResource != null) {
                try {
                    propertiesResource.close();
                } catch (Exception e) {
                    // TODO: logger
                }
            }
        }
    }

    public Properties getProperties() {
        return properties;
    }
}