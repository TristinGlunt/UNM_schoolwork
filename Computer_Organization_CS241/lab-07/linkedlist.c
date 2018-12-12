/******************************************/
/*      @author: Tristin Glunt            */
/*      CS 241 - Lab 7                    */
/*      linkedlist.c                      */    
/*                                        */
/*                                        */
/******************************************/

#include <stdio.h>
#include <stdlib.h>
#include "linkedlist.h"

#define SUCCESS 1
#define FAIL 0

struct ListNode *lnFirst = NULL;
struct ListNode *lnLast = NULL;

/* 
@function: creatNode(int data), allocates room in memory for
new struct lnNewList
@param: (int data), data being entered for the new struct
@return: new listNode struct being made
*/
struct ListNode* createNode(int data)
{
    struct ListNode *lnNewList = (struct ListNode *) malloc(sizeof(struct ListNode));

    lnNewList->data = data;

    lnNewList->next = NULL;

    return lnNewList;
}

/*
struct ListNode* insertSorted(struct ListNode* head, int data)
{

    lnFirst = head;
    struct ListNode* lnNew = createNode(data);

    if(lnFirst == NULL)
    {
        lnNew->next = lnFirst;
        return lnNew;
    }
    else
    {
        struct ListNode *lnNewList = (struct ListNode *) malloc(sizeof(struct ListNode));
        
        if(lnFirst == lnLast)
        {
            head = lnFirst;
            lnFirst->next = lnNewList;
            lnLast = lnNewList;
            lnNewList->next = NULL;
        }
        else
        {
            lnLast->next = lnNewList;
            lnNewList->next = NULL;
            lnLast = lnNewList;
        }
    }
    return head;
}
*/

/*
@function: insertSorted(struct ListNode* head, int data)
inserts data in a sorted list
@param: (struct ListNode* head, int data), takes in the top of the stack
to insert the new data with
@return: the new top of stack, will return value of the top 
*/
struct ListNode* insertSorted(struct ListNode * head , int data)
{
    struct ListNode *current = head ;
    struct ListNode *newNode = createNode ( data );
    if( current == NULL || data < current -> data )
    {
        newNode->next = current ;
        return newNode ;
    }
    else
    {
        while ( current -> next != NULL &&
            current -> next -> data < data )
        {
            current = current -> next ;
        }
        newNode -> next = current -> next ;
        current -> next = newNode ;
        return head;
    }
}


/*

*/
int removeItem(struct ListNode** headRef, int data)
{
    struct ListNode *lnNewList;
    if(*headRef == NULL)
    {
        return FAIL;
    }
    else if((*headRef)->data == data)
    {
        lnNewList = *headRef;
        *headRef = (*headRef)->next;
        return SUCCESS;
    }
    else
    {
        int recursiveRemoveItem = removeItem(&((*headRef)->next), data);
        return recursiveRemoveItem;
    }
}

struct ListNode* pushStack(struct ListNode* head, int data)
{
    struct ListNode *lnNewList = createNode(data);
    lnNewList->next = head;
    return lnNewList;
}

int popStack(struct ListNode** headRef)
{
    struct ListNode* lnNew = *headRef;
    int data = lnNew->data;
    *headRef = lnNew->next;
    free(lnNew);
    return data;
}

int listLength(struct ListNode* head)
{
    struct ListNode* lnNew = head;
    int len = 0;
    while(lnNew != NULL)
    {
        len++;
        lnNew = lnNew->next;
    }
    return len;
}

/*
function: printList(struct ListNode* head), print current list
while the currentNode is not NULL, print the currentNode data,
set the new currentNode to the next node
@param: (struct ListNode* head), take in current list data
@return: void
*/
void printList(struct ListNode* head)
{
    struct ListNode *currentNode = head;

    while(currentNode != NULL)
    {
        printf("%d ", currentNode->data);
        currentNode = currentNode->next;
    }
    printf("\n");
}

void freeList(struct ListNode* head)
{
    struct ListNode* current = head;

    if(current != NULL)
    {
        freeList(current->next);
        current->next = NULL;
        free(current);
    }
}

void reverseList(struct ListNode** headRef)
{
    struct ListNode *previous;
    struct ListNode *currentNode = *headRef;
    struct ListNode *rest;

    while(currentNode != NULL)
    {
        if(&rest == &currentNode)
            break;
        rest = currentNode->next;
        currentNode->next = previous;
        previous = currentNode;
        currentNode = rest;
    }
    *headRef = previous;
}

