#include <stdio.h>
#include <stdlib.h>
#include "huffman.h"
    
#define FAIL 0 
#define SUCCESS 1
    
/* global 1 day arrays that hold chars and their freqs from file */
  
unsigned long globalFreqs[256] = {0};
char globalUsedCh[256] = {0};
int globalUniqueSymbols; 
  
struct HuffmanTreeNode* globalSortedLL;
/*
struct has the input letter, the letters frequency, and the left and irght childs 
*/
struct HuffmanTreeNode 
{
    char symbol;
    unsigned long freq;
    struct HuffmanTreeNode *left, *right;
    struct HuffmanTreeNode* next;
};
    
/* does it make sense to have a struct for the entire huffman tree to see its size? */
struct HuffmanTree
{
    unsigned size;
};
   
/*generate new node with given symbol and freq */
struct HuffmanTreeNode* newNode(char symbol, int freq)
{
    struct HuffmanTreeNode* newNode = malloc(sizeof(struct HuffmanTreeNode));
    newNode->symbol = symbol;
    newNode->freq = freq;
    newNode->left = newNode->right = NULL;
    return newNode;
}
     
/*current work in progress, i believe this is the way to insert it for a BST
/* will change for HuffmanTreenode once working
/*
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
  
int compareTwoNodes(struct HuffmanTreeNode* a, struct HuffmanTreeNode* b)
{
    if(b->freq < a->freq)
    {
        return 0;
    }
    if(a->freq == b->freq)
    {
        if(a->symbol > b->symbol)
            return 1;
        return 0;
    }
    if(b->freq > a->freq)
        return 1;
}
  
struct HuffmanTreeNode* popNode(struct HuffmanTreeNode** head)
{
    struct HuffmanTreeNode* node = *head;
    *head = (*head)->next;
    return node;
}
  
/*convert output to bytes from bits*/
/*use binary fileio to output */
/*put c for individual character byte*/
/*fwrite each individual byte for frequency of symbol(look at fileio slides) */
  
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
@function:
@param:
@return:
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
@function:
@param:
@return:
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
@function:
@param:
@return:
*/
void buildSortedList()
{
    int i;
    for(i = 0; i < 256; i++)
    {
        if(!globalFreqs[i] == 0)
        {
            globalSortedLL = insert(globalSortedLL, newNode(i, globalFreqs[i]));
        }
    }
      
    printf("Sorted freqs: ");
    printList(globalSortedLL);
    printf("listL: %d\n", listLength(globalSortedLL));
}
    
/*where I plan to build the actual huffmantree */
/*
@function:
@param:
@return:
*/
struct HuffmanTreeNode* buildHuffmanTree(struct HuffmanTreeNode* node)
{
    int top = 0;
    struct HuffmanTreeNode *left, *right, *topNode, *huffmanTree;
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
    printList(head);
    
    return head; 
}

void printArr(char arr[], int n)
{
    int i;
    for (i = 0; i < n; ++i)
        printf("%d", arr[i]);
    printf("\n");
}
  
/*for huffman codes, make array of characters to hold codes for each individual character*/
void printCodes(struct HuffmanTreeNode* node, char codesArray[], int head)
{
    int i;
    if(node->left)
    {
        codesArray[head] = 0;
        printCodes(node->left, codesArray, head+1);
    }
    if(node->right)
    {
        codesArray[head] = 1;
        printCodes(node->right, codesArray, head+1);
    }
    if(isLeaf(node))
    {
        printf("%c", node->symbol);
        printArr(codesArray, head);
    }
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
void getFileFreq(FILE* in, FILE* out)
{  
    unsigned long freqs[256] = {0};
    int i, t, fileCh;
    unsigned long totalCount = 0;
     
    while((fileCh = fgetc(in)) != EOF)
    {
        freqs[fileCh]++;
        totalCount++;
    }
    printf("Symbol\tFreq\tCode\n");
    for(i = 0; i < 256; i++)
    {
        if(freqs[i] != 0)
        {
            globalUsedCh[i] = i;
            globalFreqs[i] = freqs[i];
            if(i <= ' ' || i > '~')
            {
                printf("=%d\t%lu\n", i, freqs[i]);
                globalUniqueSymbols++;
            }
            else
            {
                printf("%c\t%lu\n", i, freqs[i]);
                globalUniqueSymbols++;
            }
        }
    }
/* below code until total count is for debugging purposes */
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
    printf("\n");
/* end of code for debugging/vizualazation of arrays*/
    printf("Total Count %lu\n", totalCount);
    printf("globalArrayLength: %d\n", globalUniqueSymbols);
}
     
void encodeFile(FILE* in, FILE* out)
{
    int top = 0;
    getFileFreq(in, out);
    buildSortedList();
    printCodes(buildHuffmanTree(globalSortedLL), globalUsedCh, top);
   
    /*test stuff
    struct HuffmanTreeNode* tree;
    tree = newNode('Q', 1);
   
    insert(tree, 'A', 5);
    insert(tree, 'b', 12);
    insert(tree, 'd', 4);
    insert(tree, 'l', 6);
    insert(tree, 'e', 2);
    insert(tree, 'f', 3);
    insert(tree, 'h', 7);
   
    printf("Test tree: ");
    printList(tree);
    end of test stuff*/
}
