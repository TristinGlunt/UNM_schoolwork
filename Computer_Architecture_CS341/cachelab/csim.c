

/*
    @author: Tristin Glunt
    tglunt@unm.edu
*/

#include "cachelab.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>
#include <inttypes.h>

typedef struct cache_line
{
    char validBit;
    unsigned tag;
    int  index;
    struct cache_line *prev, *next;
} cache_line;

typedef struct queue
{
    struct cache_line *front;
    struct cache_line *rear;
    unsigned count;
    unsigned totalPossible;
} Queue;

unsigned miss = 0;
unsigned hit = 0;
unsigned miss_eviction = 0;

void printCacheVals(int E, int b, int s)
{
    printf("E = %d\n", E);
    printf("b = %d\n", b);
    printf("s = %d\n\n", s);
}

int ispowerof2(unsigned int x)
{
   return x && !(x & (x - 1));
}

// A function to check if there is slot available in memory
int isQueueFull( Queue* queue, int setIndex )
{
    return queue[setIndex].count == queue[setIndex].totalPossible;
}

// A utility function to check if queue is empty
int isQueueEmpty( Queue* queue, int setIndex )
{
    return (queue[setIndex].count < 1);
}

cache_line** initializeCache(cache_line** cache, int S, int E)
{
    cache = malloc(S * sizeof(struct cache_line));

    // Then allocate each entry in the above allocated "array"
    // I.e. make each pointer in the "array" point to an "array" of `int`
    for (int i = 0; i < S; ++i)
        cache[i] = malloc(E  * sizeof(struct cache_line));

    for(int k = 0; k < S; k++)
    {
        for(int j = 0; j < E; j++)
        {
            cache[k][j].validBit = 0;
            cache[k][j].index = j;
            cache[k][j].tag = 0;
        }
    }
    return cache;
}

Queue* createQueue( int numberOfLines, int sets)
{
    Queue *queue; //clear up enough queues for each set
    queue = malloc(sets * sizeof(Queue));

    for(int i = 0; i < sets; i++)
    {
        queue[i].count = 0;
        queue[i].front = queue[i].rear = NULL;
        queue[i].totalPossible = numberOfLines;
    }

    return queue;
}

void deque(Queue *queue, int setIndex)
{
    if(!isQueueEmpty(queue, setIndex))
    {
        if(queue[setIndex].front == queue[setIndex].rear)
        {
            queue[setIndex].front = NULL;
        }

        queue[setIndex].rear = queue[setIndex].rear->prev;
        if(queue[setIndex].rear)
        {
            queue[setIndex].rear->next = NULL;
        }
        if(queue[setIndex].count > 0)
            queue[setIndex].count--;
    }
}

void enque(Queue *queue, cache_line *lineToBeQueued, int setIndex)
{

    if ( isQueueFull ( queue, setIndex ) )         //if queue is full, remove whatever was added first
    {
        deque( queue , setIndex);
    }

    lineToBeQueued->next = queue[setIndex].front;       //no matter what, set the next item in the cache to be the front of the q

    if(isQueueEmpty(queue, setIndex))                                   //if queue is empty, set rear and front of q to cacheline
    {
        queue[setIndex].front = queue[setIndex].rear = lineToBeQueued;
    }
    else
    {
        queue[setIndex].front->prev = lineToBeQueued; //set the fronts previous to the soon to be front, the cache line we're adding to the q
        queue[setIndex].front = lineToBeQueued;         //se the front to the cache line
    }
    queue[setIndex].count++;
}

void updateQueue(Queue *queue, cache_line* cacheLine, int setIndex)
{
    if(cacheLine == queue[setIndex].front)
    {
        printf("cache line is in front!\n");
        return;
    }
    else if(cacheLine == queue[setIndex].rear && isQueueFull(queue, setIndex))
    {
        printf("cache line is in back!\n");
        enque(queue, cacheLine, setIndex);
    }
    else if(cacheLine == queue[setIndex].rear && !isQueueFull(queue, setIndex))
    {
        queue[setIndex].rear = cacheLine->prev;
        cacheLine->prev->next = NULL;
        cacheLine->prev = NULL;
        queue[setIndex].count--;
        enque(queue, cacheLine, setIndex);
    }
    else
    {
        cacheLine->prev->next = cacheLine->next;
        cacheLine->next->prev = cacheLine->prev;
        cacheLine->prev = NULL;
        queue[setIndex].count--;
        enque(queue, cacheLine, setIndex);
    }
}

/*
    Go to set index.
    Check if any valid bits are on.
    If there are valid bits on, check if tags match, if tags match, it's a hit.
    If tags don't match, evict least rcently used.
*/
void simulateCache(FILE* pFile, Queue* queue, int s, int b, int E, int S, cache_line** cache)
{
    char identifier;
    unsigned long long int address;
    int size;
    int flagTagMatch = 0;
    int counter = 0;

    int x = s + b;
    int mask = (1 << x) - 1; //bit manipulation to get last X bits of a value (1 0000) - 1 = 1111
    printf("mask = %x\n", mask);

    while(fscanf(pFile," %c %llx,%d", &identifier, &address, &size)>0)
    {
        if(identifier == 'I')
            continue;
        printf("%c %llx, %d", identifier, address, size);
        int setandbytes = address & mask;
        int setIndex = setandbytes >> b;
        unsigned long long int tag = address >> x;
        printf(", setIndex = %d ,tag = %llx", setIndex, tag);
        flagTagMatch = 0;

        //check valid bits, if a valid bit is on check tags
        if(E == 1)
        {
            if(cache[setIndex][0].validBit == 0)
            {
                cache[setIndex][0].validBit = 1;
                cache[setIndex][0].tag = tag;
                miss++;
                if(identifier == 'M')
                {
                    printf("miss, hit\n");
                    hit++;
                }
                else
                    printf("miss\n");

            }
            else
            {
                if(cache[setIndex][0].tag == tag)
                {
                    printf(" - hit\n");
                    hit++;
                    if(identifier == 'M')
                        hit++;
                }
                else
                {
                    cache[setIndex][0].tag = tag;
                    miss_eviction++;
                    miss++;
                    if(identifier == 'M')
                    {
                        hit++;
                        printf("miss, hit\n");
                    }
                    else
                        printf("miss\n");
                }
            }
        }
        else
        {
            if(!isQueueEmpty(queue, setIndex))
            {
                int i = 0;
                cache_line *current = queue[setIndex].front;
                while(i < queue[setIndex].count)
                {
                    if(current->validBit != 0)
                    {
                        printf(" cache tag = %x ", current->tag);
                        if(current->tag == tag)
                        {
                            if(identifier == 'M')
                            {
                                hit++;
                                printf("- hit, hit");
                            }
                            else
                                printf("- hit");
                            hit++;
                            flagTagMatch = 1;
                            updateQueue(queue, current, setIndex);
                            break;
                        }
                    }
                    i++;
                    if(i < queue[setIndex].count) //replace with dowhile
                    {
                          current = current->next;
                    }
                }
            }

            if(flagTagMatch == 0)  //if no valid bits
            {
                if(isQueueEmpty(queue, setIndex))
                {
                    cache_line *cache = malloc(sizeof(struct cache_line));
                    printf("- b miss!\n");
                    cache->validBit = 1;
                    cache->tag = tag;
                    enque(queue, cache, setIndex);
                    miss++;
                }
                else if(isQueueFull(queue, setIndex))
                {
                    printf("- b miss eviction\n");
                    cache_line *cache = malloc(sizeof(struct cache_line));
                    cache->validBit = 1;
                    cache->tag = tag;
                    enque(queue, cache, setIndex); //least recently cache line is going to be used to enque
                    miss_eviction++;
                    miss++;
                    if(identifier == 'M')
                        counter++;
                }
                else    //queue is not empty nor full
                {
                    printf("- c miss!\n");
                    cache_line *cache = malloc(sizeof(struct cache_line));
                    cache->validBit = 1;
                    cache->tag = tag;
                    enque(queue, cache, setIndex); //use unused cache spot
                    miss++;
                }

                if(identifier == 'M')
                    hit++;
            }
        }
    }
    printf("\ncounter = %d\n", counter);
}
	

int main(int argc, char** argv)
{
    int E, b, s, S, B;
    FILE* pFile;
    cache_line** cache = NULL;

    for(int i = 0; i < argc; i++)
    {
        if(strcmp(argv[i], "-s") == 0)
            s = atoi(argv[i+1]);
        else if(strcmp(argv[i], "-E") == 0)
            E = atoi(argv[i+1]);
        else if(strcmp(argv[i], "-b") == 0)
            b = atoi(argv[i+1]);
        else if(strcmp(argv[i], "-t") == 0)
            pFile = fopen(argv[i+1], "r");
    }

    S = pow(2, s);
    B = pow(2, b);

    if(!ispowerof2(S))
    {
        printf("invalid s, s must be a power of two");
        exit(1);
    }

    if(!ispowerof2(B))
    {
        printf("invalid b, b must be a power of two");
        exit(1);
    }

    cache = initializeCache(cache, S, E);
    Queue *q = createQueue(E, S);           //initialize queue with S sets and E lines per set

    printCacheVals(E, b, s);

    if(pFile != NULL)
    {
        simulateCache(pFile, q, s, b, E, S, cache); //need to update B
        fclose(pFile);
    }
	printSummary(hit, miss, miss_eviction);

    return 0;
}
