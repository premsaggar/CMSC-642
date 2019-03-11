package CMSC_642.CMSC_624;

public class Fibonacci
{
	public int getFib(int n)
	{
		int one = 1;
		int two = 1;
		int temp = 0;

		if (n == 0)
		{
			return 1;
		}
		else if (n == 1)
		{
			return 1;
		}

		for (int i = 2; i <= n; i++)
		{
			temp = one + two;
			one = two;
			two = temp;
		}

		return temp;

	}

	public int getFibRecursive(int n)
	{
		int one = 1;
		int two = 1;
		int temp = 0;

		if (n == 0)
		{
			return 1;
		}
		else if (n == 1)
		{
			return 1;
		}
		else
		{
			return getFibRecursive(n-1) + getFibRecursive(n-2);
		}

	}

}
