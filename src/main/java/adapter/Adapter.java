package adapter;

import com.mongodb.client.MongoCursor;
import org.bson.Document;

public interface Adapter {
    public MongoCursor<Document> getMongoQuery();
}
