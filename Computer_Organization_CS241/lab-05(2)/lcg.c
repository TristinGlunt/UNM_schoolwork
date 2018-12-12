#include "lcg.h"
#include <stdio.h>

unsigned long getP(unsigned long m);

struct LinearCongruentialGenerator makeLCG(unsigned long m, unsigned long c)
{
    struct LinearCongruentialGenerator lcg;
    unsigned long p = getP(m);
    lcg.m = m;
    lcg.c = c;
    lcg.x = c;


    if((m % 4) == 0)
    {
        lcg.a = (1 + (2 * p));
    }
    else { lcg.a = 1 + p; }
    
    if(lcg.a >= m || lcg.a < = 0)
    {
        lcg.m = 0;
        lcg.c = 0;
        lcg.x = 0;
        lcg.a = 0;
    }
    return lcg;
}

unsigned long getP(unsigned long m)
{
    unsigned long savedN = 1;
    /* step 2 */
    /* start with testDiviser at 2 (2.2 part 2)*/
    unsigned long testDiviser = 2;
    /* step 1 */
    unsigned long p = 1;
    /* step 3 */        
    while(m != 1)
    {
        if((testDiviser * testDiviser) > m)
        {
            if(m != 1)
            {
                savedN = m;
                (p *= savedN);
                return p;
            }
        }
        /* step 4 */
        else if((m % testDiviser) == 0)
        {
            p *= testDiviser;
            /* part 4.a */
            savedN = testDiviser;
            /* part 4.c */
            while((m % testDiviser) == 0)
            {
                /* part 4.b */
                m = m / testDiviser;
            }
        }
        /* step 5 */
        testDiviser++;
    }
    
    return p;
}

/* Update lcg and return next value in the sequence. */
unsigned long getNextRandomValue(struct LinearCongruentialGenerator* lcg)
{
    unsigned long tempX = lcg->x;
    lcg->x = (((lcg->a) * (lcg->x)) + lcg->c) % lcg->m;
    return tempX;
}