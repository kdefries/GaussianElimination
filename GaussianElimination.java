/**
 * GaussianElimination inputs a users text file and uses a Scanner to read
 * the text file to create an augmented matrix. The augmented matrix is then
 * composed into reduced row echelon form.
 * Using back substitution, answers are calculated which appear in output.
 *
 * @version September 2018
 * @author Defries, Kevin
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Scanner;

public class GaussianElimination {
    private double[][] matrix;                     // m by n+1 augmented matrix
    private final int numberOfEquations;           // number of rows
    private final int columns;                     // number of rows + 1 (columns)
    private static final double EPSILON = 1e-8;


    /**
     * getFile() creates File and Scanner to read fileName
     * @param fileName input from user passed from main()
     * @throws FileNotFoundException Exception when user inputs wrong file name
     */
    private static void getFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner inputFile = new Scanner(file);

        new GaussianElimination(inputFile);
    }

    /**
     * GaussianElimination created augmented matrix from user text file
     * @param inputFile Scanner passed from getFile()
     */
    private GaussianElimination(Scanner inputFile){

        // Gets the first int of data file then nextLine()
        numberOfEquations = Integer.parseInt((inputFile.next()));
        inputFile.nextLine();

        columns = numberOfEquations + 1; // Declare columns = rows + 1

        // Build augmented matrix [numberOfEquations][columns]
        matrix = new double [numberOfEquations][columns];
        for (int i = 0; i < numberOfEquations; i++)
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = inputFile.nextDouble();
            }

        forwardElimination();

        checkSpecialCases();
    }

    /**
     * forwardElimination swaps rows and uses pivot points to
     * generate a matrix that is in reduced row echelon form
     */
    private void forwardElimination(){
        for (int p = 0; p < Math.min(numberOfEquations, columns); p++){

            // Find pivot row using partial pivoting
            int max = p;
            for (int i = p + 1; i < numberOfEquations; i++){
                if (Math.abs(matrix[i][p]) > Math.abs(matrix[max][p])){
                    max = i;
                }
            }

            // swap
            swap(p, max);

            if (Math.abs(matrix[p][p]) <= EPSILON) {
                continue;
            }

            // pivot
            pivot(p);

        }
    }

    /**
     * swap simply swaps the 2 rows passed into the method
     * @param row1 row to be swapped with row2
     * @param row2 row to be swapped with row1
     */
    private void swap(int row1, int row2) {
        double[] temp = matrix[row1];
        matrix[row1] = matrix[row2];
        matrix[row2] = temp;
    }

    /**
     * pivot uses the pivot point found in forwardElimination()
     * @param p the pivot point in matrix[p][p]
     */
    private void pivot(int p){
        for (int i = p + 1; i < numberOfEquations; i++){
            double alpha = matrix[i][p] / matrix[p][p];
            for ( int j = p; j < columns; j++){
                matrix[i][j] -= alpha * matrix[p][j];
            }
        }
    }

    /**
     * backSubstitution calculates the answers of the equations
     * using back substitution method
     * @return returns array of answers x
     */
    private double[] backSubstitution(){
        double[] x = new double[numberOfEquations];
        for (int i = Math.min(columns - 2, numberOfEquations - 1); i >= 0; i--){
            double sum = 0.0;
            for (int j = i + 1; j < columns - 1; j++){
                sum += matrix[i][j] * x[j];
            }

            if (Math.abs(matrix[i][i]) > EPSILON)
                x[i] = (matrix[i][numberOfEquations] - sum) / matrix[i][i];
            else if (Math.abs(matrix[i][numberOfEquations] - sum) > EPSILON)
                return null;
        }

        // redundant rows
        for (int i = columns - 1; i < numberOfEquations; i++){
            double sum = 0.0;
            for (int j = 0; j < numberOfEquations; j++){
                sum += matrix[i][j] * x[j];
            }
            if (Math.abs(matrix[i][numberOfEquations] - sum) > EPSILON)
                return null;
        }
        return x;
    }

    /**
     * isFeasible checks if there can be solutions
     * @return returns if backSubstitution is != null
     */
    private boolean isFeasible() {
        return backSubstitution() != null;
    }

    /**
     * isInfinite checks for infinitely many solutions
     * @param x the solved v values
     * @return returns true if x = 0, false if else
     */
    private boolean isInfinite(double[] x){
        assert x != null;
        for (int i = 0; i < numberOfEquations; i++){
            if (x[i] == 0) {
                System.out.println("\nInfinitely many Solutions");
                return true;
            }
        }
        return false;
    }

    /**
     * generateSubscript creates a string of subscript(i) for the
     * purpose of displaying xsub0 etc. in the output.
     * @param i the number to be made into a subscript string
     * @return returns the value i
     */
    private String generateSubscript(int i) {
        StringBuilder sb = new StringBuilder();
        for (char ch : String.valueOf(i).toCharArray()) {
            sb.append((char) ('\u2080' + (ch - '0')));
        }
        return sb.toString();
    }

    /**
     * checkSpecialCases checks for no solutions or infinite solutions
     * before using backSubstitution() to solve to system of equations.
     * If there are no special cases then the output is printed to console.
     * @return returns if system has no solution
     */
    private void checkSpecialCases(){
        if (!isFeasible()){
            System.out.println("\nNo Solution");
            return;
        }
        double[] x = backSubstitution();

        DecimalFormat df = new DecimalFormat("#.0");

        if(!isInfinite(x)) {
            for (int i = 0; i < numberOfEquations; i++) {
                System.out.print("X" + generateSubscript(i) + " = " + df.format(x[i]));
                System.out.println("\n");
            }
        }
    }

    /**
     * main method that starts GaussianElimination
     * @param args main args
     */
    public static void main(String[] args) {
        System.out.print("Enter the filename: ");
        Scanner scan =  new Scanner(System.in);
        String userInput = scan.nextLine();
        try {
            getFile(userInput);
        } catch (FileNotFoundException e) {
            System.out.println("\nFile not found: " + e.getLocalizedMessage());
        }
    }
}
