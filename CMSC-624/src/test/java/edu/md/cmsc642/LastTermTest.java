package edu.md.cmsc642;

import org.junit.jupiter.api.*;

import edu.md.cmsc642.App;

/**
 * Unit test for simple App.
 */
public class LastTermTest
{
    
    @Test
	public void getLastTerm()
    {
    	App a = new App();
    	int got = a.getLastTerm(3,4,3);
    	Assertions.assertEquals(got, 192);
    }
}
