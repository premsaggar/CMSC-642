package com.amazon.machineLearning;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.apache.commons.math4.ml.clustering.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.netlib.util.booleanW;

import spire.optional.intervalGeometricPartialOrder;


class KMeansForImageryTest
{

	@Test
	/**
	 * method that tests if KMeansPlusPlus identifies similar mock images of type KMeansForImagery
	 * The idea:
	 * Cluster all images using k-means++ to obtain similar images. K-means++ was chosen do to the smarter centroid initialization vs. random selection. 
	 * This needs to be unsupervised since we don't know the classifiers for the clusters. 
	 * We're trying to say this image is like those images instead of this image is something specific like a classification problem. 
	 * For k means we have to determine a distance.
	 * The distance will be the Euclidean distance based on pixel values. Let red =1, green = 2, and blue = 3. 
	 * For simplicity I've set the images (KMeansForImagery) to have 1 red, green, and blue value, and an initial cluster amount of 3. 
	 * Note: We can run with different cluster amounts and find an optimal amount of clusters (knee of the graph), 3 was chosen for simplicity.
	 * We can run k-means++ now based on the distances to obtain clusters. 
	 * Now we have clusters. When we receive an image to find similar images we insert the image into the clustered list and run k-means++ again.
	 * We can find the images assigned cluster and return it's clusters images to give the most similar clusters.
	 * 
	 */
	@RepeatedTest(10)
	public void testKmeansForImagery()
	{
		//create a k-means++ object with an initial cluster amount of 3
		KMeansPlusPlusClusterer<Clusterable> clusters = new KMeansPlusPlusClusterer<Clusterable>(3);
		
		//setup initial images (single points, with red, green, blue values, that implement Clusterable)
		//smaller values
		KMeansForImagery image1 = new KMeansForImagery(1, 1, 1, "One");
		KMeansForImagery image2 = new KMeansForImagery(3, 3, 3, "Three");
		
		//larger values
		KMeansForImagery image3 = new KMeansForImagery(5, 5, 5, "Five");
		KMeansForImagery image4 = new KMeansForImagery(6, 6, 6, "Six");
		KMeansForImagery image5 = new KMeansForImagery(7, 7, 7, "Seven");
		KMeansForImagery image6 = new KMeansForImagery(8, 8, 8, "Eight");

		//create a list of the initial images to cluster!
		ArrayList<Clusterable> list = new ArrayList<Clusterable>();
		list.add(image1);
		list.add(image2);
		list.add(image3);
		list.add(image4);
		list.add(image5);
		list.add(image6);
		
		//cluster the images
		List<CentroidCluster<Clusterable>> got = clusters.cluster(list);
		
		// iterate over the 3 clusters to see the clustered points
		for (CentroidCluster<Clusterable> found : got)
		{
			System.out.println("cluster center found = " + found.getCenter());
			for (Clusterable point : found.getPoints())
			{
				System.out.println("images in cluster = " + point);
			}
		}

		// add new image to find similar images of to the cluster and re-cluster
		KMeansForImagery checkImage = new KMeansForImagery(9, 9, 9, "Nine");
		list.add(checkImage);
		got = clusters.cluster(list);

		//line break for output of clustered values with image to check on, inserted and clustered
		System.out.println();
		System.out.println("iterate over the 3 clusters with new data point");
		
		boolean pointFoundAfterClustering = false;
		//for each cluster found, find all points in each cluster, find the image submitted, and then find it's associated clustered points
		for (CentroidCluster<Clusterable> found : got)
		{
			// search cluster center's points for the new image, if found then list all points
			// in cluster that are similar!
			List<Clusterable> allPointsInCluster = found.getPoints();
			if (allPointsInCluster.contains(checkImage))
			{
				//test variable to ensure we found the images to check similarity on
				pointFoundAfterClustering = true;
				System.out.println("found original image");
				
				//output all similar images in the cluster
				int pointsInClusterCount = 0;
				for (Clusterable point : allPointsInCluster)
				{
					//track the amount of images found in the submitted images cluster
					pointsInClusterCount++;
					
					//output found image values
					if(point.equals(checkImage))
					{
						System.out.println("exact image match found in cluster = "+ point);
					}
					else 
					{
						System.out.println("similar image match found in cluster = " + point);
					}
				}
				
				System.out.println("total points in associated cluster = "+pointsInClusterCount);
				//since k-means++ can cluster differently (based on initial centroid values) the sizes may vary but given the distribution and that this image
				//was added last it should have at least 2 images (verified via repeated tests annotation on 10 test runs)
				//Note: we can avoid the changing of clusters on successive runs by assigning the initial centroid values. This gives repeatable results but sacrifices 
				//statistically accuracy.
				Assertions.assertTrue(pointsInClusterCount>=2);
			}

		}
		
		//assert that you found the image to check on
		Assertions.assertTrue(pointFoundAfterClustering);
	}
}