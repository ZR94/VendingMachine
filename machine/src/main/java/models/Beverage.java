package models;

public class Beverage {
    private int beverageId;   // beverage_id
    private String name;      // name
    private double price;     // price

    public Beverage() {
    }

    public Beverage(int beverageId, String name, double price) {
        this.beverageId = beverageId;
        this.name = name;
        this.price = price;
    }

    public int getBeverageId() {
        return beverageId;
    }

    public void setBeverageId(int beverageId) {
        this.beverageId = beverageId;
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
    public String toString() {
        return "Beverage{" +
               "beverageId=" + beverageId +
               ", name='" + name + '\'' +
               ", price=" + price +
               '}';
    }
}

