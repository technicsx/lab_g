package pc.vadym.configurator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropsLoader {
    private static PropsLoader instance;
    public Properties props;
    private PropsLoader() {
        Properties configuration = new Properties();
        InputStream inputStream = PropsLoader.class
                .getClassLoader()
                .getResourceAsStream("application.properties");
        try {
            configuration.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.props = configuration;
    }

    public static PropsLoader getInstance() {
        if (instance == null) {
            instance = new PropsLoader();
        }
        return instance;
    }
}
