package CMSC_642.CMSC_624;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        System.out.println( "non-recursive" );

        printPuzzle(3,2,3);
        printPuzzle(4,3,2);
        printPuzzle(3,4,4);

        System.out.println( "recursive" );

        printPuzzleRecursive(3,2,3);
        printPuzzleRecursive(4,3,2);
        printPuzzleRecursive(3,4,4);

    }
    
    public static void printPuzzle(int a, int b, int c)
    {
    	int prev = a; 
    	for (int i = 0; i < c; i++) {
    		//System.out.println("fooooo");
    		prev = prev * b; 
    		System.out.print(prev);
    		System.out.print(" ");
    	}
    	System.out.println();
    }

    public static void printPuzzleRecursive(int a, int b, int c)
    {
    	int num = a * b; 
    	System.out.print(num);
    	System.out.print(" ");
    	if (c>1) {
    		printPuzzleRecursive(num,b,c-1);
    	} else {
    		System.out.println();
    	}
    		
    }

}
