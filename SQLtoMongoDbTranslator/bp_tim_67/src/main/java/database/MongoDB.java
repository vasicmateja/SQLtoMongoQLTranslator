package database;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import resource.data.Row;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MongoDB {
    private static String user = "writer";
    private static String database = "bp_tim67";
    private static String password = "dEVppnNf5K2Pbv8s";
    public static MongoClient getConnection(){

        MongoCredential credential = MongoCredential.createCredential(user, database, password.toCharArray());
        MongoClient mongoClient = new MongoClient(new ServerAddress("134.209.239.154", 27017), Arrays.asList(credential));

        System.out.println ("Mongo Database connection established");

        return mongoClient;

    }

}
