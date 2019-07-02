package com.amazon.machineLearning;

import org.apache.commons.math4.ml.clustering.Clusterable;

/**
 * Wrapper class for an image with 1 red, green, and blue point
 * The most important part is that it implements Clusterable so k-means++ can compute distances on it
 * @author Prem
 *
 */
public class KMeansForImagery implements Clusterable
{

	/**
	 * image color values
	 */
	private int red, green, blue;
	/**
	 * an image name for convenience, note name is not used in distance computation
	 */
	private String name;

	public KMeansForImagery(int red, int green, int blue, String name)
	{
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.name = name;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + blue;
		result = prime * result + green;
		result = prime * result + red;
		return result;
	}

	@Override
	/**
	 * key equals method, note name is not used for equality only the red, green, and blue values
	 */
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KMeansForImagery other = (KMeansForImagery) obj;
		if (blue != other.blue)
			return false;
		if (green != other.green)
			return false;
		if (red != other.red)
			return false;
		return true;
	}

	public int getRed()
	{
		return red;
	}

	public void setRed(int red)
	{
		this.red = red;
	}

	public int getGreen()
	{
		return green;
	}

	public void setGreen(int green)
	{
		this.green = green;
	}

	public int getBlue()
	{
		return blue;
	}

	public void setBlue(int blue)
	{
		this.blue = blue;
	}

	@Override
	/**
	 * key method to see actual image values
	 */
	public String toString()
	{
		return "KMeansForImagery [red=" + red + ", green=" + green + ", blue=" + blue + ", name=" + name + "]";
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * key interface method that sets the values to compute distances on
	 */
	public double[] getPoint()
	{
		double[] array = { this.getRed(), this.getGreen(), this.getBlue() };
		return array;

	}

}
