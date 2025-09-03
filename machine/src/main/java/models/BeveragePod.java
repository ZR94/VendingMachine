package models;

public class BeveragePod {
	
    private Beverage beverage;  // Riferimento all'oggetto Beverage (FK su beverage_id)
    private Pod pod;            // Riferimento all'oggetto Pod (FK su pod_id)
    private int podRequired;    // Quante cialde servono per fare la bevanda

    public BeveragePod() {
    }

    public BeveragePod(Beverage beverage, Pod pod, int podRequired) {
        this.beverage = beverage;
        this.pod = pod;
        this.podRequired = podRequired;
    }

    public Beverage getBeverage() {
        return beverage;
    }

    public void setBeverage(Beverage beverage) {
        this.beverage = beverage;
    }

    public Pod getPod() {
        return pod;
    }

    public void setPod(Pod pod) {
        this.pod = pod;
    }

    public int getPodRequired() {
        return podRequired;
    }

    public void setPodRequired(int podRequired) {
        this.podRequired = podRequired;
    }

    @Override
    public String toString() {
        return "BeveragePod{" +
               "beverage=" + (beverage != null ? beverage.getName() : "null") +
               ", pod=" + (pod != null ? pod.getName() : "null") +
               ", podRequired=" + podRequired +
               '}';
    }
}

