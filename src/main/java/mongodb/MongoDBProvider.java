package mongodb;


import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoDatabase;

public class MongoDBProvider {
    private final MongoDatabase db;
//    private final static int port = 27017; // Default port for mongo

    public MongoDBProvider(String dbName, int port) {
        this.db = MongoClients.create("mongodb://localhost:" + port).getDatabase(dbName);
    }

    public MongoDatabase getDatabase() {
        return db;
    }
}
