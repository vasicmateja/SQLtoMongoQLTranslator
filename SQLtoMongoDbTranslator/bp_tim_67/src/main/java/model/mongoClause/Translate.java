package model.mongoClause;

import model.clauses.Clause;
import org.bson.Document;

import java.util.List;

public interface Translate {

    public String fromToCollection(Clause clause);

    public String selectToProjection(Clause clause);

    public String orderByToSort(Clause clause);

    public String whereToFind(Clause clause);

    public List<Document> aggregationTranslate(List<Clause> clauses);

    public List<Document> subQuerryTranslate(List<Clause> clauses,String podUpitQuery);


}
