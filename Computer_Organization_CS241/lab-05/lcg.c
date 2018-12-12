#include "lcg.h"
#include <stdio.h>

int p;
int n;
int globalX;

struct LinearCongruentialGenerator makeLCG(unsigned long m, unsigned long c)
{
	struct LinearCongruentialGenerator lcg;
	lcg.m = m;
	lcg.c = c;
	lcg.x = c;
	globalX = lcg.x;
	getP(m);

	if((m % 4) == 0)
	{
		lcg.a = (1 + (2 * p));
	}
	else { lcg.a = 1 + p; }
}

int getP(int m)
{
	int savedN;
	/* step 2 */
	int testDiviser = 2;
	/* step 1 */
	n = m;
	/* step 3 */
	while(1)
	{
		if((testDiviser * testDiviser) > n)
		{
			if(n != 1)
			{
				savedN = n;
				return (p = savedN);
			}
		}
		/* step 4 */
		else if((n % testDiviser) == 0)
		{
			/* part a */
			savedN = testDiviser;
			/* part c */
			while((n % testDiviser) == 0)
			{
				/* part b */
				n = n / testDiviser;
			}
		}
		/* step 5 */
		else if((n % testDiviser) != 0)
		{
			testDiviser++;
		}
	}
}

unsigned long getNextRandomValue(struct LinearCongruentialGenerator* lcg)
{
	return (lcg = globalX + 1);
}
