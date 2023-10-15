package controller;

import adapter.AdapterImplementation;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import database.MongoDB;
import model.Parser;
import model.clauses.Clause;
import model.clauses.SelectClause;
import observer.Notification;
import observer.NotificationCode;
import observer.Publisher;
import observer.Subscriber;
import org.bson.Document;
import packager.Packager;
import resource.data.Row;
import view.MainFrame;
import view.table.TableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;


public class RunButtonController extends AbstractAction implements Publisher {

    private MongoClient connection;
    private Parser parser;
    private List<Clause> clauses;

    private TableModel tableModel;
    private List<Subscriber> subscribers;

    public RunButtonController() {
        subscribers = new ArrayList<>();
    }

    private Packager packager;

    // TODO: Srediti konekciju
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("ULAZAK U ACTION PERFORMED  " + MainFrame.getInstance().getQuerry());

        clauses = new ArrayList<>();

        connection= MongoDB.getConnection();
        parser = new Parser();



        clauses = parser.parse(MainFrame.getInstance().getQuerry()
                        .replace("("," ( ")
                        .replace(")", " ) ")
                        .replace(","," , ")
                        .replace(">="," $gte ")
                        .replace("!="," $ne ")
                        .replace("<="," $lte ")
                        .replace("="," $eq ")
                        .replace(">"," $gt ")
                        .replace("<"," $lt ")
                        .replace("not in"," $nin ")
                        .replace("like"," $regex ")
                        .replace("'%","/")
                        .replace("%'","/")
                        .replace("order by", "mojmilane"));

            AdapterImplementation adapterImplementation = new AdapterImplementation(clauses,connection, parser.isAggregation(),parser.getPodUpitQuerry(),parser.isPodUpit());


        if(parser.isPodUpit()){

            System.out.println("JESTE PODUPIT");

            MongoCursor<Document> documents;
            documents = adapterImplementation.getMongoQuery();
            List<Row> rows = new ArrayList<>();

            packager = new Packager();
            rows=packager.packageSubQuery(documents,clauses);

            for (Row red : rows) {
                System.out.println("Red  " + red);
            }

            MainFrame.getInstance().getAppCore().getTableModel().setRows(rows);
        }else if(parser.isAggregation()) {
            System.out.println("JESTE AGREGACIJA");
            MongoCursor<Document> documents;
            documents = adapterImplementation.getMongoQuery();
            List<Row> rows = new ArrayList<>();

            packager=new Packager();
            rows = packager.packagerAggregation(documents,clauses);
            for (Row red : rows) {
                System.out.println("red    " + red);
            }

            MainFrame.getInstance().getAppCore().getTableModel().setRows(rows);
           // this.notify(new Notification(NotificationCode.RESOURCE_LOADED, rows));

        }else {
            System.out.println("OBICAN QUERY");


            MongoCursor<Document> documents;
                documents = adapterImplementation.getMongoQuery();
                List<Row> rows = new ArrayList<>();

                packager = new Packager();
                rows = packager.packageNormal(documents,clauses);

                for(Row red : rows){
                    System.out.println(red);
                }

                MainFrame.getInstance().getAppCore().getTableModel().setRows(rows);
                // this.notify(new Notification(NotificationCode.RESOURCE_LOADED, rows));

        }
    }


    @Override
    public void addSubscriber(Subscriber subscriber) {
        if(!subscribers.contains(subscriber))
            subscribers.add(subscriber);
    }

    @Override
    public void notify(Notification notification) {
        for(Subscriber s : subscribers){
            s.update(notification);
        }
    }
}
