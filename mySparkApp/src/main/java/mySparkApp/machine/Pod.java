package mySparkApp.machine;

public class Pod {

    private int idPod;
    private String name;
    private int CurrentQty;
    private int MaxQty;
    
    public Pod(int idPod, String name, int CurrentQty, int MaxQty) {
        this.idPod = idPod;
        this.name = name;
        this.CurrentQty = CurrentQty;
        this.MaxQty = MaxQty;
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

    public int getCurrentQty() {
        return CurrentQty;
    }

    public void setCurrentQty(int currentQty) {
        CurrentQty = currentQty;
    }

    public int getMaxQty() {
        return MaxQty;
    }

    public void setMaxQty(int maxQty) {
        MaxQty = maxQty;
    }

    
}
