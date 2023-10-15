package model.clauses;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class Clause {

    private String keyword;
    private List<String> parameters;

    public Clause(String keyword) {
        this.keyword = keyword;
        parameters = new ArrayList<>();
    }

    public void addParameter(String parameter) {
        parameters.add(parameter);
    }


}
