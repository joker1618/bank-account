package xxx.joker.apps.bank.accountold.account.common;

import xxx.joker.apps.bank.accountold.account.hsbc.model.Movement;

import java.util.List;
import java.util.function.DoublePredicate;
import java.util.stream.DoubleStream;

public class BaUtil {

    public static double sumAmounts(List<Movement> mlist) {
        return sumAmounts(mlist, null);
    }
    public static double sumAmounts(List<Movement> mlist, DoublePredicate filter) {
        DoubleStream stream = mlist.stream().mapToDouble(Movement::getAmount);
        if(filter != null)  stream = stream.filter(filter);
        return stream.sum();
    }

}
