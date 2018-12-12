/*
*
*	@author: Tristin Glunt
*	tglunt@unm.edu
*
*/


/*
 * trans.c - Matrix transpose B = A^T
 *
 * Each transpose function must have a prototype of the form:
 * void trans(int M, int N, int A[N][M], int B[M][N]);
 *
 * A transpose function is evaluated by counting the number of misses
 * on a 1KB direct mapped cache with a block size of 32 bytes.
 */
#include <stdio.h>
#include "cachelab.h"

int is_transpose(int M, int N, int A[N][M], int B[M][N]);

/*
 * transpose_submit - This is the solution transpose function that you
 *     will be graded on for Part B of the assignment. Do not change
 *     the description string "Transpose submission", as the driver
 *     searches for that string to identify the transpose function to
 *     be graded.
 */
char transpose_submit_desc[] = "Transpose submission";
void transpose_submit(int M, int N, int A[N][M], int B[M][N])
{
	int block;
	if(M == 32)
		block = 8;
	else if(M == 64)
		block = 4;
	else
		block = 1;
    
	for (int i = 0; i < N; i += block)
    {
        for (int j = 0; j < M; j += block)
        {
            for (int k = i; k < i + block; ++k)
            {
				if(M == 32)
				{
					for (int l = j; l < j + block; l+=8)
                	{
						int tmp1 = A[k][l];
						int tmp2 = A[k][l+1];
						int tmp3 = A[k][l+2];
						int tmp4 = A[k][l+3];
						int tmp5 = A[k][l+4];
						int tmp6 = A[k][l+5];
						int tmp7 = A[k][l+6];
						int tmp8 = A[k][l+7];
		                B[l][k] = tmp1;
						B[l+1][k] = tmp2;
		                B[l+2][k] = tmp3;
						B[l+3][k] = tmp4;
		                B[l+4][k] = tmp5;
						B[l+5][k] = tmp6;
		                B[l+6][k] = tmp7;
						B[l+7][k] = tmp8;
					}
				}
				else if(M == 64)
				{
					for (int l = j; l < j + block; l+=4)
                	{
						int tmp1, tmp2, tmp3, tmp4;
						tmp1 = A[k][l];
						tmp2 = A[k][l+1];
						tmp3 = A[k][l+2];
						tmp4 = A[k][l+3];	
						B[l][k] = tmp1;
						B[l+1][k] = tmp2;
			            B[l+2][k] = tmp3;
						B[l+3][k] = tmp4;
					
					}
				}	
				else
				{
					for (int l = j; l < j + block; l++)
                	{
						B[l][k] = A[k][l];
					}					
                }
            }
        }
    }
/*
    int tmp, tmp2, tmp3;
    int i = 0;
    int j = 0;

    for (i = 0; i < N; i++)
    {
        for (j = 0; j < M; j+=3)
        {
            tmp = A[i][j];
			tmp2 = A[i][j+1];
			tmp3 = A[i][j+2];
//              tmp1 = A[i][j+1];
            B[j][i] = tmp;
			B[j+1][i] = tmp2;
			B[j+2][i] = tmp3;
//                B[j+1][i] = tmp1;
        }
//        for (j = 0; j < M; j++)
//        {
//            tmp = A[i+1][j];
//                tmp1 = A[i+1][j+1];
//            B[j][i+1] = tmp;
//                B[j+1][i+1] = tmp1;
//        }

            for (j = 0; j < M; j+=2)
            {
                tmp = A[i+2][j];
                tmp1 = A[i+2][j+1];
                B[j][i+2] = tmp;
                B[j+1][i+2] = tmp1;
            }

    }
*/
}

/*
 * You can define additional transpose functions below. We've defined
 * a simple one below to help you get started.
 *
 * trans - A simple baseline transpose function, not optimized for the cache.
 */
char trans_desc[] = "Simple row-wise scan transpose";
void trans(int M, int N, int A[N][M], int B[M][N])
{
    int i, j, tmp;

    for (i = 0; i < N; i++) {
        for (j = 0; j < M; j++) {
            tmp = A[i][j];
            B[j][i] = tmp;
        }
    }
}

char transBlockDesc[] = "block sizes";
void transBlocks(int M, int N, int A[N][M], int B[M][N])
{
// transpose a much smaller subset of the matrix
    int blocksize = 2;
    for (int i = 0; i < N; i += blocksize)
    {
        for (int j = 0; j < M; j += blocksize)
        {
            for (int k = i; k < i + blocksize; ++k)
            {
                for (int l = j; l < j + blocksize; ++l)
                {
                    B[l][k] = A[k][l];
                }
            }
        }
    }
}

/*
 * registerFunctions - This function registers your transpose
 *     functions with the driver.  At runtime, the driver will
 *     evaluate each of the registered functions and summarize their
 *     performance. This is a handy way to experiment with different
 *     transpose strategies.
 */
void registerFunctions()
{
    /* Register your solution function */
    registerTransFunction(transpose_submit, transpose_submit_desc);

    /* Register any additional transpose functions */
    registerTransFunction(trans, trans_desc);

    registerTransFunction(transBlocks, transBlockDesc);

}

/*
 * is_transpose - This helper function checks if B is the transpose of
 *     A. You can check the correctness of your transpose by calling
 *     it before returning from the transpose function.
 */
int is_transpose(int M, int N, int A[N][M], int B[M][N])
{
    int i, j;

    for (i = 0; i < N; i++) {
        for (j = 0; j < M; ++j) {
            if (A[i][j] != B[j][i]) {
                return 0;
            }
        }
    }
    return 1;
}
