//Tester program for sliding board solver
public class SlidingTester
{
    //main method for testing
    public static void main(String[] args)
    {
        //initial board
        char[][] initial = {{'5', '7', '1'},
                            {'2', ' ', '8'},
                            {'4', '6', '3'}};

        //final board
        char[][] goal = {{'5', ' ', '1'},
                         {'4', '7', '8'},
                         {'6', '2', '3'}};

        //solve sliding puzzle
        Sliding s = new Sliding(initial, goal, 3);
        s.solve();
    }
}

