#include <stdio.h>
#include <ctype.h>
#include "lcg.h"
 
#define SUCCESS 0
#define ERROR 1
#define NLERR 2
#define ENCRYPT 3
#define DECRYPT 4
 
int errorFlag;
int modeFlag;
int lineCounter = 1;
struct LinearCongruentialGenerator lcg;

int readCurrentLine();
int readDirection();
int readUL();
int finishLine();
int encrypt(struct LinearCongruentialGenerator *lcg);
int decrypt(struct LinearCongruentialGenerator *lcg);
int readData();
struct LinearCongruentialGenerator makeLCG(unsigned long m, unsigned long c);
unsigned long getNextRandomValue(struct LinearCongruentialGenerator* lcg);
 
/******************************************/
/*  Read current line from standard in    */
/*                                        */
/*                                        */
/******************************************/
int readCurrentLine()
{
   unsigned long c, m;
   /*printf("beginning of readCurrentLine\n"); */
   if((errorFlag = readDirection()) != SUCCESS ) {return errorFlag;}
   /*printf("test passed for readDirection\n"); */
   if((errorFlag = readUL(&m)) != SUCCESS) {return errorFlag;}
   /*printf("test passed for number check for modulus m\n");*/
   if((errorFlag = readUL(&c)) != SUCCESS) {return errorFlag;}
   /*printf("test passed for number check for increment c\n");*/

   printf("%d)", lineCounter);
   lineCounter++;
   /*3.3.1 */
   lcg = makeLCG(m, c);
   if(lcg.x == 0 && lcg.a == 0 && lcg.m == 0 && lcg.c == 0)
   {
        return ERROR;
   }
 
   /*
   if readDirection is E then encrypt
   if readDirection is D then decrypt */
    if(modeFlag == ENCRYPT)
    {
        /*printf("we are in the encrypt part :D\n");*/
        encrypt(&lcg);
        modeFlag = 0;
        return SUCCESS;
    }
    else if(modeFlag == DECRYPT)
    {
        /*printf("We are in the decrypt part!");*/
        decrypt(&lcg);
        modeFlag = 0;
        return SUCCESS;
    }
    else{return ERROR;}
}
/*function to determine whether each line encodes
a valid direction(encryption[e] or decryption[d]*/
 
int readDirection()
{
    /*printf("in read direction\n");*/
    int c;
    c = getchar();
    /*putchar(c);*/
    if(c == 'e') { modeFlag = ENCRYPT; return SUCCESS;}
    else if(c == 'd') { modeFlag = DECRYPT; return SUCCESS;}
    else if(c == '\n') {return NLERR;}
    else if(c == EOF) {return EOF;}
    else{ return ERROR;}
}
 
/*readUL(unsigned long *num) takes in the address of the key or the data
will check for errors. (used for readCurrentLine) */
int readUL(unsigned long *num)
{
    /*printf("inside readUL\n");*/
    unsigned long val = 0;
    int c;
    int numberCount = 0;
    while(',' != (c = getchar()))
    {
        /*putchar(c);*/
        if(c == '\n') { return NLERR;}
        if(!isdigit(c)) { return ERROR;}
        val = val * 10 + (c - '0');
        numberCount++;
    }
    if(numberCount > 20)
    {
        return ERROR;
    }
    *num = val;
    return SUCCESS;
}

/*remove once working on encrypt and decrypt */
int readData()
{
    int c;
    while((c = getchar()) != '\n')
    {
        /*putchar(c);*/
    }
    printf("\n");
    return 0;
}
 
/*finishLine() goes to the end of the line if there is an error */
int finishLine()
{
    int c;
    while((c = getchar()) != '\n')
    {
        if(c == EOF)
            return EOF;
    }
}

/*encrypt() function to encrypt given data using the given key */
int encrypt(struct LinearCongruentialGenerator *lcg)
{
    char b;
    int x;
    char encryptedByte;
    /*3.3.2*/
    while((b = getchar()) != '\n')
    {
        /*3.3.3*/
        x = getNextRandomValue(lcg);
        /*3.3.4*/
        encryptedByte = b ^ (x % 128);
        /*3.3.5*/
        if(encryptedByte < ' ')
        {
            printf("*");
            char k = ('@' + encryptedByte);
            printf("%c", k);
            continue;
        }
        else if(encryptedByte == 127)
        {
            printf("*&");
            continue;
        }
        else if(encryptedByte == '*')
        {
            printf("**");
            continue;
        }
        putchar(encryptedByte);

    }
    printf("\n");
    return SUCCESS;
}
 
/*decrypt() function to decrypt given data using the given key */
int decrypt(struct LinearCongruentialGenerator *lcg)
{
    return SUCCESS;
}

int main()
{
    int c;
    int result;
    while((result = readCurrentLine()) != EOF)
    {
        if(result == ERROR)
        {
            finishLine();
            printf("Error\n");
        }
        else if(result == NLERR) {printf("Error\n");}
    }
}
