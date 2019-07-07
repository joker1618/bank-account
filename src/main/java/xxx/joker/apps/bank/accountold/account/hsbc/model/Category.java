package xxx.joker.apps.bank.accountold.account.hsbc.model;

import xxx.joker.libs.core.lambdas.JkStreams;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private String name;
    private List<Condition> conditions;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
        this.conditions = new ArrayList<>();
    }

    public boolean match(String source) {
        return !JkStreams.filter(conditions, c -> c.match(source)).isEmpty();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }
}
