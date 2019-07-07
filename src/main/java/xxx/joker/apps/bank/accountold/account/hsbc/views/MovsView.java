package xxx.joker.apps.bank.accountold.account.hsbc.views;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.apps.bank.accountold.account.hsbc.model.Movement;
import xxx.joker.apps.bank.accountold.account.hsbc.views.StatElem;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkStrings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.DoublePredicate;

public class MovsView {

    private List<Movement> movs;

    private StatElem statTotal;
    private StatElem statIn;
    private StatElem statOut;

    private Map<String, StatElem> byCategory;

    public MovsView(List<Movement> movs) {
        init(movs);
    }

    private void init(List<Movement> movs) {
        this.movs = movs;
        statTotal = new StatElem(movs);
        statIn = new StatElem(filterAmount(d -> d >= 0d));
        statOut = new StatElem(filterAmount(d -> d < 0d));

        Map<String, List<Movement>> byCat = JkStreams.toMap(movs, m -> JkStrings.safeTrim(m.getCategory(), "<none>"));
        byCategory = new TreeMap<>();
        byCat.forEach((k,v) -> byCategory.put(k, new StatElem(v)));
    }
    private List<Movement> filterAmount(DoublePredicate filter) {
        return JkStreams.filter(movs, m -> filter.test(m.getAmount()));
    }

    public List<Movement> getMovs() {
        return movs;
    }

    public StatElem getStatTotal() {
        return statTotal;
    }

    public StatElem getStatIn() {
        return statIn;
    }

    public StatElem getStatOut() {
        return statOut;
    }

    public Map<String, StatElem> getByCategory() {
        return byCategory;
    }
}
