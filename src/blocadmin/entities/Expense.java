package blocadmin.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Geanina
 */
public class Expense implements Serializable, Comparable<Expense>{
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Household household;
    private String expenseType;
    private double totalSum;
    private double leftoverSum;
    private boolean payedInFull;
    private String details;
    private Date dueDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    public double getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(double totalSum) {
        this.totalSum = totalSum;
    }

    public double getLeftoverSum() {
        return leftoverSum;
    }

    public void setLeftoverSum(double leftoverSum) {
        this.leftoverSum = leftoverSum;
    }

    public boolean isPayedInFull() {
        return payedInFull;
    }

    public void setPayedInFull(boolean payedInFull) {
        this.payedInFull = payedInFull;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Household getHousehold() {
        return household;
    }

    public void setHousehold(Household household) {
        this.household = household;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
        hash = 89 * hash + Objects.hashCode(this.household);
        hash = 89 * hash + Objects.hashCode(this.expenseType);
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.totalSum) ^ (Double.doubleToLongBits(this.totalSum) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.leftoverSum) ^ (Double.doubleToLongBits(this.leftoverSum) >>> 32));
        hash = 89 * hash + (this.payedInFull ? 1 : 0);
        hash = 89 * hash + Objects.hashCode(this.details);
        hash = 89 * hash + Objects.hashCode(this.dueDate);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Expense other = (Expense) obj;
        if (Double.doubleToLongBits(this.totalSum) != Double.doubleToLongBits(other.totalSum)) {
            return false;
        }
        if (Double.doubleToLongBits(this.leftoverSum) != Double.doubleToLongBits(other.leftoverSum)) {
            return false;
        }
        if (this.payedInFull != other.payedInFull) {
            return false;
        }
        if (!Objects.equals(this.expenseType, other.expenseType)) {
            return false;
        }
        if (!Objects.equals(this.details, other.details)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.household, other.household)) {
            return false;
        }
        if (!Objects.equals(this.dueDate, other.dueDate)) {
            return false;
        }
        return true;
    }
    
    
    @Override
    public int compareTo(Expense o) {
      return getDueDate().compareTo(o.getDueDate());
    }

    @Override
    public String toString() {
        return "Expense{" + "id=" + id + ", household=" + household + ", expenseType=" + expenseType + ", totalSum=" + totalSum + ", leftoverSum=" + leftoverSum + ", payedInFull=" + payedInFull + ", details=" + details + ", dueDate=" + dueDate + '}';
    }
}
