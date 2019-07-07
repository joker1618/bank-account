package xxx.joker.apps.bank.accountold.account.hsbc.model;

import org.apache.commons.lang3.StringUtils;

public class Condition {

    private boolean caseSensitive;
    private String equalsTo;
    private String startWith;
    private String endWith;
    private String contained;

    public Condition() {
    }

    public Condition(String equalsTo, String startWith, String endWith, String contained) {
        this.equalsTo = equalsTo;
        this.startWith = startWith;
        this.endWith = endWith;
        this.contained = contained;
    }

    public boolean match(String source) {
        boolean res = true;

        if(StringUtils.isNotBlank(equalsTo)) {
            res &= caseSensitive ? source.equals(equalsTo) : source.equalsIgnoreCase(equalsTo);
        }
        if(StringUtils.isNotBlank(startWith) && res) {
            res &= caseSensitive ? source.startsWith(startWith) : StringUtils.startsWithIgnoreCase(source, startWith);
        }
        if(StringUtils.isNotBlank(endWith) && res) {
            res &= caseSensitive ? source.endsWith(endWith) : StringUtils.endsWithIgnoreCase(source, endWith);
        }
        if(StringUtils.isNotBlank(contained) && res) {
            res &= caseSensitive ? source.contains(contained) : StringUtils.containsIgnoreCase(source, contained);
        }

        return res;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public String getEqualsTo() {
        return equalsTo;
    }

    public void setEqualsTo(String equalsTo) {
        this.equalsTo = equalsTo;
    }

    public String getStartWith() {
        return startWith;
    }

    public void setStartWith(String startWith) {
        this.startWith = startWith;
    }

    public String getEndWith() {
        return endWith;
    }

    public void setEndWith(String endWith) {
        this.endWith = endWith;
    }

    public String getContained() {
        return contained;
    }

    public void setContained(String contained) {
        this.contained = contained;
    }
}
