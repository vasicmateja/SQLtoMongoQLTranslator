package test;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;

public class MongoDBTest implements DBTest {

    private MongoClient connection;

    public MongoDBTest(MongoClient connection) {
        this.connection = connection;
    }

    public void runTest() {

       MongoDatabase database = connection.getDatabase("bp_tim67");
       MongoCursor<Document> cursor = database.getCollection("employees").aggregate(
                Arrays.asList(
                        Document.parse("{\n" +
                                "  $match: {first_name: \"Steven\", last_name: \"King\"}\n" +
                                "}"),
                        Document.parse("{\n" +
                                "  $lookup: {\n" +
                                "    from: \"employees\",\n" +
                                "    localField: \"department_id\",\n" +
                                "    foreignField: \"department_id\",\n" +
                                "    as: \"employeesInTheSameDepartment\"\n" +
                                "  }\n" +
                                "}"),
                        Document.parse("{ $unwind: \"$employeesInTheSameDepartment\" }"),
                        Document.parse("{ $project: {\n" +
                                "    \"employeesInTheSameDepartment.first_name\": 1,\n" +
                                "    \"employeesInTheSameDepartment.last_name\": 1\n" +
                                "  }\n" +
                                "}")
                )
       ).iterator();

       while (cursor.hasNext()){
           Document d = cursor.next();
           System.out.println(d.toJson());
       }

       /*
       String projection = "{department_name:1, location_id:1}";
       String sort = "{department_id:-1}";
       MongoCursor<Document> cursor1 = database.getCollection("departments").find(Document.parse("{department_id:{$lt:90}}")).projection(Document.parse(projection))
               .sort(Document.parse(sort)).iterator();

        while (cursor1.hasNext()){
            Document d = cursor1.next();
            System.out.println(d.toJson());
        }


        */
        this.closeConnection();
    }

    private void closeConnection(){
        try{
            connection.close();
        }
        catch (MongoClientException e){
            e.printStackTrace();
        }
        finally {
            connection = null;
        }
    }
}
