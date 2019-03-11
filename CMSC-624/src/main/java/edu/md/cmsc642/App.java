package edu.md.cmsc642;

/**
 * Hello world!
 *
 */
public class App 
{
	//4,2,2
	public int getLastTerm(int a, int b, int c)
	{
		for(int i=0;i<c;i++)
		{
			a *=b;
		}
		
		return a;
	}
	
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}
