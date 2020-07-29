/**
 * This file represents a square grid of size n.
 */
package sudoku;

/**
 *
 * @author arthurmanoha
 */
public class Grid {

    private static int maxIndex = 0;

    private static int step = 0;
    private static int maxDepthReached = 0;

    private int size;

    // The values of the numbers in the grid
    private int[][] tab;

    // Flags that are true for the numbers we need to find, false for given values.
    private boolean[][] variable;

    public Grid(int size) {
        this.size = size;
        this.tab = new int[size][];
        this.maxIndex = size * size;
        this.variable = new boolean[size][];
        for (int i = 0; i < size; i++) {
            tab[i] = new int[size];
            variable[i] = new boolean[size];
            for (int j = 0; j < size; j++) {
                tab[i][j] = 0;
                variable[i][j] = false;
            }
        }
    }

    public Grid() {
        this(9);
//        initGrid("480000000" + "000250100" + "000000904"
//                + "004008010" + "010046030" + "008005070"
//                + "000000502" + "000130800" + "960000000");
//        initGrid("000804000" + "200000006" + "403000901"
//                + "000060000" + "010000050" + "605908103"
//                + "080000060" + "006000800" + "109506204");
//        initGrid("530070000" + "600195000" + "098000060"
//                + "800060003" + "400803001" + "700020006"
//                + "060000280" + "000419005" + "000080079");
        initGrid("000000000" + "000000000" + "000000000"
                + "000000000" + "000000000" + "000000000"
                + "000000000" + "000000000" + "000000000");
        this.printGrid();
    }

    public final void initGrid(String text) {
        for (int i = 0; i < text.length(); i++) {
            int value = text.charAt(i) - '0';
            int row = i / size;
            int col = i - size * row;
            tab[row][col] = value;

            if (value == 0) {
                variable[row][col] = true;
            } else {
                variable[row][col] = false;
            }
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
        System.out.println("...\n");
    }

    /**
     * Solve the grid using a backtracking algorithm. Start at the first empty
     * digit, find a possible value; go back if no value is possible.
     */
    public boolean solve() {
        return solve(0, true);
    }

    public boolean solve(int index, boolean forward) {

        System.out.println("index " + index);
        printGrid();

        int line = index / 9;
        int col = index - 9 * line;

        if (line >= 9) {
            System.out.println("Success !");
            printGrid();
            return true;
        } else if (line < 0) {
            System.out.println("No solution.");
            return false;
        }

        while (tab[line][col] < 9) {
            tab[line][col]++;
            if (isCorrect() && solve(index + 1, true)) {
                return true;
            }
        }
        // Tested all values, none fits. Need to go backward.
        tab[line][col] = 0;
        return solve(index - 1, false);
    }

    public boolean isCorrect() {

        if (!this.areColumnsCorrect()) {
            return false;
        }
        if (!this.areLinesCorrect()) {
            return false;
        }
        if (!areBlocksCorrect()) {
            return false;
        }
        return true;
    }

    private boolean areColumnsCorrect() {

        // Compare each value to each other value in the same column.
        // If a double is found, the columns are not correct.
        for (int col = 0; col < size; col++) {
            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    if (tab[i][col] != 0) { // Do not compare zero values
                        if (tab[i][col] == tab[j][col]) {
                            return false;
                        }
                    }
                }
            }
        }
        // At this point, all columns are correct.
        return true;
    }

    private boolean areLinesCorrect() {

        // Compare each value to each other value in the same line.
        // If a double is found, the lines are not correct.
        for (int line = 0; line < size; line++) {
            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    if (tab[line][i] != 0) { // Do not compare zero values
                        if (tab[line][i] == tab[line][j]) {
                            return false;
                        }
                    }
                }
            }
        }
        // At this point, all columns are correct.
        return true;
    }

    private boolean areBlocksCorrect() {

        // The number of blocks is also the size of each block.
        int nbBlocks = (int) Math.sqrt(size);

        for (int iBlock = 0; iBlock < nbBlocks; iBlock++) {
            for (int jBlock = 0; jBlock < nbBlocks; jBlock++) {

                if (!this.checkBlock(iBlock, jBlock)) {
                    return false;
                }
            }
        }
        // At this point, all blocks have passed the test.
        return true;
    }

    // Check the block (iBlock, jBlock);
    private boolean checkBlock(int iBlock, int jBlock) {

        int blockSize = (int) Math.sqrt(size);

        // The value being checked
        for (int line = iBlock * blockSize; line < (iBlock + 1) * blockSize; line++) {
            for (int col = jBlock * blockSize; col < (jBlock + 1) * blockSize; col++) {

                // We check the current value against each other value.
                for (int otherline = iBlock * blockSize; otherline < (iBlock + 1) * blockSize; otherline++) {
                    for (int othercol = jBlock * blockSize; othercol < (jBlock + 1) * blockSize; othercol++) {

                        // Do not check a value against itself.
                        if (line != otherline || col != othercol) { // Do not compare zero values.
                            if (tab[line][col] != 0) {
                                if (tab[line][col] == tab[otherline][othercol]) {
                                    // Found a double. Block is not correct.
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        // At this point, each block is correct.
        return true;
    }
}
