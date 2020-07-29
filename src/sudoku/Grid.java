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

    // Flags that tell which values are still possible.
    // tabByHand[line][col][digit] tells if 'digit+1' is still allowed at that line and column.
    private boolean[][][] possibleValues;

    private boolean solvingManually;

    public Grid(int size) {
        this.size = size;
        this.tab = new int[size][];
        this.possibleValues = new boolean[size][][];
        this.maxIndex = size * size;
        this.variable = new boolean[size][];
        for (int i = 0; i < size; i++) {
            tab[i] = new int[size];
            possibleValues[i] = new boolean[size][];
            variable[i] = new boolean[size];
            for (int j = 0; j < size; j++) {
                tab[i][j] = 0;
                variable[i][j] = false;
                possibleValues[i][j] = new boolean[size];
                for (int k = 0; k < size; k++) {
                    possibleValues[i][j][k] = true;
                }
            }
        }
    }

    public Grid() {
        this(9);
        initGrid("480000000" + "000250100" + "000000904"
                + "004008010" + "010046030" + "008005070"
                + "000000502" + "000130800" + "960000000");
//        initGrid("000804000" + "200000006" + "403000901"
//                + "000060000" + "010000050" + "605908103"
//                + "080000060" + "006000800" + "109506204");
//        initGrid("530070000" + "600195000" + "098000060"
//                + "800060003" + "400803001" + "700020006"
//                + "060000280" + "000419005" + "000080079");
//        initGrid("800000000" + "000300092" + "003085000"
//                + "060200400" + "034000580" + "090800300"
//                + "009024000" + "000700014" + "300000000");
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
                // Only 'value' is allowed for this square.
                for (int k = 0; k < size; k++) {
                    possibleValues[row][col][k] = false;
                }
                possibleValues[row][col][value - 1] = true;
            }
        }
    }

    public void printGrid() {

        if (solvingManually) {
            System.out.println("Manual solve.");
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (tab[i][j] != 0) {
                        // Print the only value.
                        System.out.print("   <" + tab[i][j] + ">   ");
                    } else {
                        // Print all the possible values.
                        for (int k = 0; k < size; k++) {
                            if (possibleValues[i][j][k]) {
                                System.out.print((k + 1) + "");
                            } else {
                                System.out.print("-");
                            }
                        }
                    }
                    System.out.print(" ");
                }
                System.out.println("");
            }
        } else {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int value = tab[i][j];
                    System.out.print(value == 0 ? "-" : value);
                    if (3 * ((j + 1) / 3) == (j + 1)) {
                        System.out.print(" ");
                    }
                }
                System.out.println("");
            }
            System.out.println("...\n");
        }
        System.out.println(this.getScore() + " digits found.");
    }

    /**
     * Solve the grid using a backtracking algorithm. Start at the first empty
     * digit, find a possible value; go back if no value is possible.
     *
     * @return true when the gris has at least one solution, false when there is
     * no solution.
     */
    public boolean solve() {
        solvingManually = false;
        return solve(0);
    }

    public boolean solve(int index) {

        step++;

        if (index > maxDepthReached) {
            maxDepthReached = index;
        }

        System.out.println("index " + index + ", max depth: " + maxDepthReached + ", step: " + step);

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

        if (!variable[line][col]) {
            // Go to the next empty square
            return solve(index + 1);
        }

        while (tab[line][col] < 9) {
            tab[line][col]++;
            if (isCorrect() && solve(index + 1)) {
                return true;
            }
        }
        // Tested all values, none fits. Need to go backward.
        tab[line][col] = 0;
        return false;
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

    /**
     * Find and return the amount of known digits.
     *
     * @return the amount of known digits.
     */
    private int getScore() {
        int score = 0;
        for (int line = 0; line < size; line++) {
            for (int col = 0; col < size; col++) {
                if (tab[line][col] != 0) {
                    score++;
                }
            }
        }
        return score;
    }

    /*
     * Use manual methods to solve the grid.
     */
    public boolean solveByHand() {

        solvingManually = true;
        printGrid();

        // Apply the unicity rule as long as it finds new numbers.
        int scoreBefore, scoreAfter;
        do {
            scoreBefore = getScore();
            applyDigitUnicity();
            checkNewFoundValues();
            printGrid();
            scoreAfter = getScore();
        } while (scoreBefore != scoreAfter);

        return true;
    }

    /**
     * A digit already found may not exist anywhere else in the same line,
     * column of block.
     *
     * Make sure that each digit already found is removed from the possibilities
     * of the same line, column and block.
     */
    private void applyDigitUnicity() {
        for (int line = 0; line < size; line++) {
            for (int col = 0; col < size; col++) {
                applyDigitUnicity(line, col);
            }
        }
    }

    /**
     * Remove the digit located at the given line and column from the
     * possibilities in the same line, the same column and the same block.
     *
     * This method only applies when the digit at the given line and column was
     * already found.
     *
     * @param line
     * @param col
     */
    private void applyDigitUnicity(int line, int col) {

        // If a digit was found, the corresponding value in 'tab' is non-zero.
        int valueAtSpot = tab[line][col];

        if (valueAtSpot != 0) {

            for (int index = 0; index < size; index++) {
                if (index != col) {
                    // That value is no longer allowed anywhere else in this line.
                    possibleValues[line][index][valueAtSpot - 1] = false;
                }
                if (index != line) {
                    // That value is no longer allowed anywhere else in this column.
                    possibleValues[index][col][valueAtSpot - 1] = false;
                }
            }

            // Remove the value from the possibilities in the block.
            int blockLine = line / 3;
            int blockCol = col / 3;
            for (int newLine = 3 * blockLine; newLine < 3 * blockLine + 3; newLine++) {
                for (int newCol = 3 * blockCol; newCol < 3 * blockCol + 3; newCol++) {
                    if (newLine != line || newCol != col) {
                        // That value is no longer allowed anywhere else in this 3x3 block.
                        possibleValues[newLine][newCol][valueAtSpot - 1] = false;
                    }
                }
            }
        }
    }

    /**
     * Scan the grid for values that remain the only possible one for their
     * square.
     *
     */
    private void checkNewFoundValues() {
        for (int line = 0; line < size; line++) {
            for (int col = 0; col < size; col++) {
                int nbCandidates = 0;
                int newFoundValue = -1;

                for (int k = 0; k < size; k++) {
                    if (possibleValues[line][col][k]) {
                        // The value (k+1) is still a candidate.
                        nbCandidates++;
                        newFoundValue = k + 1;
                    }
                }
                if (nbCandidates == 1) {
                    // The value k is the only one that remains.
                    tab[line][col] = newFoundValue;
                }
            }
        }
    }

}
