package xxx.joker.apps.bank.accountold.account.hsbc.views;

import xxx.joker.apps.bank.accountold.account.common.BaUtil;
import xxx.joker.apps.bank.accountold.account.hsbc.model.Movement;

import java.util.List;

public class StatElem {

    private String label;
    private int numMovs;
    private double amountMovs;

    public StatElem() {
    }

    public StatElem(List<Movement> movs) {
        this.numMovs = movs.size();
        this.amountMovs = BaUtil.sumAmounts(movs);
    }

    public StatElem(int numMovs, double amountMovs) {
        this.numMovs = numMovs;
        this.amountMovs = amountMovs;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getNumMovs() {
        return numMovs;
    }

    public void setNumMovs(int numMovs) {
        this.numMovs = numMovs;
    }

    public double getAmountMovs() {
        return amountMovs;
    }

    public void setAmountMovs(double amountMovs) {
        this.amountMovs = amountMovs;
    }
}
