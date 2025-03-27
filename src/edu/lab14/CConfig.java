package edu.lab14;

public class CConfig {

    public final static String waitingString = "Czekamy na przeciwnika...";
    public final static String waitingOpString = "Przeciwnik wykonuje ruch";
    public final static String comErrorString = "Błąd komunikacji :(";
    public final static String wonString = "Wygrałeś !";
    public final static String enemyWonString = "Wygrał przeciwnik !";
    public final static String tieString = "Mamy remis";

    public final static int WIDTH = 480;
    public final static int HEIGHT = 480;

    public static String[] board = new String[9];

    public static boolean yourTurn = false;
    public static boolean accepted = false;
    public static boolean comError = false;
    public static boolean won = false;
    public static boolean enemyWon = false;
    public static boolean tie = false;

    public static boolean circle = true;

    public static int errors = 0;
    public static volatile boolean threadRunning = false;

    public static String ip = "localhost";
    public final static int port = 23456;

    public static int[] line = new int[2];
    public static final int[][] wins = new int[][] {
            {0, 1, 2}, {3, 4, 5},  {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0,4,8},{2,4,6} };

    public static void reset() {
        errors = 0;
        yourTurn = false;
        comError = false;
        won = false;
        enemyWon = false;
        tie = false;
        board = new String[9];
        line = new int[2];
    }

}
