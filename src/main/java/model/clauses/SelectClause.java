package model.clauses;

import lombok.Getter;
import lombok.Setter;
import model.mongoClause.AggOperators;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class SelectClause extends Clause{
    public SelectClause() {
        super("SELECT");
    }

    private List<AggOperators> stevan = new ArrayList<>();
}
