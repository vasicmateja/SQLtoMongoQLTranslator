package adapter;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import database.MongoDB;
import execution.Execution;
import model.clauses.*;
import model.mongoClause.TranslateImplementation;
import org.bson.BsonTimestamp;
import org.bson.Document;
import view.MainFrame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AdapterImplementation implements Adapter {

    private MongoClient connection;
    private List<Clause> sqlClauses;
    private boolean isAggregation;

    private boolean isSubQuery;

    private String podUpitQuery;
    public AdapterImplementation(List<Clause> sqlClauses, MongoClient connection, boolean isAggregation, String podUpitQuery,boolean isSubQuery) {
        this.connection = new MongoClient();
        this.sqlClauses = sqlClauses;
        this.connection= connection;
        this.isAggregation=isAggregation;
        this.isSubQuery = isSubQuery;
        this.podUpitQuery=podUpitQuery;
    }


    @Override
    public MongoCursor<Document> getMongoQuery() {

        System.out.println("AdapterImplementation getQuery");

        MongoCursor<Document> documentMongoCursor = translateSQLtoMongo(this.sqlClauses);
        if(!documentMongoCursor.hasNext()){
            JOptionPane.showMessageDialog(MainFrame.getInstance(),"SQL je los");
            // System.out.println("Translate je los");
        }
//        while (documentMongoCursor.hasNext()){
//            Document d = documentMongoCursor.next();
//            System.out.println("Umbrellaa  " + d.toJson());
//        }
        return documentMongoCursor;
    }



    List<Document> aggregation = new ArrayList<>();

    private MongoCursor<Document> translateSQLtoMongo(List<Clause> sqlClauses) {

        TranslateImplementation translateImplementation = new TranslateImplementation();
        MongoCursor<Document> cursor;
        Execution execution = new Execution(connection);

        if(isSubQuery){
            System.out.println("PODUPIT ADAPTER");
            cursor = execution.executeMongoQuerySubQuery(translateImplementation,sqlClauses, podUpitQuery);
        }else if(!isAggregation) {
            System.out.println("NORMALNI ADAPTER");
            cursor = execution.executeMongoQueryNormal(translateImplementation,sqlClauses);
        }else {
            System.out.println("AGREGACIONI ADAPTER");
            cursor = execution.executeMongoQueryAggregation(translateImplementation,sqlClauses);
        }



//       while (cursor.hasNext()){
//           Document d = cursor.next();
//           System.out.println("STA JE OVO" + d.toJson());
//       }

        return cursor;
    }
}
