/**
 * This file represents a square grid of size n.
 */
package sudoku;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author arthurmanoha
 */
public class Grid {

    // Diabolic
    public static String grid1 = "502084600" + "004000008" + "300100000"
            + "030200006" + "070000090" + "600009030"
            + "000001009" + "800000500" + "006750204";
    // Diabolic
    public static String grid2 = "460502001" + "502000000" + "017900000"
            + "000009400" + "000275000" + "005100000"
            + "000003860" + "000000904" + "800604017";
    // Expert
    public static String grid3 = "800234005" + "304560000" + "750000364"
            + "008002400" + "000906500" + "003000200"
            + "040000070" + "080105040" + "030020050";
    // Not rated
    public static String grid4 = "480000000" + "000250100" + "000000904"
            + "004008010" + "010046030" + "008005070"
            + "000000502" + "000130800" + "960000000";
    // Not rated
    public static String grid5 = "000804000" + "200000006" + "403000901"
            + "000060000" + "010000050" + "605908103"
            + "080000060" + "006000800" + "109506204";
    // Not rated
    public static String grid6 = "530070000" + "600195000" + "098000060"
            + "800060003" + "400803001" + "700020006"
            + "060000280" + "000419005" + "000080079";
    // Not rated
    public static String grid7 = "800000000" + "000300092" + "003085000"
            + "060200400" + "034000580" + "090800300"
            + "009024000" + "000700014" + "300000000";

    private static int maxIndex = 0;

    private static int step = 0;
    private static int maxDepthReached = 0;

    // Number of columns, or digits.
    private int size;

    // The values of the numbers in the grid; 0 for empty, 1-9 for found value.
    private int[][] solvedDigits;

    // Flags that are true for the numbers we need to find, false for given values.
    private boolean[][] variable;

    // Flags that tell which values are still possible.
    // possibleValues[line][col][digit] tells if 'digit+1' is still allowed at that line and column.
    private boolean[][][] possibleValues;

    private boolean solvingManually;

    // Score at the beginning, i.e. how many digits are aready in place.
    private int initialScore;

    public Grid(int size) {
        this.size = size;
        this.solvedDigits = new int[size][];
        this.possibleValues = new boolean[size][][];
        this.maxIndex = size * size;
        this.variable = new boolean[size][];
        for (int i = 0; i < size; i++) {
            solvedDigits[i] = new int[size];
            possibleValues[i] = new boolean[size][];
            variable[i] = new boolean[size];
            for (int j = 0; j < size; j++) {
                solvedDigits[i][j] = 0;
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
            solvedDigits[row][col] = value;

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
        initialScore = getScore();
    }

    public void printGrid() {

        if (solvingManually && !isSolved()) {
            for (int line = 0; line < size; line++) {
                for (int col = 0; col < size; col++) {
                    if (solvedDigits[line][col] != 0) {
                        // Print the only value.
                        System.out.print("   <" + solvedDigits[line][col] + ">   ");
                    } else {
                        // Print all the possible values.
                        for (int k = 0; k < size; k++) {
                            if (possibleValues[line][col][k]) {
                                System.out.print((k + 1) + "");
                            } else {
                                System.out.print("-");
                            }
                        }
                    }
                    if (3 * ((col + 1) / 3) == (col + 1)) {
                        System.out.print(" || ");
                    } else {
                        System.out.print(" ");
                    }
                }
                System.out.println("");
                if (3 * ((line + 1) / 3) == (line + 1)) {
                    System.out.println("--------------------------------------------------------------------------------------------------");
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int value = solvedDigits[i][j];
                    System.out.print(value == 0 ? "-" : value);
                    if (3 * ((j + 1) / 3) == (j + 1)) {
                        System.out.print(" ");
                    }
                }
                System.out.println("");
            }
            System.out.println("...\n");
        }
        System.out.println((this.getScore() - initialScore) + " digits found, "
                + (81 - this.getScore()) + " remaining.");
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

        while (solvedDigits[line][col] < 9) {
            solvedDigits[line][col]++;
            if (isCorrect() && solveWithBacktrack(index + 1)) {
                return true;
            }
        }
        // Tested all values, none fits. Need to go backward.
        solvedDigits[line][col] = 0;
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
                    if (solvedDigits[i][col] != 0) { // Do not compare zero values
                        if (solvedDigits[i][col] == solvedDigits[j][col]) {
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
                    if (solvedDigits[line][i] != 0) { // Do not compare zero values
                        if (solvedDigits[line][i] == solvedDigits[line][j]) {
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
                if (solvedDigits[line][col] == 0) {
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
                            if (solvedDigits[line][col] != 0) {
                                if (solvedDigits[line][col] == solvedDigits[otherline][othercol]) {
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
                if (solvedDigits[line][col] != 0) {
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

        printGrid();

        step = 0;

        // Apply the unicity rule as long as it finds new numbers.
        int scoreBefore, scoreAfter;
        do {
            scoreBefore = getScore();
            solveByHandStep();
            scoreAfter = getScore();
            step++;
        } while (step < 1);
//        } while (scoreBefore != scoreAfter);

        System.out.println("Solving manually: " + step + " steps.");
        printGrid();

        if (isSolved()) {
            System.out.println("grid solved");
        } else if (isFull()) {
            System.out.println("grid full but not solved.");
        }
        return isSolved();
    }

    /**
     * Apply once every technique. Digit unicity, position unicity
     */
    private void solveByHandStep() {

        try {
            while (true) {
                printGrid();
                System.out.println("**************");
                System.out.println("**************");
                System.out.println("*** STEP " + step + " ***");
                System.out.println("**************");
                System.out.println("**************");

                ruleOne();
                ruleTwo();
                ruleThree();

                System.out.println("Press any key.");
                System.in.read();
            }
        } catch (IOException ex) {
            Logger.getLogger(Grid.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Rule one: if a square contains a solved digit, then that digit may no
     * longer be candidate in the same line/col/block.
     */
    private void ruleOne() {
        for (int line = 0; line < size; line++) {
            for (int col = 0; col < size; col++) {
                if (solvedDigits[line][col] != 0) {
                    eliminateCandidate(line, col);
                }
            }
        }
    }

    /**
     * Rule two: A digit that is the only candidate for a given square will be
     * elected.
     *
     */
    private void ruleTwo() {
        for (int line = 0; line < size; line++) {
            for (int col = 0; col < size; col++) {
                validateOnlyCandidate(line, col);
            }
        }
    }

    /**
     * Rule three: a digit that has only one place to go (in a given line, or
     * col, or block) will be elected.
     *
     */
    private void ruleThree() {
        for (int line = 0; line < size; line++) {
            for (int col = 0; col < size; col++) {
                for (int candidate = 1; candidate <= 9; candidate++) {

                    // Examine only the digits that still are candidates.
                    if (possibleValues[line][col][candidate - 1]) {
                        if (candidateHasOnlyOnePlaceToGo(candidate, line, col)) {
                            solvedDigits[line][col] = candidate;
                        }
                    }
                }
            }
        }
    }

    /**
     * The value at (line, col) may no longer be a candidate in the given line
     * and column, or in the corresponding block.
     *
     * @param line
     * @param col
     */
    private void eliminateCandidate(int line, int col) {
        int digit = solvedDigits[line][col];

        for (int i = 0; i < size; i++) {
            // Line:
            possibleValues[line][i][digit - 1] = false;
            // Column:
            possibleValues[i][col][digit - 1] = false;
        }
        // Block:
        int blockLine = line / 3;
        int blockCol = col / 3;
        int blockSize = (int) Math.sqrt(size);
        for (int i = blockSize * blockLine; i < blockSize * blockLine + blockSize; i++) {
            for (int j = blockSize * blockCol; j < blockSize * blockCol + blockSize; j++) {
                possibleValues[i][j][digit - 1] = false;
            }
        }
    }

    /**
     * In a square not yet validated, if there is only one candidate, it is
     * elected.
     *
     * @param line
     * @param col
     */
    private void validateOnlyCandidate(int line, int col) {
        if (solvedDigits[line][col] == 0) {
            // Count the candidates
            int nbCandidates = 0;
            int greatestCandidateValue = 0; // The last candidate will be stored here.

            for (int i = 0; i < 9; i++) {
                if (possibleValues[line][col][i] == true) {
                    nbCandidates++;
                    greatestCandidateValue = i + 1;
                }
            }
            if (nbCandidates == 1) {
                // Elect that candidate.
                solvedDigits[line][col] = greatestCandidateValue;
            }
        }
    }

    /**
     * Test if a given digit may be positioned somewhere else in the same
     * line/col/block, or if (line,col) is the only possible position.
     *
     * @param candidate the digit we test (1->9)
     * @param line
     * @param col
     * @return true when there is at least one set (i.e. line, column or block)
     * where the digit has only one place to go, even though there might be two
     * available positions in another set.
     */
    private boolean candidateHasOnlyOnePlaceToGo(int candidate, int line, int col) {

        int nbAvailablePositions;

        // Test the line.
        nbAvailablePositions = 0;
        for (int otherCol = 0; otherCol < size; otherCol++) {
            if (possibleValues[line][otherCol][candidate - 1]) {
                nbAvailablePositions++;
            }
        }
        if (nbAvailablePositions == 1) {
            // Candidate has only one possible position in this line; it is solved.
            return true;
        }

        // Test the column.
        nbAvailablePositions = 0;
        for (int otherLine = 0; otherLine < size; otherLine++) {
            if (possibleValues[otherLine][col][candidate - 1]) {
                nbAvailablePositions++;
            }
        }
        if (nbAvailablePositions == 1) {
            // Candidate has only one possible position in this line; it is solved.
            return true;
        }

        // Test the block
        nbAvailablePositions = 0;
        int blockLine = line / 3;
        int blockCol = col / 3;
        int blockSize = (int) Math.sqrt(size);
        for (int i = blockSize * blockLine; i < blockSize * blockLine + blockSize; i++) {
            for (int j = blockSize * blockCol; j < blockSize * blockCol + blockSize; j++) {
                if (possibleValues[i][j][candidate - 1]) {
                    nbAvailablePositions++;
                }
            }
        }
        if (nbAvailablePositions == 1) {
            // Candidate has only one possible position in this block; it is solved.
            return true;
        }

        // Candidate has at least two possible places in both the line, the column and the block; no way to tell yet.
        return false;
    }
}
