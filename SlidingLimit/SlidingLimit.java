import java.util.LinkedList;

//This program solves sliding puzzle. It uses breadth first/depth first search
//with depth limit
public class SlidingLimit
{
    //Board class (inner class)
    private class Board
    {
        private char[][] array;                 //board array
        private int depth;                      //board depth
        private Board parent;                   //parent board

        //Constructor of board class
        private Board(char[][] array, int size)
        {
            this.array = new char[size][size];  //create board array

            for (int i = 0; i < size; i++)      //copy given array
                for (int j = 0; j < size; j++)
                    this.array[i][j] = array[i][j];

            this.depth = 1;                     //depth one

            this.parent = null;                 //no parent
        }
    }

    private Board initial;                      //initial board
    private Board goal;                         //final board
    private int size;                           //board size
    private int limit;                          //depth limit        

    //Constructor of SlidingLimit class
    public SlidingLimit(char[][] initial, char[][] goal, int size, int limit)
    {
        this.size = size;                           //set board size
        this.limit = limit;                         //set depth limit
        this.initial = new Board(initial, size);    //create initial board
        this.goal = new Board(goal, size);          //create final board
    }

    //Method solves sliding puzzle
    public void solve()
    {
         LinkedList<Board> openList = new LinkedList<Board>();  //open list
         LinkedList<Board> closedList = new LinkedList<Board>();//closed list

         openList.addFirst(initial);   //add initial board to open list     

         while (!openList.isEmpty())   //while open list has more boards
         {
             Board board = openList.removeFirst();  //remove first board from open list
                                                    
             closedList.addLast(board);             //add board to closed list

             if (goal(board))                       //if board is goal
             {
                 displayPath(board);                //display path to goal
                 return;                            //stop search
             }
             else                                   //if board is not goal
             {
                 if (board.depth < limit)           //if board depth is less than depth limit
                 {
                     LinkedList<Board> children = generate(board);//create children

                     for (int i = 0; i < children.size(); i++)
                     {
                        Board child = children.get(i);  //for each child
                                                     
                        if (!exists(child, openList) && !exists(child, closedList))
                            openList.addLast(child);    //if child is not in open and closed
                     }                                  //lists then add it to open list
                 }
             }                                       
         }       //breadth first search - adding at end - addLast()
                 //depth first search - adding at begining - addFirst()

         System.out.println("no solution");          //no solution if there are
    }                                                //no boards in open list

    //Method creates children of a board
    private LinkedList<Board> generate(Board board)
    {
        int i = 0, j = 0;
        boolean found = false;

        for (i = 0; i < size; i++)              //find location of empty slot
        {                                      
            for (j = 0; j < size; j++)
                if (board.array[i][j] == ' ')
                {   
                    found = true;
                    break;
                }
            
            if (found)
               break;
        }

        boolean north, south, east, west;       //decide whether empty slot
        north = i == 0 ? false : true;          //has N, S, E, W neighbors
        south = i == size-1 ? false : true;
        east = j == size-1 ? false : true; 
        west = j == 0 ? false : true;

        LinkedList<Board> children = new LinkedList<Board>();//list of children

        if (north) children.addLast(createChild(board, i, j, 'N')); //add N, S, E, W
        if (south) children.addLast(createChild(board, i, j, 'S')); //children if 
        if (east) children.addLast(createChild(board, i, j, 'E'));  //they exist
        if (west) children.addLast(createChild(board, i, j, 'W'));  
                                                                    
        return children;                        //return children      
    }

    //Method creates a child of a board by swapping empty slot in a 
    //given direction
    private Board createChild(Board board, int i, int j, char direction)
    {
        Board child = copy(board);                   //create copy of board

        if (direction == 'N')                        //swap empty slot to north
        {
            child.array[i][j] = child.array[i-1][j];
            child.array[i-1][j] = ' ';
        }
        else if (direction == 'S')                   //swap empty slot to south
        {
            child.array[i][j] = child.array[i+1][j];
            child.array[i+1][j] = ' ';
        }
        else if (direction == 'E')                   //swap empty slot to east
        {
            child.array[i][j] = child.array[i][j+1];
            child.array[i][j+1] = ' ';
        }
        else                                         //swap empty slot to west
        {
            child.array[i][j] = child.array[i][j-1];
            child.array[i][j-1] = ' ';
        }

        child.depth = board.depth + 1;               //set depth of child

        child.parent = board;                        //set parent of child
        
        return child;                                //return child
    }

    //Method creates copy of a board
    private Board copy(Board board)
    {
        return new Board(board.array, size);
    }

    //Method decides whether a board is goal
    private boolean goal(Board board)
    {
        return identical(board, goal);            //compare board with goal
    }                                             

    //Method decides whether a board exists in a list
    private boolean exists(Board board, LinkedList<Board> list)
    {
        for (int i = 0; i < list.size(); i++)    //compare board with each
            if (identical(board, list.get(i)))   //element of list
               return true;

        return false;
    }

    //Method decides whether two boards are identical
    private boolean identical(Board p, Board q)
    {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (p.array[i][j] != q.array[i][j])
                    return false;      //if there is a mismatch then false

        return true;                   //otherwise true
    }

    //Method displays path from initial to current board
    private void displayPath(Board board)
    {
        LinkedList<Board> list = new LinkedList<Board>();

        Board current = board;         //start at current board

        while (current != null)        //go back towards initial board
        {
            list.addFirst(current);    //add boards to list

            current = current.parent;  //keep going back
        }
                                       //print boards in list
        for (int i = 0; i <  list.size(); i++)  
            displayBoard(list.get(i));
    }

    //Method displays board
    private void displayBoard(Board board)
    {
        for (int i = 0; i < size; i++) //print each element of board
        {
            for (int j = 0; j < size; j++)
                System.out.print(board.array[i][j] + " ");
            System.out.println();
        }   
        System.out.println();     
    }
}
