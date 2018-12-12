/**
 * after more thought, I think this entire class can be removed and all of the code can be copied
 * to the GUI. Let me know what you think
 *
 * -T
 */



public class Coordination
{
    private String mapLayout;
    private Train train; //for now the coordinator or coordination will always start with one train

    /**
     * right now my idea is that the coordination will be passed
     * the configuration of the map from the main, we will then have to parse
     * the map here and create the map.
     *
     * OR
     *
     * We parse the file as we read it, and pass the Coordination object the different parameters.
     * -T
     * station = A, B
     * train = +
     * track = _
     * light = I
     * switches = & or something
     * @param mapLayout
     */
    public Coordination(String mapLayout)
    {
        //WE WOULD MAKE THE MAP HERE, BUT FOR NOW WE WILL HARD CODE OUR OWN STRING AND PARSER -T
        this.mapLayout = "A +_ _ _ I _ _ I _ I _ _ B"; //1 rail, 1 train, 8 tracks, 3 lights, station A and B -T
        readMapLayout(mapLayout);
    }

    /**
     * read mapLayout will be our parser to read the string
     * and make the objects as necessary, this will be pretty heavy duty since
     * it will be making the coordinates of each object for the GUI, and giving each object
     * the identification of it's neighboring objects.
     *
     *
     * -T
     * @param mapLayout
     */
    public void readMapLayout(String mapLayout)
    {

    }












    /*
    I'm not sure what this code was for so I commented it out for now.
    My thoughts are that the coordinator is the map maker the GUI maker. It will initiate all
    of our objects and give them their correct location according to the XML file, and then it
    will no longer be needed because each objects will have their own thread. -T

    After more thought I think the GUI may just make the layout, and we don't need this object.


    private double x, y;

    public void setX(double x)
    {
        this.x = x;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }
    */
}
