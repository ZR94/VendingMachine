package models;

public enum StatusMachine {
	
	ACTIVE("active"),
    FAULTY("faulty"),
    LOW_PODS("low_pods"),
    FULL_CASH("full_cash");

    private final String value;

    StatusMachine(String value) {
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
    public static StatusMachine fromString(String status) {
        for (StatusMachine ms : StatusMachine.values()) {
            if (ms.value.equalsIgnoreCase(status)) {
                return ms;
            }
        }
        throw new IllegalArgumentException("Stato macchina non valido: " + status);
    }
}
