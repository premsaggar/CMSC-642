/**
 * 
 */
package edu.md.cmsc642;

import static org.junit.jupiter.api.Assertions.*;
import org.apache.spark.*;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.jupiter.api.Test;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.athena.AmazonAthenaClientBuilder;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClientBuilder;

/**
 * @author prems
 *
 */
class SparkConnectionTest
{
	
			
	@Test
	void test()
	{
		AmazonElasticMapReduce emr = AmazonElasticMapReduceClientBuilder.standard()
				.withRegion(Regions.US_EAST_1)
				.build();
		
		SparkConf conf = new SparkConf().setAppName("WorkCountApp").setMaster("spark://masterIP:54.163.222.159");
		JavaSparkContext sc = new JavaSparkContext(conf);
		sc.env();
	}

}
