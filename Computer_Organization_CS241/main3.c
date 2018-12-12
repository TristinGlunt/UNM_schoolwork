#include <stdio.h>

int isValid(currentPuzzle[9][9], int row, int col, int num)
{
    int rowBox = (row/3) *3;
    int colBox = (col/3) *3;
    
    int i;
    
    for(i=0; i<9; ++i)
    {
        if (puzzle[row][i] == num) return 0;
        if (puzzle[i][col] == num) return 0;
        if (puzzle[rowBox + (i%3)][colBox + (i/3)] == num) return 0;
    }
    return 1;
}


/* readPuzzle() function fills in the array puzzle[][] with sudoku values from 
 * standard in.  If it finds a value that is not . or 1-9, then it returns
 * TRUE as an error code.  If a puzzle consists of some quantity other than
 * 81 entries, it returns TRUE as an error code.  If there are no errors
 * in the quantity or range of values, it returns FALSE.  
 * As required by the spec, puzzle is echoed to standard out regardless of
 * whether it contains errors or not.
 */
 
 
/* findRowErrors() function checks to see if there is a duplicate entry in any
 * row.  If there is, it returns TRUE to signal an error, otherwise returns
 * FALSE for no error.
 */
 
 /* findColErrors() function checks to see if there is a duplicate entry in any
 * column.  If there is, it returns TRUE to signal an error, otherwise returns
 * FALSE for no error.
 */
 
 /* findBoxErrors() function checks to see if there is a duplicate entry in any
 * box.  If there is, it returns TRUE to signal an error, otherwise returns
 * FALSE for no error.
 */
 
 /* noSolution() function checks to see if the puzzle that came back from
 * solvePuzzle doesn't have any 0's, which means that no solution was
 * found.  If the puzzle does not have a solution, it prints "No solution"
 * and returns TRUE.  Otherwise, it returns FALSE.
 */
 
 /* writePuzzle() function writes out the sudoku puzzle from the array 
 * puzzle[][].
 * If DEBUG is TRUE, then it writes out the puzzle in standard sudoku form. 
 * If DEBUG is FALSE, it writes out the puzzle in the form required by the spec.
 */
 
 /* solvePuzzle() function is the recursive function at the heart of solving
 * the sudoku puzzles once they have been screened for errors.
 */
 
 /* isSafe() function is a helper function to solvePuzzle which determines 
 * whether num is a safe number to put into the puzzle at the index.
 */


int main()
{
    printf("Hello, World!\n");

    return 0;
}
