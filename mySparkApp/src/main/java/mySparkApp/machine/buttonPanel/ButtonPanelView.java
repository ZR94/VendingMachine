package mySparkApp.machine.buttonPanel;

import java.util.List;
import java.util.Scanner;

import org.eclipse.paho.client.mqttv3.MqttException;

import mySparkApp.machine.Beverage;
import mySparkApp.machine.utils.MqttModClient;

public class ButtonPanelView {

    private final int idMachine;
    private final Scanner scanner;
    private ButtonPanel buttonPanel;

    public ButtonPanelView(int idMachine) {
        this.idMachine = idMachine;
        this.scanner = new Scanner(System.in);
		buttonPanel = new ButtonPanel(idMachine);
		buttonPanel.setupMQTT(); 
    }

    public static void main(String[] args) throws MqttException {
		/*
		 * if (args.length < 1) {
		 * System.out.println("Utilizzo corretto: java DisplayManager <idMachine>");
		 * return; }
		 */
		int idMachine = 100;
		try {
			// idMachine = Integer.parseInt(args[0]);
			System.out.println("La macchinetta si sta avviando");
		} catch (NumberFormatException e) {
			System.out.println("L'ID della macchina deve essere un numero intero!");
			return;
		}

        ButtonPanelView view = new ButtonPanelView(idMachine);
        view.runDisplay();
	}

    public void runDisplay() {
		System.out.println("===== Display Started =====");
		System.out.println("Coffe machine ID: " + idMachine);
		boolean exit = false;

		while (!exit) {
			Menu();
			int choice = readchoice();

			switch (choice) {
			case 1:
				insertCoin();
				break;
			case 2:
				showBalance();
				break;
			case 3:
				selectBeverage();
				break;
			case 4:
				refund();
				break;
			case 0:
				System.out.println("Exit...");
				exit = true;
				break;
			default:
				System.out.println("Choice not supported!");
				break;
			}
		}

		// Eventuali azioni di chiusura: disconnessioni, cleanup, ...
		System.out.println("Shutdown Coffee Machine...");
	}


    private void Menu() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'Menu'");
    }

    private void insertCoin() {
		if (!isMachineActive()) {
	        System.out.println("[DisplayManager] La macchina NON è in stato ACTIVE: operazione non disponibile.");
	        return;
	    }
		
		System.out.print("Inserisci l'importo in euro (es. 0.50, 1, 2): ");
		try {
			double importo = Double.parseDouble(scanner.nextLine());
			System.out.println("Hai inserito: " + importo + " euro");
			
			buttonPanel.handleInsertCoin(importo);
			// Simulazione di risposta:
			System.out.println("(Simulazione) - Credito aggiornato!");
		} catch (NumberFormatException e) {
			System.out.println("Formato importo non valido!");
		}
    }

    private void showBalance() {
		double credito = buttonPanel.getCurrentCredit();
		System.out.println("Credito attuale: " + credito + " euro");
    }

    private void selectBeverage() {
        // TODO Auto-generated method stub
		if (!isMachineActive()) {
	        System.out.println("[DisplayManager] La macchina NON è in stato ACTIVE: operazione non disponibile.");
	        return;
	    }
		
		System.out.println("\n-- Seleziona la bevanda desiderata --");
		// carico la lista delle bevande dal db
		List<Beverage> list = buttonPanel.getAllBeverage(idMachine);
		System.out.println("Bevande Disponibili:");
		list.forEach(beverage -> {
			System.out.println("   [" + beverage.getIdBeverage() + "] " + beverage.getName() + " ("
					+ beverage.getPrice() + " euro)");
		});
		System.out.print("Scelta: ");

		int scelta;
		try {
			scelta = Integer.parseInt(scanner.nextLine());
		} catch (NumberFormatException e) {
			scelta = -1;
		}

		if (scelta < 1 || scelta > list.size()) {
			System.out.println("Selezione non valida!");
			return;
		}
		
		String resultChoice = buttonPanel.handleBeverageChoice(scelta);

		// Eventualmente chiedi zucchero, ecc...
		// Qui facciamo una simulazione
		//System.out.println("(Simulazione) - Verifico credito disponibile e cialde in magazzino...");

		// Esempio esito fittizio
		//boolean erogazioneOk = true; // In futuro deciderai in base al controllo effettivo

		if ("OK".equals(resultChoice)) {
			System.out.println("Bevanda erogata correttamente!");
		} else {
			System.out.println("Impossibile erogare la bevanda: " + resultChoice);
		}
    }

    private void refund() {
		double creditoRestituito = buttonPanel.resetCurrCredit();
		System.out.println("Restituiti " + creditoRestituito + " euro al cliente.");
       
    }

    private int readchoice() {
		int scelta;
		try {
			scelta = Integer.parseInt(scanner.nextLine());
		} catch (NumberFormatException e) {
			scelta = -1; // Indichiamo scelta non valida
		}
		return scelta;
    }

	private boolean isMachineActive() {
	    String status = buttonPanel.getMachineStatus();
	    return "active".equalsIgnoreCase(status);
	}

}
