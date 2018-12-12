#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define ERROR 0
#define SUCCESS 1

int flag;

int checkCmdLine(int argc, char **argv)
{
    if(argc != 2)
    {
        flag = ERROR;
        return flag;
    }
}


int main(int argc, char **argv)
{
    if(!checkCmdLine(argc, argv))
    {
        printf("Error\n");
    }
}
