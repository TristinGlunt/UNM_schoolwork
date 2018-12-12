#include <stdio.h>
#include <ctype.h>
#include "lcg.h"
 
#define SUCCESS 0
#define ERROR 1
#define NLERR 2
#define ENCRYPT 3
#define DECRYPT 4
 
int errorFlag;

int readCurrentLine();
int readDirection();
int readUL();
void finishLine();
int encrypt();
int decrypt();
 
/******************************************/
/*  Read current line from standard in    */
/*                                        */
/*                                        */
/******************************************/
int readCurrentLine()
{
   unsigned long c, m;
   printf("beginning of readCurrentLine\n"); 
   if((errorFlag = readDirection()) != SUCCESS ) { printf("\nreturning error in readDirection\n"); return ERROR;}
   printf("test passed for readDirection\n"); 
   if((errorFlag = readUL(&m)) != SUCCESS) { return ERROR;}
   printf("test passed for number check for modulus m\n");
   if((errorFlag = readUL(&c)) != SUCCESS) { return ERROR;}
   printf("test passed for number check for increment c\n");
 
   /*
   if readDirection is E then encrypt
   if readDirection is D then decrypt */
    if((readDirection()) == ENCRYPT)
    {
        printf("we are in the encrypt part :D");
        encrypt(&c);
    }
    else if((readDirection()) == DECRYPT)
    {
        printf("We are in the decrypt part!");
        decrypt(&c);
    }
    else{return ERROR;}
}
/*function to determine whether each line encodes
a valid direction(encryption[e] or decryption[d]*/
 
int readDirection()
{
    printf("in read direction\n");
    int c;
    c = getchar();
    putchar(c);
    if(c == 'e') { return SUCCESS;}
    else if(c == 'd') { return SUCCESS;}
    else if(c == '\n') {return NLERR;}
    else if(c == EOF) {return ERROR;}
    else{ return ERROR;}
}
 
/*readUL(unsigned long *num) takes in the address of the key or the data
will check for errors. (used for readCurrentLine) */
int readUL(unsigned long *num)
{
    printf("inside readUL\n");
    unsigned long val = 0;
    int c;
    int middleNumberCount = 0;
    while(',' != (c = getchar()))
    {
        if(c == '\n') { return NLERR;}
        if(isdigit(c) == 0) { return ERROR;}
        val = val * 10 + (c - '0');
        middleNumberCount++;
    }
    *num = val;
    return SUCCESS;
}
 
/*finishLine() goes to the end of the line if there is an error */
void finishLine()
{
    int c;
    while((c = getchar()) != '\n')
    {
        if(c == EOF)
            return;
    }
}

/*encrypt() function to encrypt given data using the given key */
int encrypt(unsigned long *encryptNum)
{
    return 0;
}
 
/*decrypt() function to decrypt given data using the given key */
int decrypt()
{
    return 0;
}

int main()
{
    while(readCurrentLine() != ERROR)
    {
        if(readCurrentLine() == ERROR)
        {
            finishLine();
            printf("Error");
        }
        else if(errorFlag == NLERR) printf("Error");
    }
}
