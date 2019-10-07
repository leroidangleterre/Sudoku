/**
 * This file represents a square grid of size n.
 */
package sudoku;

/**
 *
 * @author arthurmanoha
 */
public class Grid {

    private int size;
    private int[][] tab;

    public Grid(int size) {
        this.size = size;
        this.tab = new int[size][];
        for (int i = 0; i < size; i++) {
            tab[i] = new int[size];
            for (int j = 0; j < size; j++) {
                tab[i][j] = 0;
            }
        }
    }

    public Grid() {
        this(9);
        initGrid("530070000" + "600195000" + "098000060"
                + "800060003" + "400803001" + "700020006"
                + "060000280" + "000419005" + "000080079");
        this.printGrid();
    }

    public final void initGrid(String text) {
        for (int i = 0; i < text.length(); i++) {
            int value = text.charAt(i) - '0';
            int row = i / size;
            int col = i - size * row;
            tab[row][col] = value;
        }
    }

    public void printGrid() {

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int value = tab[i][j];
                System.out.print(value == 0 ? "-" : value);
            }
            System.out.println("");
        }
    }

}
