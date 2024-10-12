//Tester program for tic-tac with min-max, depth limit, 
//board evaluation, and alph-beta pruning
public class AlphaBetaTester
{
   //main program for tester
   public static void main(String[] args)
   {
       //play tic-tac game
       AlphaBeta a = new AlphaBeta(4);
	   a.play();
   }
}