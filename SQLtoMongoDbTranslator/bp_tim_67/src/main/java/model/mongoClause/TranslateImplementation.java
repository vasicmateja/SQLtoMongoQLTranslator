package model.mongoClause;

import model.Parser;
import model.clauses.*;
import org.bson.BsonTimestamp;
import org.bson.Document;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.DoublePredicate;

public class TranslateImplementation implements Translate{


    List<Operator> operators = new ArrayList<>();

    List<AggOperators> agregacije = new ArrayList<>();
    List<String> aggregationOperations;
    public TranslateImplementation(){
        aggregationOperations = new ArrayList<>();
        aggregationOperations.add("count");
        aggregationOperations.add("avg");
        aggregationOperations.add("max");
        aggregationOperations.add("min");
        aggregationOperations.add("sum");
    };





    @Override
    public String selectToProjection(Clause clause) {
        String projection = "";


        StringBuilder projectionBuilder = new StringBuilder("");



        projectionBuilder.append("{");


        for(String parametar : clause.getParameters()){

            if(parametar.equals(" "))
                continue;

            if(parametar.equals("*"))
                break;
            projectionBuilder.append(parametar);
            projectionBuilder.append(":1");
            projectionBuilder.append(",");
        }
            projectionBuilder.deleteCharAt(projectionBuilder.length()-1);


        projectionBuilder.append("}");

        projection = projectionBuilder.toString();

        System.out.println("PROJECTION: " + projection);

        return projection;
    }
    @Override
    public String fromToCollection(Clause clause) {

        String rez = clause.getParameters().get(0);
        // TODO hendluj gresku da baca Exception
        if(clause.getParameters().size() > 1){
            System.out.println("Previse parametara GRESKA FROM");
        }

        return rez;
    }


    @Override
    public String orderByToSort(Clause clause) {

        //int counter=0;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for(String parametar : clause.getParameters()){

            if(parametar.equalsIgnoreCase("asc")){

                stringBuilder.append(": 1");
            }
            else if(parametar.equalsIgnoreCase("dsc")){
                stringBuilder.append(": -1");
            }else
                stringBuilder.append(parametar);


        }
        stringBuilder.append("}");

        String rez = stringBuilder.toString();

        System.out.println("ORDER By: "+rez);

        return rez;
    }

    @Override
    public String whereToFind(Clause clause) {
        String noviString = "";

        if(clause.getParameters().isEmpty()){
            return "{}";
        }
        //System.out.println("Moji parametri su" + clause.getParameters());

        int counter =1;
       String levi="";
       String op="";

        StringBuilder stringBuilder = new StringBuilder();


        for(String parametar : clause.getParameters()) {
            if(parametar.equals(",")) continue;
            if(counter % 3 == 0){
                Operator operator = new Operator();
                operator.setLevo(levi);
                operator.setOperator(op);
                operator.setDesno(parametar);
                levi="";
                op="";
                operators.add(operator);
            }else if(counter % 3 == 1){
                levi=parametar;
            }else{
                op=parametar;
            }



            counter++;
          }

        stringBuilder.append("{");


        for(Operator operator : operators){

            stringBuilder.append(operator.getLevo() + ": " + "{" + operator.getOperator() + ":" + operator.getDesno()+"}"+",\n");



        }

        stringBuilder.deleteCharAt(stringBuilder.length()-2);

        stringBuilder.append("}");

        //System.out.println(stringBuilder.toString());
        String rez = stringBuilder.toString();
        System.out.println("WHERE : "+rez);
        return rez;
    }






    @Override
    public List<Document> aggregationTranslate(List<Clause> clauses) {

        List<Document> aggregationQuery = new ArrayList<>();
        System.out.println("USAOOOOOOOOOOO");
        Document select = null;
        List<String> selectParameters = new ArrayList<>();
        List<String> fromParameters = new ArrayList<>();
        List<OrderByOperators> orderByParameters = new ArrayList<>();

        boolean orderFlag = false;
        boolean whereFlag = false;
        boolean groupFlag = false;

        // TODO OVO TI JE UPIT KOJI PREVODIS
        //  db.getCollection("hr.employees").aggregate([
        //  {
        //    $match: {
        //      department_id: { $gt: 30 },
        //      manager_id: { $lt: 120 }
        //    }
        //  },
        //  {
        //    $group: {
        //      _id: {
        //        department_id: "$department_id",
        //        manager_id: "$manager_id"
        //      },
        //      avgSalary: { $avg: "$salary" }
        //    }
        //  },
        //  {
        //    $sort: {
        //      avgSalary: 1
        //    }
        //  },
        //  {
        //    $project: {
        //      _id: 0, // Da biste izuzeli _id polje iz rezultata
        //      avgSalary: 1,
        //      department_id: "$_id.department_id",
        //      manager_id: "$_id.manager_id"
        //    }
        //  }
        // ])

        Document matchDocument;
        Document sortDocument;
        Document projectionDocument;
        Document groupDocument;
        String m = "";
        StringBuilder sortBuilder = new StringBuilder();
        StringBuilder projectionBuilder = new StringBuilder();
        StringBuilder groupBuilder = new StringBuilder();
        Document groupObject = null;


        for(Clause clause : clauses){

            if(clause instanceof WhereClause){
                whereFlag = true;
                m = whereToFind(clause);
            }

            if(clause instanceof OrderByClause){
                orderFlag = true;
                boolean aggFlag = false;
                StringBuilder mojString = new StringBuilder();

                sortBuilder.append("{");
                for(String parametar: clause.getParameters()){
                    if(parametar.equalsIgnoreCase("(")) continue;
                    if(aggregationOperations.contains(parametar)){
                        aggFlag = true;
                        mojString = new StringBuilder();
                        mojString.append(parametar);
                    } else if (aggFlag && parametar.equalsIgnoreCase(")")) {
                        aggFlag = false;
                        sortBuilder.append(mojString);
                    } else if (aggFlag) {
                        mojString.append(parametar);
                    } else{
                        if(parametar.equalsIgnoreCase("asc")){

                            sortBuilder.append(": 1,");
                        }
                        else if(parametar.equalsIgnoreCase("dsc")){
                            sortBuilder.append(": -1,");
                        }else
                            sortBuilder.append(parametar);

                    }

                }
                sortBuilder.deleteCharAt(sortBuilder.length()-1);
                sortBuilder.append("}");

            }

            if(clause instanceof SelectClause){
                boolean aggFlag = false;
                StringBuilder mojString = new StringBuilder();

                projectionBuilder.append("{");
                projectionBuilder.append("_id: 0,");


                    for(int i = 0; i < clause.getParameters().size(); i++) {
                        if (aggregationOperations.contains(clause.getParameters().get(i))) {
                            AggOperators agregacija = new AggOperators();
                            agregacija.setOperator(clause.getParameters().get(i));
                            agregacija.setKolona(clause.getParameters().get(i + 2));
                            agregacije.add(agregacija);
                        }
                    }

                    for(String parametar : clause.getParameters()) {


                        if (parametar.equalsIgnoreCase("(")) continue;
                        if (aggregationOperations.contains(parametar)) {
                            aggFlag = true;
                            mojString = new StringBuilder();
                            mojString.append(parametar);
                        } else if (aggFlag && parametar.equalsIgnoreCase(")")) {
                            aggFlag = false;
                            projectionBuilder.append(mojString);
                        } else if (aggFlag) {
                            mojString.append(parametar).append(": 1,");
                        } else {
                            projectionBuilder.append(parametar).append(":").append("\"").append("$").append(parametar).append("\"").append(",");
                        }

                    }
                    projectionBuilder.deleteCharAt(projectionBuilder.length()-1);
                projectionBuilder.append("}");
            }

            if(clause instanceof GroupByClause){
                groupFlag=true;

                Document idObject = new Document();
                for (String parametar : clause.getParameters()) {
                    if(parametar.equalsIgnoreCase(",")) continue;
                    idObject.append(parametar, "$" + parametar);
                }

                // Kreiranje avgSalary objekta
                 groupObject = new Document("_id", idObject);

                for(AggOperators parametar: agregacije) {
                    Document aggObject = new Document();
                    aggObject.append("$" + parametar.getOperator(), "$" + parametar.getKolona());
                    groupObject.append(parametar.getOperator() + parametar.getKolona(), aggObject);
                }




            }


        }

        String sort = sortBuilder.toString();
        String projection = projectionBuilder.toString();
        String group = groupBuilder.toString();

        System.out.println("GROUP   "+group);
        System.out.println(m + " OVO mi je M");
        List<Document> rezultat = new ArrayList<>();

        if(whereFlag) {
            matchDocument = new Document("$match", Document.parse(m));
            rezultat.add(matchDocument);
        }
        if(groupFlag) {
            groupDocument = new Document("$group", groupObject);
            rezultat.add(groupDocument);
        }
        if(orderFlag) {
            sortDocument = new Document("$sort", Document.parse(sort));
            rezultat.add(sortDocument);
            //projectionDocument = new Document("$project",Document.parse(projection));
        }

//        List<Document> rezultat = new ArrayList<>();
//
//        rezultat.add(matchDocument);
//        rezultat.add(groupDocument);
//        rezultat.add(sortDocument);


        return rezultat;
    }

    // TODO
    //  select
    //  department_name, department_id, location_id from hr.departments where department_id in
    //  (select department_id from hr.employees group by department_id having max(salary) > 10000)


    // TODO OVO TI JE UPIT KOJI RADIS!!!!!
    //  db.departments.aggregate([
    //  {
    //    $lookup: {
    //      from: "employees",
    //      localField: "department_id",
    //      foreignField: "department_id",
    //      as: "employees"
    //    }
    //  },
    //  {
    //    $unwind: "$employees"
    //  },
    //  {
    //    $group: {
    //      _id: "$department_id",
    //      department_name: { $first: "$department_name" },
    //      location_id: { $first: "$location_id" },
    //      max_salary: { $max: "$employees.salary" }
    //    }
    //  },
    //  {
    //    $match: {
    //      max_salary: { $gt: 10000 }
    //    }
    //  }
    //  }
    //])

    @Override
    public List<Document> subQuerryTranslate(List<Clause> clauses, String podUpitQuery) {

        Parser parser = new Parser();

        List<String> podSelectParametri = new ArrayList<>();
        List<String> podFromParametri = new ArrayList<>();
        List<String> podWhereParametri = new ArrayList<>();
        List<String> podOrderByParametri = new ArrayList<>();
        List<String> podGroupByParametri = new ArrayList<>();


        List<PodAggOperator> havingParametri = new ArrayList<>();

        boolean have = false;

        List<Document> rezultat = new ArrayList<>();

        List<Clause> podUpitClauses = parser.parse(podUpitQuery);


        for(Clause podClause : podUpitClauses){



            // TODO ISPUNILI SMO NASE LISTE PARAMETARA 8=====D
            if(podClause instanceof SelectClause){
                podSelectParametri.addAll(podClause.getParameters());
            }
            if(podClause instanceof FromClause){
                podFromParametri.addAll(podClause.getParameters());
            }
            if(podClause instanceof WhereClause){
                podWhereParametri.addAll(podClause.getParameters());
            }
            if(podClause instanceof GroupByClause){
                podGroupByParametri.addAll(podClause.getParameters());
            }
            if(podClause instanceof OrderByClause){
                podOrderByParametri.addAll(podClause.getParameters());
            }


        }


        podGroupByParametri.remove(")");
        podGroupByParametri.remove("(");

        System.out.println(podGroupByParametri+ "OVO SU GROUP PARAMETRI");

        int i=-1;
        for(String parametar : podGroupByParametri){
            i++;
            if(parametar.equalsIgnoreCase("having")){
                have = true;
            }

            if(have){
                if(aggregationOperations.contains(parametar)){
                    PodAggOperator operator = new PodAggOperator();
                    operator.setOperator(parametar);
                    operator.setKolona(podGroupByParametri.get(i+1));
                    operator.setZnaci(podGroupByParametri.get(i+2));
                    operator.setBroj(podGroupByParametri.get(i+3));
                    havingParametri.add(operator);
                }
            }
        }
        have=false;



        Document lookUp=null;
        Document unwind = null;
        Document groupDoc = null;
        Document groupDocument = null;
        StringBuilder lookupBuilder = new StringBuilder();

        for(Clause obicni : clauses){
            System.out.println("Usao u "+obicni.getKeyword());
            if(obicni instanceof WhereClause){
                lookupBuilder.append("{");
                lookupBuilder.append("from").append(":");
                for(String parametar : podFromParametri){
                    lookupBuilder.append("\"").append(parametar).append("\"").append(",");
                }

                lookupBuilder.append("localField").append(":");
                lookupBuilder.append("\"").append(obicni.getParameters().get(0)).append("\"").append(",");
                lookupBuilder.append("foreignField").append(":");

                for(String parametar : podSelectParametri){
                    lookupBuilder.append("\"").append(parametar).append("\"").append(",");
                }
                lookupBuilder.append("as: ");
                for(String parametar : podFromParametri){
                    lookupBuilder.append("\"").append(parametar).append("\"").append(",");
                }

                lookupBuilder.deleteCharAt(lookupBuilder.length()-1);

                lookupBuilder.append("}");
            }


           // System.out.println(lookupBuilder.toString());


            if(obicni instanceof SelectClause) {

                Document idObject = new Document();

                for (String parametar : obicni.getParameters()) {
                    if (parametar.equalsIgnoreCase(",")) continue;
                    idObject.append(parametar, "$" + parametar);
                }

                // Kreiranje avgSalary objekta
                groupDoc = new Document("_id", idObject);

                for (PodAggOperator parametar : havingParametri) {
                    Document aggObject = new Document();
                    aggObject.append("$" + parametar.getOperator(), "$" + podFromParametri.get(0)+"." + parametar.getKolona());
                    groupDoc.append(parametar.getOperator() + parametar.getKolona(), aggObject);
                    //System.out.println(groupDoc.toJson());
                }

            }

        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for(PodAggOperator operator : havingParametri){

            stringBuilder.append(operator.getOperator()).append(operator.getKolona())
                    .append(":").append("{").append(operator.getZnaci()).append(":").append(operator.getBroj())
                    .append("}").append(",");

        }
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
        stringBuilder.append("}");

        String lookUpString = lookupBuilder.toString();


        lookUp = new Document("$lookup",Document.parse(lookUpString));
        rezultat.add(lookUp);


        StringBuilder unWind = new StringBuilder();

        unWind.append("$").append(podFromParametri.get(0));
        String unwindString = unWind.toString();

        unwind = new Document("$unwind",unwindString);
        rezultat.add(unwind);

        groupDocument = new Document("$group",groupDoc);
        rezultat.add(groupDocument);

        String matchString = stringBuilder.toString();


        Document match= new Document("$match",Document.parse(matchString));
        rezultat.add(match);

       System.out.println(lookUp.toJson());
//        System.out.println(unwind.toJson().replace("\\", ""));
//        System.out.println(groupDocument.toJson().replace("\\", ""));
//        System.out.println(match.toJson());


        return rezultat;
    }
}
