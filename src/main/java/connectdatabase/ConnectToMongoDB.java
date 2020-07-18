package connectdatabase;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import console.ConsoleHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectToMongoDB {
    public MongoClient mongoClient;
    public MongoDatabase db;
    public static final String PATH_TO_PROPERTIES = new File("").getAbsolutePath() + "\\settings.properties";
    ConsoleHelper ch = new ConsoleHelper();

    public ConnectToMongoDB() {
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
        Properties prop = null;
        try {
            prop = getProperties();
        } catch (IOException e) {
            ch.showMessage("Ошибка чтения файла настроек: " + e.getMessage());
        }
        try {
            String strURL = "mongodb://" + prop.getProperty("user") + ":" + prop.getProperty("password") + "@" + prop.getProperty("host")
                    + ":" + prop.getProperty("port") + "/" + prop.getProperty("dbname");
            // Создаем подключение
            mongoClient = MongoClients.create(strURL);
            db = mongoClient.getDatabase(prop.getProperty("dbname"));
        } catch (Exception e) {
            // Если возникли проблемы при подключении сообщаем об этом
            ch.showMessage("Ошибка подключения к базе данных: " + e.getMessage());
        }
    }

    private Properties getProperties() throws IOException {
        Properties prop = new Properties();
        FileInputStream fileInputStream = new FileInputStream(PATH_TO_PROPERTIES);
        prop.load(fileInputStream);
        return prop;
    }
}
