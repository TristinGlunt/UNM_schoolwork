    #include <stdio.h>

    #define MAX_CHAR_COUNT 81
     
    /* 
    2d array initialized
    how many characters are currently in the char initialized
    */

    char sudokuArray[9][9];
    int endOfArray = 0;
    int charCount = 0;
     
    /*
    function that checks the line of input given
    */

    char checkStringInput()
    {
        /*
        define our getchar
        initialize it as 0 to enter the while loop
        */

        int c;
        int i, j;
        c = '0';
        
        /*
        while the input isn't a newline
        and it is not the end of the file
        */

        while(c != '\n' && c != EOF)
        {
            for(i = 0; i < 9; i++)
            {
                for(j = 0; j < 9; j++)
                {
                    /*
                    get the character,
                    if the character is not a period,
                    add that character to the array
                    */
                    c = getchar();
                    if(c != '.')
                        {
                            if(charCount < 81)
                            {
                            sudokuArray[i][j] = c;
                            }
                        }
                    /* 
                    if that character is a period,
                    add it to the array as a perdio
                    */
                    else if(c == '.')
                        {
                            if(charCount < 81)
                            {                            
                            sudokuArray[i][j] = '.';
                            }
                        }
                    else
                        {
                            endOfArray = 1;
                            break;
                        }
                    charCount++;
                 }
            }  
        }
        return '0';
    }
     
     
    int main()
    {
        /*
        if our function was too long
        then print out that function and
        print there was an error
        */
        int i, j;
        checkStringInput();
        for(i = 0; i < 9; i++)
        {
            for(j = 0; j < 9; j++)
            {
                printf("%c", sudokuArray[i][j]);
            }
        }
    }