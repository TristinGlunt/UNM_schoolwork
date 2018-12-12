#include <stdio.h>
#include <string.h>
#include <stdlib.h>

int convertBinary = 0;
int convertDecimal = 0;
int error = 0;
int newNumber;
int blehNewNumber;
int tooLongOfBit;

int numberOfBits = 0;

int userInput[20][20];
char number[19];
char bleh[19];



int convertDecimalFunction(int decimalToConvert);
int convertBinaryFunction(int binaryToConvert);
int errorPrint();



int main(int argc, char *argv[])
{
	int i, j, n, z, t;

	for(i = 0; i < argc; i++) 
	{
		for(j = 0, n = strlen(argv[i]); j < n; j++)
		{
			userInput[i][j] = argv[i][j];
			/*printf("userinput[%d][%d] = %c\n", i, j, userInput[i][j]);*/

		}
	}

	if(argc < 4)
	{
		printf("ERROR: incorrect number of arguments\n");
		errorPrint();
		return 0;
	}


	if(userInput[1][0] == '-')
	{
		if(userInput[1][1] == 'b')
		{
			convertBinary = 1;
		}
		else if(userInput[1][1] == 'd')
		{
			convertDecimal = 1;
		}
		else
		{
			printf("ERROR: argument 1 must be -b | -d\n");
			errorPrint();
			return 0;
		}
	}

	if(userInput[2][0] == '-')
	{
		if(userInput[2][1] == '8')
		{
			numberOfBits = 8;
			/*printf("input is an unsigned 8-bit integer\n");*/
		}
		else if(userInput[2][1] == '1')
		{
			numberOfBits = 16;
			/*printf("input is an unsigned 16-bit integer\n");*/
		}
		else if(userInput[2][1] == '3')
		{
			numberOfBits = 32;
			/*printf("input is an unsigned 32-bit integer\n");*/
		}
		else if(userInput[2][1] == '6')
		{
			numberOfBits = 64;
			/*printf("input is an unsigned 64-bit integer\n");*/
		}
		else
		{
			printf("ERROR: argument 2 must be: -8 | -16 | -32 | -64\n");
			errorPrint();
			return 0;
		}
	}

	for(z = 0; z < 20; z++)
	{
		if((userInput[3][z] < '0') || (userInput[3][z] > '9'))
		{
			error = 1;
		}
		else
		{

			number[z] = userInput[3][z];
			newNumber = atoi(number);
			/*printf("newNumber = %d\n", newNumber);*/
			tooLongOfBit = 0;
		}
	}
	if(convertDecimal == 1)
	{
		convertDecimalFunction(newNumber);
	}
	if(convertBinary == 1)
	{
		convertBinaryFunction(newNumber);
	}

}

int convertDecimalFunction(int decimalToConvert)
{
	int k, c;
	int binaryArray[numberOfBits];
	int counter = 0;


	for (c = numberOfBits-1; c >= 0; c--)
	{
		k = decimalToConvert >> c;

		if (k & 1)
			printf("1");
		else
			printf("0");
		if(c == 4 || c == 8 || c == 12 || c == 16 || c == 20 || c== 24 || c == 28
		|| c == 32 || c == 36 || c == 40 || c == 44 || c == 48 || c == 52
		|| c == 56 || c == 60 || c == 64)
			printf(" ");
	}

	printf("\n");

	return 0;
}

int convertBinaryFunction(int binaryToConvert)
{


	int binary_val, decimal_val = 0, base = 1, rem;

	binary_val = binaryToConvert;
	while (binaryToConvert > 0)
    {
        rem = binaryToConvert % 10;
        decimal_val = decimal_val + rem * base;
        binaryToConvert = binaryToConvert / 10 ;
        base = base * 2;
    }
    printf("%d\n", decimal_val);
}

int errorPrint()
{
	printf("usage:\n");
	printf("./binary OPTION SIZE NUMBER\n");
	printf("    OPTION:\n");
	printf("      -b NUMBER  binary and output will be in decimal.\n");
	printf("      -d NUMBER  decimal and output will be in binary.\n");
	printf("\n");
	printf("SIZE:\n");
	printf("  -8    input is an unsigned 8-bit ineger.\n");
	printf("  -16    input is an unsigned 16-bit ineger.\n");
	printf("  -32    input is an unsigned 32-bit ineger.\n");
	printf("  -64    input is an unsigned 64-bit ineger.\n");
	printf("\n");
	printf("NUMBER:\n");
	printf("  number to be converted.\n\n");
}