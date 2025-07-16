package mySparkApp.machine;

public enum Status {

    ACTIVE("active"),
    FAULTY("faulty"),
    LOW_PODS("low_pods"),
    FULL_CASH("full_cash");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Converte una stringa (ad es. letta dal DB) 
     * nello stato corrispondente. Lancia IllegalArgumentException
     * se non trova corrispondenze.
     */
    public static Status fromString(String status) {
        for (Status ms : Status.values()) {
            if (ms.value.equalsIgnoreCase(status)) {
                return ms;
            }
        }
        throw new IllegalArgumentException("Illegal status: " + status);
    }
}
