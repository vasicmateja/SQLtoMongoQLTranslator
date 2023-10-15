package model;

import lombok.Getter;
import lombok.Setter;
import model.clauses.Clause;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Query {
    private List<Clause> clauses;

    public Query() {
        clauses = new ArrayList<>();
    }

    public void addClause(Clause clause) {
        clauses.add(clause);
    }
}
