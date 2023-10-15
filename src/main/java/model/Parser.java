package model;

import lombok.Getter;
import lombok.Setter;
import model.clauses.*;
import validator.Validator;
import view.MainFrame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class Parser {

    private String sqlQuery;

    private List<Clause> clauses;

    private SelectClause selectClause;

    private FromClause fromClause;

    private WhereClause whereClause;

    private GroupByClause groupByClause;

    private HavingClause havingClause;

    private OrderByClause orderByClause;

    private InClause inClause;

    private JoinClause joinClause;

    private SortClause sortClause;

    private UsingClause usingClause;

    private String podUpitQuerry = "";

    private Validator validator;

    private boolean sel = false;
    private boolean fr = false;

    private boolean wh = false;

    private boolean sr = false;

    private boolean jn = false;

    private boolean us = false;

    private boolean gr = false;

    private boolean ob = false;

    private boolean lk = false;

    private boolean hv = false;

    private boolean aggregation = false;

    private boolean podUpit = false;

    private boolean secretPodUpit = false;


    public List<Clause> parse(String sqlQuery){

        if(sqlQuery.toLowerCase().contains("avg (") ||
                sqlQuery.toLowerCase().contains("max (") ||
                sqlQuery.toLowerCase().contains("min (") ||
                sqlQuery.toLowerCase().contains("count (") ||
                sqlQuery.toLowerCase().contains("sum ("))
            aggregation=true;


        clauses = new ArrayList<>();
        List<String> query= List.of(sqlQuery.split(" "));

        int brojac =0;



        for(String word : query){

        brojac++;
        int z1=0;
        int z2=0;

            if(word.equals("")) continue;
        word.replaceAll(" \\s","");
            if(word.equalsIgnoreCase("select") && !podUpit){
                selectClause = new SelectClause();
                sel = true;
            }else if(word.equalsIgnoreCase("from") && !podUpit){
                fromClause = new FromClause();
                fr = true;
                sel = false;

            }else if(word.equalsIgnoreCase("join")){
                joinClause = new JoinClause();
                fr = false;
                sel = false;
                jn = true;

            }else if(word.equalsIgnoreCase("using")){
                usingClause = new UsingClause();
                jn=false;
                us=true;
            }else if(word.equalsIgnoreCase("where") && !podUpit){
                if(whereClause==null)
                whereClause = new WhereClause();

                fr = false;
                wh = true;

            }else if(word.equalsIgnoreCase("group") && query.get(brojac).equalsIgnoreCase("by") && !podUpit){
                gr = true;
                wh = false;
                fr = false;
                sel = false;
                ob=false;
                System.out.println("USAOOOOO");
                continue;
            }else if(word.equalsIgnoreCase("by") && gr && !podUpit) {
                groupByClause = new GroupByClause();

            }else if(word.equalsIgnoreCase("mojmilane") && !podUpit){
                hv = false;
                fr=false;
                gr = false;
                wh = false;
                ob = true;
                orderByClause = new OrderByClause();
            }else if(sel){

               // if(word.equals("")) continue;

                if(word.equalsIgnoreCase(",")) continue;

                selectClause.addParameter(word);

            }else if (fr){
                fromClause.addParameter(word);

            }else if(jn){
                joinClause.addParameter(word);
            }else if(us){
                usingClause.addParameter(word);
            }else if(wh){

                if(word.equalsIgnoreCase("(") && query.get(brojac).equalsIgnoreCase("select")){
                    z1++;
                    podUpit = true;
                    continue;
                }else if(word.equalsIgnoreCase(")")) {
                    z2++;
                    if(z2!=z1){
                        //System.out.println("gde sam sad  " + word);
                        continue;
                    }

                    podUpit = false;
                    continue;
                }


                if(!podUpit) {

                    if(word.equalsIgnoreCase("and")) continue;

                    whereClause.addParameter(word);
                }else{
                    podUpitQuerry = podUpitQuerry + " " + word;
                }


            }else if(gr){
                groupByClause.addParameter(word);
            }else if(ob){
                orderByClause.addParameter(word);
            }else {
                continue;
            }

        }
        if(selectClause!=null) {
            clauses.add(selectClause);
        }
        if(fromClause != null) {
            clauses.add(fromClause);
        }
        if(joinClause != null){
            clauses.add(joinClause);
        }
        if(usingClause != null){
            clauses.add(usingClause);
        }

        if(whereClause != null) {
            clauses.add(whereClause);
        }
        if(groupByClause != null) {
            clauses.add(groupByClause);
        }
        if(havingClause != null) {
            clauses.add(havingClause);
        }
        if(orderByClause != null) {
            clauses.add(orderByClause);
        }

        for(Clause clause : clauses){
            System.out.println("Clause :" +clause.getKeyword()+" words: " + clause.getParameters());
        }

        System.out.println(podUpitQuerry);

        validator = new Validator();
       String poruka = validator.validate(clauses);

       if(!secretPodUpit) {
           if (!poruka.equals("SVE TOP")) {
               if (poruka.equals("SELECT")){
                   JOptionPane.showMessageDialog(MainFrame.getInstance(),"Nemate obavezne delove upita");
                   System.out.println("Nemate obavezne delove upita");}
               if (poruka.equals("GROUP")) {
                   JOptionPane.showMessageDialog(MainFrame.getInstance(),"Nedostaju stvari u group by delu upita");
                   System.out.println("Nedostaju stvari u group by delu upita");
               }
               if (poruka.equals("WHERE")){
                   JOptionPane.showMessageDialog(MainFrame.getInstance(),"U where delu se nalazi funkcija agregacije a to nije dozvoljeno");
                   System.out.println("U where delu se nalazi funkcija agregacije a to nije dozvoljeno");
               }
               if (poruka.equals("JOIN")){
                   JOptionPane.showMessageDialog(MainFrame.getInstance(),"Nedostaje using ili on u delu join");
                   System.out.println("Nedostaje using ili on u delu join");
               }
               System.exit(-111);
           }
       }
        return clauses;
    }


    public boolean isAggregation() {
        return aggregation;
    }

    public void setAggregation(boolean aggregation) {
        this.aggregation = aggregation;
    }

    public boolean isPodUpit() {
        return podUpit;
    }

    public void setPodUpit(boolean podUpit) {
        this.podUpit = podUpit;
    }
}
