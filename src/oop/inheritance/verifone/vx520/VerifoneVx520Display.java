package oop.inheritance.verifone.vx520;

public class VerifoneVx520Display {
    private boolean lightTurnedOn;
    private static VerifoneVx520Display uniqueInstance;

    private VerifoneVx520Display(){

    }

    public static VerifoneVx520Display getInstance(){

        if(uniqueInstance == null){
            uniqueInstance = new VerifoneVx520Display();
        }

        return uniqueInstance;
    }

    /**
     * Prints a message to specied position
     *
     * @param x       horizontal position
     * @param y       vertical position
     * @param message message to be printed
     */
    public void showMessage(int x, int y, String message) {
    }

    /**
     * Clears the screen
     */
    public void clear() {

    }
}
