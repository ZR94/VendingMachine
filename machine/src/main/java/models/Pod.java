package models;

public class Pod {
    private int podId;        // pod_id
    private String name;      // name
    private int currentQty;   // current_qty
    private int maxQty;       // max_qty

    public Pod() {
    }

    public Pod(int podId, String name, int currentQty, int maxQty) {
        this.podId = podId;
        this.name = name;
        this.currentQty = currentQty;
        this.maxQty = maxQty;
    }

    public int getPodId() {
        return podId;
    }

    public void setPodId(int podId) {
        this.podId = podId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCurrentQty() {
        return currentQty;
    }

    public void setCurrentQty(int currentQty) {
        this.currentQty = currentQty;
    }

    public int getMaxQty() {
        return maxQty;
    }

    public void setMaxQty(int maxQty) {
        this.maxQty = maxQty;
    }

    @Override
    public String toString() {
        return "Pod{" +
               "podId=" + podId +
               ", name='" + name + '\'' +
               ", currentQty=" + currentQty +
               ", maxQty=" + maxQty +
               '}';
    }
}

