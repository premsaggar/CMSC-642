package edu.md.cmsc642;

import org.junit.jupiter.api.*;
import java.util.*;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import java.util.List;

public class AWSConnectTest
{
	@Test
	public void testConnection() throws Exception
	{
		
		

		final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		List<Bucket> buckets = s3.listBuckets();
		System.out.println("Your Amazon S3 buckets are:");
		for (Bucket b : buckets)
		{
			System.out.println("* " + b.getName());
		}

	}

}
