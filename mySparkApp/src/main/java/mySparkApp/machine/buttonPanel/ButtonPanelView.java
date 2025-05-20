package mySparkApp.machine.buttonPanel;

import java.util.Scanner;

import org.eclipse.paho.client.mqttv3.MqttException;

public class ButtonPanelView {

    private final int idMachine;
    private final Scanner scanner;
    private ButtonPanel buttonPanel;

    public ButtonPanelView(int idMachine) throws MqttException {
        this.idMachine = idMachine;
        this.scanner = new Scanner(System.in);
        this.buttonPanel = new ButtonPanel();
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
        view.runDisplay(idMachine);
	}

    public void runDisplay(int idMachine) {
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertCoin'");
    }

    private void showBalance() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showBalance'");
    }

    private void selectBeverage() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selectBeverage'");
    }

    private void refund() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refund'");
    }

    private int readchoice() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readchoice'");
    }

}
