# SmartRails

----------------------------------------------------------------------------------------
CS 351 UNM - SmartRails - A concurrent train simulator
Tristin Glunt | tglunt@unm.edu
Duong Nguyen  | dnguyen@unm.edu


================HOW TO PLAY================
1. Enter how many trains you would like to play with, press enter; we recommend having less than
half as many stations there are, but the program will accept 1 less than the total amount of stations.

2. Enter the number of the station you would like to place a train, press enter.
   -> Once every train has a station the popup menu for placing trains will go away.

3. To tell a train to go to a route, select the train image with your mouse, and enter
the station number you would like the train to go to, then press ENTER.

4. You can either:
   a. Let the train go to it's route right away by pressing ENTER.
   b. Select other trains, give them stations to go to and then press ENTER.

5. Once ENTER has been pressed, all trains will go if they have a route.
	-> NOTE: from here on out, trains will go right away if they have a route.

6. To stop trains from going immediately when they are given a destination press S.

7. If S has been press, no trains will go until ENTER has been pressed, regardless if they
have a route or not.

NOTE: The entire GUI has two listeners
	- when ENTER is pressed, trains go when they have a route.
	- When S is pressed, no train will go regardless if they have a route
	- There's a text label informing you which is currently active

=================Scenarios=================
1. To get trains to go immediately when you give them a route, press enter once all trains have been
placed. Whenever you give a train a route, it will go immediately.

2. To get multiple trains to be traveling concurrently, once all trains have been placed
give each their own destination (Step 3). Once all the trains you want have their own route,
press ENTER and all trains with a route will start going. Once the trains have reached their destination,
press S to stop trains from going immediately when they have a route.

=============Map Layout Rules==============
Our program can use any type of map layout as long as the data file follows the below rules:

1. A switch can't be reused for another diagonal track
2. A track can't have both a light and switch on it
3. The first and last track may not have a switch or light on them
4. Components such as lights or switches always come after the track they're on, with the same x and y coord.
5. Diagonal tracks are in between two rails, so they will have odd y values in XML file.
6. There can not be more than 3 diagonal tracks per "Diagonal Track Rail" (the rail in between two actual rails).
7. Diagonal Tracks must have a top and bottom switch, differing in 1 x unit and 1 y unit on different rails
8. Do not place components on a diagonal track
9. Clearly the file must be a XML file.

Follow the format of the current Data.XML file in resources.
Ex: Diag track going right.
		...
	   <Track><x>4</x><y>2</y></Track>	//one rail
	   <Switch><x>4</x><y>2</y></Switch>
		...
	   <Track><x>5</x><y>3</y></Track>     //another rail for diag track
		...
	   <Track><x>6</x><y>4</y></Track>
	   <Switch><x>6</x><y>4</y></Switch>	//another rail

*There is no check for correct configuration, so you must specify the data.xml file like above.
You can add more rails or fewer if you would like.

========Known bugs========
The jar file needs to be in the SmartRail folder to run with resources, it can't run anywhere.

========Note Worthy Extra Implementations========
1. Can place more than one train, up to one less than the max number of stations.

2. Different trains can move at the same time.

3. When a route is blocked by locked tracks, a train will move up to the nearest light,
wait at the light for the route to open up, then proceed to the destination.

EX: Place two trains, one at Station 0 and the other at station 6. Tell train @ station 0
to head to station 5. Tell train at station 6 to head to station 5. Press enter.
The train that was at station 6 has now moved to the light in front of it, but it
is red and locked, so it will wait at the light. The train at station 0 is now at station 5.
Now, tell the train now at Station 5 to go to any valid station 0, 2, or 4
(note you can try 6 for a deadlock but the case won't be allowed). The train from
station 5 will eventually cross a point allowing the train that was at Station 6 to get to Station 5,
the light the train was waiting at will turn green and it will lock the route to Station 5 now that it
is available. This last feature was the hardest to implement, as it's the only feature that really
tests for deadlock possibilities, race conditions, and actual route locking.

========GitHub========
Most... almost all commits should be able to compile. If there
are any that can't, they would have to be one of the first commits that at this point
we can't remember. I recommend the following version for an early commit (17/123+)
This version tests route finding with text based output.
https://github.com/dnguyen71/SmartRails/tree/8175be46ad692d20579f3b9490491f1f3e5a0fe5

=========Ideas to implement when coming back=========
- Start menu

- Different pre-built xml files to choose from, maybe picture showing map layout.

- Ability to add trains freely

- Ability to reset trains to 0

* Trains move to nearest possible light to destination/locked route instead of
nearest light to the start.

* Simulation mode
	=> Take in # trains at start or generate random #
	=> trains randomly choose routes and move freely

----------------------------------------------------------------------------------------
*dock the 10! But make sure to look at the extra implementations :^)
