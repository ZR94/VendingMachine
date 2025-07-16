package mySparkApp.machine;

public class Pod {

    private int idPod;
    private String name;
    private int currentQty;
    private int maxQty;
    
    public Pod(int idPod, String name, int currentQty, int maxQty) {
        this.idPod = idPod;
        this.name = name;
        this.currentQty = currentQty;
        this.maxQty = maxQty;
    }

    public int getIdPod() {
        return idPod;
    }

    public void setIdPod(int idPod) {
        this.idPod = idPod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getcurrentQty() {
        return currentQty;
    }

    public void setcurrentQty(int currentQty) {
        this.currentQty = currentQty;
    }

    public int getmaxQty() {
        return maxQty;
    }

    public void setmaxQty(int maxQty) {
        this.maxQty = maxQty;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pod other = (Pod) obj;
        if (idPod != other.idPod)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (currentQty != other.currentQty)
            return false;
        if (maxQty != other.maxQty)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Pod [idPod=" + idPod + ", name=" + name + ", currentQty=" + currentQty + ", maxQty=" + maxQty + "]";
    }

}
