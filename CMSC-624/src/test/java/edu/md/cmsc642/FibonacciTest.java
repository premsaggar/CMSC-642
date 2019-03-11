package edu.md.cmsc642;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.md.cmsc642.Fibonacci;

class FibonacciTest
{

	@Test
	public void test()
	{
		Fibonacci fibonacci = new Fibonacci();
		int actual = fibonacci.getFib(3);
		Assertions.assertEquals(3, actual);
	}
	
	@Test
	public void testRecursive()
	{
		Fibonacci fibonacci = new Fibonacci();
		int actual = fibonacci.getFibRecursive(2);
		Assertions.assertEquals(2, actual);

	}
	

}
