/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

/**
 *
 * @author arthurmanoha
 */
public class Sudoku {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String gridToSolve = Grid.grid4;

        Grid g = new Grid(gridToSolve);
        g.solveWithBacktrack();
        g = new Grid(gridToSolve);
        g.solveByHand();
    }

}
