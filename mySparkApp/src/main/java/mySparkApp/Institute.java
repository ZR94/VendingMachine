package mySparkApp;


public class Institute {

    private int instituteId;
    private String name;

    public Institute(String name) {
        this.name = name;
    }

    public Institute(int instituteId, String name) {
        this.instituteId = instituteId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getInstituteId() {
        return instituteId;
    }

    @Override
    public String toString() {
        return "Institute [instituteId=" + instituteId + ", name=" + name + "]";
    }

}
