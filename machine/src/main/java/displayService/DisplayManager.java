package displayService;

import java.util.List;
import java.util.Scanner;

import models.Beverage;

public class DisplayManager {

	private final int machineId;
	private final Scanner scanner;
	private DisplayController controller;

	// Costruttore che inizializza lo scanner e salva l'ID della macchina
	public DisplayManager(int machineId) {
		this.machineId = machineId;
		this.scanner = new Scanner(System.in);
		this.controller = new DisplayController(machineId);
		controller.setupMQTT();
	}

	// Metodo main per avviare il servizio da riga di comando
	public static void main(String[] args) {
		/*
		 * if (args.length < 1) {
		 * System.out.println("Utilizzo corretto: java DisplayManager <machineId>");
		 * return; }
		 */
		int machineId = 100;
		try {
			// machineId = Integer.parseInt(args[0]);
			System.out.println("La macchinetta si sta avviando");
		} catch (NumberFormatException e) {
			System.out.println("L'ID della macchina deve essere un numero intero!");
			return;
		}

		DisplayManager display = new DisplayManager(machineId);
		display.runDisplay();
	}

	// Metodo principale per l'esecuzione del servizio "display"
	public void runDisplay() {
		System.out.println("===== Display Manager Avviato =====");
		System.out.println("Macchina del caffè ID: " + machineId);
		boolean exit = false;

		while (!exit) {
			stampaMenu();
			int scelta = leggiScelta();

			switch (scelta) {
			case 1:
				inserisciDenaro();
				break;
			case 2:
				mostraCreditoCorrente();
				break;
			case 3:
				selezionaBevanda();
				break;
			case 4:
				recuperaCredito();
				break;
			case 0:
				System.out.println("Uscita dal Display Service...");
				exit = true;
				break;
			default:
				System.out.println("Scelta non valida!");
				break;
			}
		}

		// Eventuali azioni di chiusura: disconnessioni, cleanup, ...
		System.out.println("Chiusura DisplayManager completata.");
	}

	// Stampa le opzioni principali disponibili
	private void stampaMenu() {
		System.out.println("\n--------------------------------------------------");
		System.out.println("    1) Inserisci denaro");
		System.out.println("    2) Mostra credito corrente");
		System.out.println("    3) Seleziona una bevanda");
		System.out.println("    4) Recupera credito (Pulsante Rosso)");
		System.out.println("    0) Esci");
		System.out.println("--------------------------------------------------");
		System.out.print("Digita il numero dell'operazione desiderata: ");
	}

	// Legge la scelta dell’utente (intero)
	private int leggiScelta() {
		int scelta;
		try {
			scelta = Integer.parseInt(scanner.nextLine());
		} catch (NumberFormatException e) {
			scelta = -1; // Indichiamo scelta non valida
		}
		return scelta;
	}

	// Esempio di gestione "inserisci denaro"
	protected void inserisciDenaro() {
		if (!isMachineActive()) {
	        System.out.println("[DisplayManager] La macchina NON è in stato ACTIVE: operazione non disponibile.");
	        return;
	    }
		
		System.out.print("Inserisci l'importo in euro (es. 0.50, 1, 2): ");
		try {
			double importo = Double.parseDouble(scanner.nextLine());
			System.out.println("Hai inserito: " + importo + " euro");
			
			controller.handleInsertCoin(importo);
			// Simulazione di risposta:
			System.out.println("(Simulazione) - Credito aggiornato!");
		} catch (NumberFormatException e) {
			System.out.println("Formato importo non valido!");
		}
	}

	// Esempio di gestione "mostra credito"
	protected void mostraCreditoCorrente() {
		
		double credito = controller.getCurrentCredit();
		System.out.println("Credito attuale: " + credito + " euro");
	}

	// Esempio di gestione "seleziona una bevanda"
	protected void selezionaBevanda() {
		if (!isMachineActive()) {
	        System.out.println("[DisplayManager] La macchina NON è in stato ACTIVE: operazione non disponibile.");
	        return;
	    }
		
		System.out.println("\n-- Seleziona la bevanda desiderata --");
		// carico la lista delle bevande dal db
		List<Beverage> list = controller.getAllBeverage();
		System.out.println("Bevande Disponibili:");
		list.forEach(beverage -> {
			System.out.println("   [" + beverage.getBeverageId() + "] " + beverage.getName() + " ("
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
		
		String resultChoice = controller.handleBeverageChoice(scelta);

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

	// Esempio di gestione "recupera credito"
	protected void recuperaCredito() {
		
		double creditoRestituito = controller.resetCurrCredit();
		System.out.println("Restituiti " + creditoRestituito + " euro al cliente.");
	}
	
	
	// UTILS
	
	private boolean isMachineActive() {
	    String status = controller.getMachineStatus();
	    return "active".equalsIgnoreCase(status);
	}


}
