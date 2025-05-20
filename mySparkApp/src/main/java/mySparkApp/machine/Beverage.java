package mySparkApp.machine;

public class Beverage {
    
    private int idBeverage;
    private String name;
    private double price;
    private Pod pod;
    private int podQty;

    public Beverage() {}

    public Beverage(int idBeverage, String name, double price, Pod pod, int podQty) {
        this.idBeverage = idBeverage;
        this.name = name;
        this.price = price;
        this.pod = pod;
        this.podQty = podQty;
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

    public Pod getPod() {
        return pod;
    }

    public void setPod(Pod pod) {
        this.pod = pod;
    }

    public int getPodQty() {
        return podQty;
    }

    public void setPodQty(int podQty) {
        this.podQty = podQty;
    }

    

}
