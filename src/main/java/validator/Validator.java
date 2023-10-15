package validator;

import model.clauses.*;
import model.mongoClause.AggOperators;
import view.MainFrame;

import java.util.ArrayList;
import java.util.List;

public class Validator {


    ArrayList<String> aggregationOperations;
    List<String> agregacije = new ArrayList<>();
    public Validator() {
        aggregationOperations = new ArrayList<>();
        aggregationOperations.add("count");
        aggregationOperations.add("avg");
        aggregationOperations.add("max");
        aggregationOperations.add("min");
        aggregationOperations.add("sum");
    }

    public String validate(List<Clause> clauses){
            System.out.println("Usao u validator");
            boolean select = false;
            boolean from = false;
            boolean join = false;
            boolean using = false;
            boolean where = false;
            boolean group = false;

            List<String> selectParametri = new ArrayList<>();
            List<String> groupParametri = new ArrayList<>();
            List<String> whereParametri = new ArrayList<>();

        for (Clause clause: clauses){
            if(clause instanceof SelectClause){
                select = true;
                selectParametri.addAll(clause.getParameters());

                for(int i = 0; i < clause.getParameters().size(); i++) {
                    if (aggregationOperations.contains(clause.getParameters().get(i))) {
                        agregacije.add(clause.getParameters().get(i));
                        agregacije.add(clause.getParameters().get(i + 2));
                    }
                }

                selectParametri.removeAll(agregacije);
                selectParametri.remove("(");
                selectParametri.remove(")");
            }
            if(clause instanceof FromClause){
                from = true;
            }

            if(clause instanceof GroupByClause){
                group=true;
                groupParametri.addAll(clause.getParameters());
                groupParametri.remove(",");
            }

            if(clause instanceof WhereClause){
                where = true;
                whereParametri.addAll(clause.getParameters());

            }
            if(clause instanceof JoinClause) {
                join = true;
                if(clause.getParameters().contains("on") || clause.getParameters().contains("ON")) using=true;
            }
            if(clause instanceof UsingClause){
                using = true;
            }
        }

        System.out.println("Select parametri "+selectParametri);
        System.out.println("Agg pametri " + agregacije);
        System.out.println("Group parametri " + groupParametri);

        if(join && !using){
            return "JOIN";
        }

        if(group){
            if(!groupParametri.equals(selectParametri)) return "GROUP";
        }

        if(where) {
            for (String whereP : whereParametri)
                if (aggregationOperations.contains(whereP)) return "WHERE";
        }

        if(!select) return "SELECT";

         if(!from) return "SELECT";



        return "SVE TOP";
    }


}
