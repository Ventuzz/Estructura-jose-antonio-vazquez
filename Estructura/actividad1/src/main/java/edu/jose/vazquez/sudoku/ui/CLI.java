package edu.jose.vazquez.sudoku.ui;

import edu.jose.vazquez.sudoku.models.Tablero;
import edu.jose.vazquez.sudoku.process.Solution;

public class CLI {

    public void displayWelcomeMessage() {
        System.out.println("Welcome to the Sudoku Solver!");
    }


    public void showValidationResult(boolean isValid) {
        if (isValid) {
            System.out.println("The Sudoku solution is valid.");
        } else {
            System.out.println("The Sudoku solution is invalid.");
        }
    }

    public void run() {
        Tablero tablero = new Tablero();
        tablero.imprimir();
        // Aquí puedes agregar lógica para interactuar con el usuario y resolver el Sudoku
    }

}
