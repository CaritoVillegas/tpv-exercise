package oop.inheritance;

import oop.inheritance.core.TPVDisplay;
import oop.inheritance.core.TPVFactory;
import oop.inheritance.data.*;
import oop.inheritance.ingenico.*;
import oop.inheritance.verifone.v240m.VerifoneV240mDisplay;

import java.time.LocalDateTime;

public class Application {

    private CommunicationType communicationType = CommunicationType.ETHERNET;
    private SupportedTerminal supportedTerminal;
    private TPVFactory tvpfactory;

    public Application(SupportedTerminal supportedTerminal) {

        this.supportedTerminal = supportedTerminal;
        tvpfactory = new TPVFactory(supportedTerminal);
    }

    public void showMenu() {
        TPVDisplay tpvdisplay = tvpfactory.getDisplayINSTANCE();

        tpvdisplay.showMessage(5, 5, "MENU");
        tpvdisplay.showMessage(5, 10, "1. VENTA");
        tpvdisplay.showMessage(5, 13, "2. DEVOLUCION");
        tpvdisplay.showMessage(5, 16, "3. REPORTE");
        tpvdisplay.showMessage(5, 23, "4. CONFIGURACION");

    }

    public String readKey() {
        IngenicoKeyboard ingenicoKeyboard = new IngenicoKeyboard.getInstance();

        return ingenicoKeyboard.get();
    }

    public void doSale() {
        IngenicoCardSwipper cardSwipper = new IngenicoCardSwipper.getInstance();
        IngenicoChipReader chipReader = new IngenicoChipReader.getInstance();
        IngenicoDisplay ingenicoDisplay = new IngenicoDisplay.getInstance();
        IngenicoKeyboard ingenicoKeyboard = new IngenicoKeyboard.getInstance();
        Card card;

        do {
            card = cardSwipper.readCard();
            if (card == null) {
                card = chipReader.readCard();
            }
        } while (card == null);

        ingenicoDisplay.clear();
        ingenicoDisplay.showMessage(5, 20, "Capture monto:");

        String amount = ingenicoKeyboard.get(); //Amount with decimal point as string

        Transaction transaction = new Transaction();

        transaction.setLocalDateTime(LocalDateTime.now());
        transaction.setCard(card);
        transaction.setAmountInCents(Integer.parseInt(amount.replace(".", "")));

        TransactionResponse response = sendSale(transaction);

        if (response.isApproved()) {
            ingenicoDisplay.showMessage(5, 25, "APROBADA");
            printReceipt(transaction, response.getHostReference());
        } else {
            ingenicoDisplay.showMessage(5, 25, "DENEGADA");
        }
    }

    private void printReceipt(Transaction transaction, String hostReference) {
        IngenicoPrinter ingenicoPrinter = new IngenicoPrinter.getInstance();
        Card card = transaction.getCard();

        ingenicoPrinter.print(5, "APROBADA");
        ingenicoPrinter.lineFeed();
        ingenicoPrinter.print(5, card.getAccount());
        ingenicoPrinter.lineFeed();
        ingenicoPrinter.print(5, "" + transaction.getAmountInCents());
        ingenicoPrinter.lineFeed();
        ingenicoPrinter.print(5, "________________");

    }

    private TransactionResponse sendSale(Transaction transaction) {
        IngenicoEthernet ethernet = new IngenicoEthernet.getInstance();
        IngenicoModem modem = new IngenicoModem.getInstance();
        IngenicoGPS gps = new IngenicoGPS.getInstance();
        TransactionResponse transactionResponse = null;

        switch (communicationType) {
            case ETHERNET:
                ethernet.open();
                ethernet.send(transaction);
                transactionResponse = ethernet.receive();
                ethernet.close();
                break;
            case GPS:
                gps.open();
                gps.send(transaction);
                transactionResponse = gps.receive();
                gps.close();
                break;
            case MODEM:
                modem.open();
                modem.send(transaction);
                transactionResponse = modem.receive();
                modem.close();
                break;
        }

        return transactionResponse;
    }

    public void doRefund() {
    }

    public void printReport() {
    }

    public void showConfiguration() {
    }

    public void clearScreen() {
        if (supportedTerminal == SupportedTerminal.INGENICO) {
            IngenicoDisplay ingenicoDisplay = new IngenicoDisplay.getInstance();

            ingenicoDisplay.clear();
        } else {
            VerifoneV240mDisplay verifoneV240mDisplay = new VerifoneV240mDisplay();

            verifoneV240mDisplay.clear();
        }
    }
}
