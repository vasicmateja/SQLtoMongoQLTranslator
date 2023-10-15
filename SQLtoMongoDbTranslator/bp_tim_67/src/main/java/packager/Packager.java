package packager;

import com.mongodb.client.MongoCursor;
import model.clauses.Clause;
import model.clauses.SelectClause;
import org.bson.Document;
import resource.data.Row;

import java.util.ArrayList;
import java.util.List;

public class Packager {


    List<Row> rows;

    public Packager() {

        rows = new ArrayList<>();
    }

    public List<Row> packageNormal(MongoCursor<Document> documents,List<Clause> clauses){


        List<String> selectParametri = new ArrayList<>();

        for(Clause clause : clauses){

            if(clause instanceof SelectClause){

                for(String parametar : clause.getParameters()) {
                    selectParametri.add(parametar);
                    System.out.println(parametar);
                }
            }


        }

        Row row;
        while (documents.hasNext()){
            Document document = documents.next();

            row = new Row();

            for (String parametar: selectParametri){

                row.setName("HR tabela");
                row.addField(parametar,document.get(parametar));

            }
            rows.add(row);


        }
        return rows;
    }

    public List<Row> packagerAggregation(MongoCursor<Document> documents,List<Clause> clauses){
        List<String> selectParametri = new ArrayList<>();

        List<String> izbaceni = new ArrayList<>();

        for (Clause clause : clauses) {

            if (clause instanceof SelectClause) {


                for(int i=0;i<clause.getParameters().size()-1;i++){
                    StringBuilder stringBuilder = new StringBuilder();
                    //System.out.println(clause.getParameters().get(i));
                    if(clause.getParameters().get(i).equalsIgnoreCase("(")){
                        String rez="";
                        rez = stringBuilder.append(clause.getParameters().get(i-1)).append(clause.getParameters().get(i+1)).toString();
                        clause.getParameters().remove(clause.getParameters().get(i-1));
                        clause.getParameters().remove(clause.getParameters().get(i));
                        izbaceni.add(rez);

                    }
                }

                clause.getParameters().remove("(");
                clause.getParameters().remove(")");

                selectParametri.addAll(clause.getParameters());
            }


        }

        Row row;

        while (documents.hasNext()) {
            Document document = documents.next();
            row = new Row();
            System.out.println(document.toJson());

            for (String izbacen:izbaceni){
                row.setName("HR tabela");
                row.addField(izbacen,document.get(izbacen));
                rows.add(row);
            }

            Document document1 = null;

            try {
                document1 = document.get("_id", Document.class);
            }catch (java.lang.ClassCastException e){
                System.out.println("OVDE JE SMO AGREGACIJA ");
            }

            if(document1 != null) {
                for (String parametar : selectParametri) {


                    row.setName("HR tabela");

                    row.addField(parametar, document1.get(parametar));


                }
            }


        }
        return rows;
    }

    public List<Row> packageSubQuery(MongoCursor<Document> documents, List<Clause> clauses){
        Row row;

        System.out.println("Usao u sub packager");

        for(Clause clause :clauses) {
            //System.out.println(clause.getKeyword());
            if (clause instanceof SelectClause) {

                System.out.println("Usao u clause");
                while (documents.hasNext()) {
                    Document document = documents.next();
                    row = new Row();
                    Document document1 = document.get("_id", Document.class);

                    for (String parametar : clause.getParameters()) {

                        row.setName("HR tabela");
                        row.addField(parametar, document1.get(parametar));

                    }
                    rows.add(row);
                }
            }
        }

        for(Row red: rows) {
            System.out.println("Red" + red);
        }
        return rows;
    }

}
