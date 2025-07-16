package mySparkApp.machine;

public class Beverage {
    
    private int idBeverage;
    private String name;
    private double price;


    public Beverage() {}

    public Beverage(int idBeverage, String name, double price) {
        this.idBeverage = idBeverage;
        this.name = name;
        this.price = price;

    }

    public int getIdBeverage() {
        return idBeverage;
    }

    public void setIdBeverage(int idBeverage) {
        this.idBeverage = idBeverage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Beverage other = (Beverage) obj;
        if (idBeverage != other.idBeverage)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Beverage [idBeverage=" + idBeverage + ", name=" + name + ", price=" + price + "]";
    }
  
}
