/******************************************/
/*      @author: Tristin Glunt            */
/*      CS 241 - Lab 9                    */
/*      huffman.c, includes encode        */
/*      and decode function               */
/*                                        */
/******************************************/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include "huffman.h"
         
#define FAIL 0
#define SUCCESS 1
         
 
/***********/
/***********/
/**GLOBALS**/
/***********/
/***********/
       
unsigned long globalFreqs[256] = {0};
unsigned char globalUsedCh[256] = {0};
unsigned char globalUniqueSymbols;
unsigned long totalCount = 0;
 
/*I realized to use this too late :( */
typedef struct HuffmanTreeNode* HTNode;
      
/*global linkedlists */   
struct HuffmanTreeNode *decodedLinkedList;      
struct HuffmanTreeNode* globalSortedLL;
 
/*  
    Struct for building nodes for my huffmanTree, and for the linked list
    struct has the input letter, the letters frequency, and the left and irght childs
*/
struct HuffmanTreeNode
{
  unsigned char symbol;
  unsigned long freq;
  char *code;
  struct HuffmanTreeNode *left, *right;
  struct HuffmanTreeNode* next;
};
         
/*generate new node with given symbol and freq */
/* basic new node function */
struct HuffmanTreeNode* newNode(char symbol, unsigned long freq)
{
  struct HuffmanTreeNode* newNode = malloc(sizeof(struct HuffmanTreeNode));
  newNode->symbol = symbol;
  newNode->freq = freq;
  newNode->left = newNode->right = NULL;
  return newNode;
}
 
/*
@function: compareTwoNodes()
take in two nodes to compare, this function will decide the tie breaker
if the nodes freqs are the same, the greater symbol will be higher priority
@param: (struct HuffmanTreeNode* a, struct HuffmanTreeNode* b)
two nodes being compared, a and b
@return: int
*/  
int compareTwoNodes(struct HuffmanTreeNode* a, struct HuffmanTreeNode* b)
{
      if(b->freq < a->freq)
      {
        return 0;
      }
      if(a->freq == b->freq)
      {
        if(a->symbol > b->symbol)
        {
            return 1;
        }
        else {return 0;}
      }
      if(b->freq > a->freq)
        return 1;
    return 0;   
}
 
/*
@function: insert()
function takes in a node to insert into a sorted LinkedList
@param: (struct HuffmanTreeNode* node, struct HuffmanTreeNode* htnNew)
the current node/list, the new node being inserted
@return: node
*/
struct HuffmanTreeNode* insert(struct HuffmanTreeNode* node, struct HuffmanTreeNode* htnNew)
{
         
  struct HuffmanTreeNode* currentNode = node;
             
  if(currentNode == NULL || compareTwoNodes(htnNew, currentNode))
  {
    htnNew->next = currentNode;
    return htnNew;
  }
  else
  {
    while(currentNode->next != NULL && compareTwoNodes(currentNode->next, htnNew))
    {
      currentNode = currentNode->next;
    }
    htnNew->next = currentNode->next;
    currentNode->next = htnNew;
    return node;
  }
}
 
/*
@function: popNode()
basic function to remove head of list
@param: (struct HuffmanTreeNode** head)
current list head
@return: new head
*/
 
struct HuffmanTreeNode* popNode(struct HuffmanTreeNode** head)
{
  struct HuffmanTreeNode* node = *head;
  *head = (*head)->next;
  return node;
}
       
/*
  @function: listLength()
  reads list and returns the length
  @param: (struct HuffmanTreeNode* node)
  @return: int (length of list)
*/
int listLength(struct HuffmanTreeNode* node)
{
  struct HuffmanTreeNode* current = node;
  int length = 0;
  while(current != NULL)
  {
    length++;
    current = current->next;
  }
  return length;
}
       
/*
  @function: printList()
  print out list
  @param: (struct HuffmanTreeNode* node)
  read in list
  @return:void
*/
void printList(struct HuffmanTreeNode* node)
{
  struct HuffmanTreeNode* currentNode = node;
         
  while(currentNode != NULL)
  {
    if(currentNode->symbol <= ' ' || currentNode->symbol > '~')
      printf("=%d", currentNode->symbol);
    else
      printf("%c", currentNode->symbol);
    printf("%lu ", currentNode->freq);
    currentNode = currentNode->next;
  }
  printf("\n");
}
       
/*
  @function: buildSortedList()
  actually builds the globalSortedLL by inserting
  newNodes of symbol i and globalFreqs[i]
  @param: 
  @return: void
*/
void buildSortedList()
{
    int i;
    for(i = 0; i < 256; i++)
    {
        if(!globalFreqs[i] == 0)
        {
            globalSortedLL = insert(globalSortedLL, newNode(globalUsedCh[i], globalFreqs[i]));
        }
    }
    /*
    printList(globalSortedLL);
 /*         
  printf("Sorted freqs: ");
  printf("listL: %d\n", listLength(globalSortedLL));
 */
}
 
/*
  @function: isLeaf()
  will test to see if the current node is a leaf or not
  @param: 
  @return
*/
         
int isLeaf(struct HuffmanTreeNode* node)
{
  if((node->left == NULL) && (node->right == NULL))
    return SUCCESS;
  else
    return FAIL;
}
         
/*
  @function: buildHuffmanTree()
  where you finally build the huffman tree
  comments throughout describing what happens
  @param: (struct HuffmanTreeNode* node)
  take in the sorted linked list
  @return: head;
*/
struct HuffmanTreeNode* buildHuffmanTree (struct HuffmanTreeNode* node)
{
  struct HuffmanTreeNode* head = node;
  struct HuffmanTreeNode *newChildNode, *firstNode, *secondNode;
       
  while(head->next != NULL)
  {
    /*grab first two items from linkedL, and remove two items*/
    firstNode = popNode(&head);
    secondNode = popNode(&head);
    /*combine sums, use higher symbol, create new node*/
    newChildNode = newNode(secondNode->symbol, (firstNode->freq + secondNode->freq));
    newChildNode->left = firstNode;
    newChildNode->right = secondNode;
    /*insert new node, decrement total symbols in use */
    head = insert(head, newChildNode);
  }
         
  return head;
}
 
/*
@function: printTable()
print the table of Symbols, freqs, and codes
@param: (char *codesArray[])
take in the array that has pointers
to the codes
@return: void
*/    
void printTable(char *codesArray[])
{
  int i;
  printf("Symbol\tFreq\tCode\n");
  for(i = 0; i < 256; i++)
  {
    if(globalFreqs[i] != 0)
    {
      if(i <= ' ' || i > '~')
      {
        printf("=%d\t%lu\t%s\n", i, globalFreqs[i], codesArray[i]);
      }
      else
      {
        printf("%c\t%lu\t%s\n", i, globalFreqs[i], codesArray[i]);
      }
    }
  }
  printf("Total chars = %lu\n", totalCount);
}
 
/*
@function: makeCodes()
tranverses the trees and makes the codes
assigned to *symCodes[256] according to the
built huffmanTree
@param:
/*   struct HuffmanTreeNode *node,         Pointer to some tree node */
/*  char *code,           The *current* code in progress */
/*   char *symCodes[256], The array to hold the codes for all the symbols */
/*   int depth)            How deep in the tree we are (code length)
@return:
*/
void makeCodes(
   struct HuffmanTreeNode *node,        /* Pointer to some tree node */
   char *code,          /* The *current* code in progress */
   char *symCodes[256], /* The array to hold the codes for all the symbols */
   int depth)           /* How deep in the tree we are (code length) */
{
    char *copiedCode;
     
    if(isLeaf(node))
    {
        code[depth] = '\0';
        symCodes[node->symbol] = code;
        return;
    }
      
    copiedCode = malloc(255*sizeof(char));
    memcpy(copiedCode, code, 255*sizeof(char));
      
    code[depth] = '0';
    copiedCode[depth] = '1';
    makeCodes(node->left, code, symCodes, depth+1);
    makeCodes(node->right, copiedCode, symCodes, depth+1);
}
      
/*
  @function: getFileFreq()
  gets the frequencies of each character in the given
  file from the command line, this function will also
  create two global 1d arrays, one for the currently
  used characters in the file, and then one with those
  characters frequencies, the two arrays will line up
  parallel
  @param: FILE* in, FILE* out,
  the current file being processed
  @return: void
*/
void getFileFreq(FILE* in)
{  
    unsigned long freqs[256] = {0};
    int i, t, fileCh;
           
    while((fileCh = fgetc(in)) != EOF)
    {
        freqs[fileCh]++;
        totalCount++;
    }
    for(i = 0; i < 256; i++)
    {
        if(freqs[i] != 0)
        {
            globalUsedCh[i] = i;
            globalFreqs[i] = freqs[i];
            if(i <= ' ' || i > '~')
            {
                globalUniqueSymbols++;
            }
            else
            {
                globalUniqueSymbols++;
            }
        }
    }
    /* below code until total count is for debugging purposes */
    /*
    **
    printf("Used Ch: ");
    for(t = 0; t < 256; t++)
    { 
    if(globalUsedCh[t] != 0)
            {  
         if(t <= ' ' || t > '~')
        {
            printf("%d ", globalUsedCh[t]);
        }
        else
            printf("%c ", globalUsedCh[t]);
        }
    }
    printf("\n");
    printf("Freq Ch: ");
    for(t = 0; t < 256; t++)
    {
        if(globalFreqs[t] != 0)
        {  
            printf("%lu ", globalFreqs[t]);
        }
    }
    */
    /*
    printf("\n");*/
    /* end of code for debugging/vizualazation of arrays*/
    /*
    printf("Total Count %lu\n", totalCount);
    printf("globalArrayLength: %d\n", globalUniqueSymbols);
    */
}
     
 
/*
@function: headerEncode()
writes out the header according to the spec,
when the code output ends, if the output is in the
middle of a byte, it will pad the byte with 0's
@param: (FILE* in, FILE* out, char *symCodes[256])
takes in the file being read, and the file to write out to,
as well as the pointer array that holds the codes
@return: void
*/
    
void headerEncode(FILE* in, FILE* out, char *symCodes[256])
{
    int i, codeIdx;
    char byte;
    char *code;
    int bitCount = 0;
    unsigned int symboIdx;
    unsigned char *fileIn;
    unsigned long totalEncodedSym;
         
     
    totalEncodedSym = ftell(in);
    rewind(in);
        
    
    fileIn = malloc((totalEncodedSym+1)*sizeof(char));
    fread(fileIn, totalEncodedSym, 1, in);
    fwrite(&globalUniqueSymbols, sizeof(char), 1, out);
    for(i = 0; i < 256; i++)
    {
        if(globalFreqs[i] != 0)
        {
            fwrite(globalUsedCh+i, sizeof(char), 1, out);
            fwrite(globalFreqs+i, sizeof(unsigned long), 1 , out);
        }
    }
    fwrite(&totalCount, sizeof(unsigned long), 1, out);
    
    byte = 0;
    bitCount = 0;
    codeIdx = 0;
    
    for(symboIdx = 0; symboIdx < totalEncodedSym; symboIdx++)
    {
        code = symCodes[fileIn[symboIdx]];
        while(code[codeIdx] != '\0')
        {
            if(bitCount < 8)
            {
                byte <<= 1;
                byte |= code[codeIdx] - '0';
                codeIdx++;
                bitCount++;
            }
            if(bitCount == 8)
            {
                fprintf(out, "%c", byte);
                byte = 0;
                bitCount = 0;
            }
        }
        codeIdx = 0;
    }
    if((symboIdx == totalEncodedSym) && (bitCount != 0))
    {
        byte <<= 8 - bitCount;
        fprintf(out, "%c", byte);
    }
    free(fileIn);
}
 
/*
@function: encodeFile()
function needed by huffman.h,
function acts as a main calling functions to
encode the file given by in
@param: (FILE* in, FILE* out)
reads in the current file, file out will be the output file 
@return: void         
*/
void encodeFile(FILE* in, FILE* out)
{
    char *code;
    char *symCodes[256] = {0};
    int depth = 0;
      
    code = malloc(255*sizeof(char));
     
    getFileFreq(in);
    buildSortedList();
    makeCodes(buildHuffmanTree(globalSortedLL), code, symCodes, depth);
    printTable(symCodes);
    headerEncode(in, out, symCodes);
          
    free(code);
}
   
/*
start of
decoding
file
:D 
*/

/*
@function: decodeFile()
decode a given huffencoded file,
read the header from encoded file, find unique symbols,
build freq and symbols array, rebuild a huffman tree, 
get bytes and loop through tree finding leafs for a given
code in the byte, out put the symbol for the leaf
@param: (FILE* in, FILE* out)
take in current file being read and output file
from huffdecode.c
@return: void
*/
void decodeFile(FILE* in, FILE* out)
{   
    char *fileIn;
    unsigned uniqueSyms;
    unsigned char currentSym;
    unsigned char currentByte;
    unsigned char* symArray;
    unsigned long* freqArray;
    unsigned long currentFreq;
    unsigned long dTotalCharacters;
    unsigned long *charsPrinted;
    unsigned char printChar;
    unsigned char tempCode;
    struct HuffmanTreeNode* decodedLinkedList;
    struct HuffmanTreeNode* decodingHuffmanTree;
    char *codes;
    char *symCodes[256] = {0};
    int depth = 0;
    char byte = 0;
    int i, q, num, bitCount;

    fread(&uniqueSyms, sizeof(char), 1, in);
    symArray = malloc(uniqueSyms*sizeof(char));
    freqArray = malloc(uniqueSyms*sizeof(unsigned long));
    codes = malloc(255*sizeof(char));

    /*printf("unique syms: %d\n", uniqueSyms);*/

    i = 0;
    if(uniqueSyms == 0)
    {
        uniqueSyms = 256;
    }
    while(i < uniqueSyms)
    {
        fread(&currentSym, sizeof(char), 1, in);
        globalUsedCh[i] = currentSym;
        fread(&currentFreq, sizeof(unsigned long), 1, in);
        globalFreqs[i] = currentFreq;
        /*
        printf("current sym: %d, current freq: %lu\n", currentSym, currentFreq);
        printf("currentSymArray[%d] = %c\n", i, globalUsedCh[i]);
        printf("currentFreqArray[%d] = %lu\n", i, globalFreqs[i]);
        */
        i++;
    }
    buildSortedList();
    decodingHuffmanTree = buildHuffmanTree(globalSortedLL);
    makeCodes(decodingHuffmanTree, codes, symCodes, depth);
    printTable(symCodes);
    struct HuffmanTreeNode *head = decodingHuffmanTree;

    fread(&dTotalCharacters, sizeof(unsigned long), 1, in);
    /*
    printf("totalCharacters = %lu\n", dTotalCharacters);
    */
    q = 0;

    currentByte = fgetc(in);
    while(q < dTotalCharacters)
    {
        for(bitCount = 7; bitCount >= 0; bitCount--)
        {
            tempCode = (currentByte >> bitCount) & 1;
            if(isLeaf(head))
            {
                printChar = head->symbol;
                fwrite(&printChar, sizeof(char), 1, out);
                head = decodingHuffmanTree;
                q++; 
            }
            if(q >= dTotalCharacters)
            {
                break;
            }
            if(tempCode == 1)
            {
                head = head->right;
            }
            else if(tempCode == 0)
            {
                head = head->left;
            }
        }
        currentByte = fgetc(in);
    }
}