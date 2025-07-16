package mySparkApp.machine;

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
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BeveragePod other = (BeveragePod) obj;
        if (beverage == null) {
            if (other.beverage != null)
                return false;
        } else if (!beverage.equals(other.beverage))
            return false;
        if (pod == null) {
            if (other.pod != null)
                return false;
        } else if (!pod.equals(other.pod))
            return false;
        if (podRequired != other.podRequired)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BeveragePod [beverage=" + beverage + ", pod=" + pod + ", podRequired=" + podRequired + "]";
    }

}
