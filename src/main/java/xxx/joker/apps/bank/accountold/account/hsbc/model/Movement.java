package xxx.joker.apps.bank.accountold.account.hsbc.model;

import java.time.LocalDate;
import java.util.Objects;

public class Movement implements Comparable<Movement> {

    private LocalDate date;
    private double amount;
    private String category;
    private String descr;


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public int compareTo(Movement o) {
        return date.compareTo(o.getDate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movement movement = (Movement) o;
        return Double.compare(movement.amount, amount) == 0 &&
                date.compareTo(movement.date) == 0 &&
                Objects.equals(descr, movement.descr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, descr, amount);
    }
}
