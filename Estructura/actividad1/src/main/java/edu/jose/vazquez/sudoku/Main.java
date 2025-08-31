package edu.jose.vazquez.sudoku;
import edu.jose.vazquez.sudoku.ui.CLI;
import edu.jose.vazquez.sudoku.models.Sudoku;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.displayWelcomeMessage();
        cli.run();
    }
}

