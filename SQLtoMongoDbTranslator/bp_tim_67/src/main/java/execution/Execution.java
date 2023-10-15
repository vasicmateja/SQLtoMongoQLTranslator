package execution;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import database.MongoDB;
import model.clauses.*;
import model.mongoClause.TranslateImplementation;
import org.bson.Document;

import java.util.List;

public class Execution {
    String projection = "{}";
    String collection = "{}";

    String find = "{}";

    String sort = "{}";
    MongoCursor<Document> cursor;
    MongoDatabase database;

    public Execution(MongoClient connection) {
        this.database = connection.getDatabase("bp_tim67");
    }

    public MongoCursor<Document> executeMongoQueryNormal(TranslateImplementation translateImplementation, List<Clause> sqlClauses){

        for (Clause sqlClause : sqlClauses) {
            System.out.println(sqlClause.getKeyword());
            if (sqlClause instanceof SelectClause && !sqlClause.getParameters().get(0).equals("*"))
                projection = translateImplementation.selectToProjection(sqlClause);
            if (sqlClause instanceof FromClause) collection = translateImplementation.fromToCollection(sqlClause);
            if (sqlClause instanceof WhereClause) find = translateImplementation.whereToFind(sqlClause);
            if (sqlClause instanceof OrderByClause) sort = translateImplementation.orderByToSort(sqlClause);

        }
        //select avg(salary), department_id from hr.employees where department_id between 59
        //    group by department_id order by avg(salary) asc

        System.out.println(projection);

        cursor = database.getCollection(collection)
                .find(Document.parse(find))
                .projection(Document.parse(projection)).sort(Document.parse(sort))
                .iterator();

        return cursor;
    }


    public MongoCursor<Document> executeMongoQueryAggregation(TranslateImplementation translateImplementation, List<Clause> sqlClauses){
        for(Clause clause: sqlClauses){
            if(clause instanceof FromClause){
                collection=clause.getParameters().get(0);
            }
        }

        System.out.println("JESTE AGREGACIJA");
        List<Document> aggregation=translateImplementation.aggregationTranslate(sqlClauses);

        for (Document document : aggregation){
            System.out.println(document.toJson());
        }
        cursor= database.getCollection(collection).aggregate(aggregation).iterator();

        return cursor;
    }

    public MongoCursor<Document> executeMongoQuerySubQuery(TranslateImplementation translateImplementation, List<Clause> sqlClauses, String podUpitQuery) {
        for(Clause clause: sqlClauses){
            if(clause instanceof FromClause){
                collection=clause.getParameters().get(0);
            }
        }

        List<Document> subQuery = translateImplementation.subQuerryTranslate(sqlClauses,podUpitQuery);

        for (Document document : subQuery){

            System.out.println(document.toJson() + "OVDE SAM DEBIL");
        }

        cursor = database.getCollection(collection).aggregate(subQuery).iterator();

        return cursor;
    }
}
