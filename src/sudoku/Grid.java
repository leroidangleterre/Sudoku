/**
 * This file represents a square grid of size n.
 */
package sudoku;

/**
 *
 * @author arthurmanoha
 */
public class Grid {

    public static String diabolic1 = "502084600" + "004000008" + "300100000"
            + "030200006" + "070000090" + "600009030"
            + "000001009" + "800000500" + "006750204";
    public static String diabolic2 = "460502001" + "502000000" + "017900000"
            + "000009400" + "000275000" + "005100000"
            + "000003860" + "000000904" + "800604017";
    public static String expert1 = "800234005" + "304560000" + "750000364"
            + "008002400" + "000906500" + "003000200"
            + "040000070" + "080105040" + "030020050";
    public static String grid1 = "480000000" + "000250100" + "000000904"
            + "004008010" + "010046030" + "008005070"
            + "000000502" + "000130800" + "960000000";
    public static String grid2 = "000804000" + "200000006" + "403000901"
            + "000060000" + "010000050" + "605908103"
            + "080000060" + "006000800" + "109506204";
    public static String grid3 = "530070000" + "600195000" + "098000060"
            + "800060003" + "400803001" + "700020006"
            + "060000280" + "000419005" + "000080079";
    public static String grid4 = "800000000" + "000300092" + "003085000"
            + "060200400" + "034000580" + "090800300"
            + "009024000" + "000700014" + "300000000";

    private static int maxIndex = 0;

    private static int step = 0;
    private static int maxDepthReached = 0;

    private int size;

    // The values of the numbers in the grid; 0 for empty, 1-9 for found value.
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
    }

    public Grid(String text) {
        this(9);
        initGrid(text);
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

        if (solvingManually && !isSolved()) {
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
    public boolean solveWithBacktrack() {
        solvingManually = false;
        return solveWithBacktrack(0);
    }

    /**
     * Solve with backtrack algorithm, trying to find the number at position
     * 'index';
     *
     * @param index the index of the number we try to find while still having a
     * solution for the rest of the grid.
     * @return true if the grid has a valid solution with this number and all
     * the ones after.
     */
    private boolean solveWithBacktrack(int index) {

        step++;

        if (index > maxDepthReached) {
            maxDepthReached = index;
        }

        int line = index / 9;
        int col = index - 9 * line;

        if (line >= 9) {
            System.out.println("Success ! " + step + " steps, max depth reached: "
                    + maxDepthReached);
            printGrid();
            return true;
        } else if (line < 0) {
            System.out.println("No solution; " + step + " steps, "
                    + "max depth reached: " + maxDepthReached);
            return false;
        }

        if (!variable[line][col]) {
            // Go to the next empty square
            return solveWithBacktrack(index + 1);
        }

        while (tab[line][col] < 9) {
            tab[line][col]++;
            if (isCorrect() && solveWithBacktrack(index + 1)) {
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

    /**
     * Check that no value appears more than once in any column.
     *
     * @return true when all columns are correct (and possibly incomplete).
     */
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

    /**
     * Check that no value appears more than once in any line.
     *
     * @return true when all lines are correct (and possibly incomplete).
     */
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

    /**
     * Check that no value appears more than once in any block.
     *
     * @return true when all blocks are correct (and possibly incomplete).
     */
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

    /**
     * Check that each value has been found, i.e. no square remains unfilled.
     *
     * @return true when each square has been filled.
     */
    private boolean isFull() {
        boolean isFull = true;
        for (int line = 0; line < size; line++) {
            for (int col = 0; col < size; col++) {
                if (tab[line][col] == 0) {
                    // That square is empty.
                    isFull = false;
                }
            }
        }
        return isFull;
    }

    /**
     * Check that the grid is completely solved, i.e. at the same time full and
     * without mistakes
     *
     * @return true when grid is full and correct.
     */
    private boolean isSolved() {
        return isFull() && isCorrect();
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

    /**
     * Solve manually by applying several times the defined techniques.
     *
     * @return true when a solution was found.
     */
    public boolean solveByHand() {

        solvingManually = true;

        int step = 0;

        // Apply the unicity rule as long as it finds new numbers.
        int scoreBefore, scoreAfter;
        do {
            scoreBefore = getScore();
            solveByHandStep();
            scoreAfter = getScore();
            step++;
        } while (scoreBefore != scoreAfter);

        System.out.println("Solving manually: " + step + " steps.");
        printGrid();

        return isSolved();
    }

    /**
     * Apply once every technique. Digit unicity, position unicity
     */
    private void solveByHandStep() {
        applyDigitUnicity();
        checkNewFoundValues();
        applyPositionUnicity();
        checkNewFoundValues();
        applyDigitUnicity();
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
     * A digit that exists at only one position in a line, column or block must
     * be validated for that position.
     */
    private void applyPositionUnicity() {
        for (int line = 0; line < size; line++) {
            for (int col = 0; col < size; col++) {
                applyPositionUnicity(line, col);
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
     * Example:
     * <4> <8> 123456789 123456789 123456789 123456789 123456789 123456789
     * 123456789 becomes
     * <4> <8> 123-567-9 123-567-9 123-567-9 123-567-9 123-567-9 123-567-8
     * 123-567-9
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

    /**
     * If a digit appears only once in a line, col or block, it is validated for
     * the given position.
     *
     * Example: on the line --3--67-9 <2> --3--678- --3---7-9 ------7-9 --3-----
     * ---4-6789 <1> ---45-78- , the value '5' is only possible on the last
     * square; the line then becomes --3--67-9 <2> --3--678- --3---7-9 ------7-9
     * --3----- ---4-6789 <1> <5>
     *
     * @param line
     * @param col
     */
    private void applyPositionUnicity(int line, int col) {
        applyPositionUnicityInLine(line, col);
        applyPositionUnicityInColumn(line, col);
    }

    private void applyPositionUnicityInLine(int line, int col) {
        boolean isUnique = true;

        int electedCandidate = -1;

        for (int candidate = 0; candidate < size; candidate++) {
            electedCandidate = candidate;
            if (possibleValues[line][col][candidate]) {
                // Check if the candidate has another possible square to go
                for (int k = 0; k < size; k++) {
                    if (possibleValues[line][k][candidate] && k != col) {
                        // We found another spot where 'candidate' might go on the same line.
                        isUnique = false;
                    }
                }
            }
        }
        if (isUnique) {
            // At this point the elected candidate has a proper value.
            tab[line][col] = electedCandidate;
        }
    }

    /**
     * If one candidate in given square is posiible nowhere else in the column,
     * it gets validated for that square.
     *
     * @param line
     * @param col
     */
    private void applyPositionUnicityInColumn(int line, int col) {

        if (tab[line][col] == 0) { // Only when this square has not been resolved.

            boolean isUnique;

            for (int candidate = 0; candidate < size; candidate++) {
                if (possibleValues[line][col][candidate]) {
                    isUnique = true;
                    // Check if the candidate has another possible square to go
                    for (int otherLine = 0; otherLine < size; otherLine++) {
                        if (possibleValues[otherLine][col][candidate] && otherLine != line) {
                            // We found another spot where 'candidate' might go on the same line.
                            isUnique = false;
                        }
                    }
                    if (isUnique) {
                        tab[line][col] = candidate;

                        // Only the elected value must remain in the possible values.
                        for (int k = 0; k < size; k++) {
                            possibleValues[line][col][k] = false;
                        }
                        possibleValues[line][col][candidate] = true;
                        // At this point the elected candidate should have a proper value and not be -1;
                    }
                }
            }
        }
    }
}
