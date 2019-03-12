package edu.md.cmsc642;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.athena.AmazonAthena;
import com.amazonaws.services.athena.AmazonAthenaClientBuilder;
import com.amazonaws.services.athena.model.ColumnInfo;
import com.amazonaws.services.athena.model.GetQueryExecutionRequest;
import com.amazonaws.services.athena.model.GetQueryExecutionResult;
import com.amazonaws.services.athena.model.GetQueryResultsRequest;
import com.amazonaws.services.athena.model.GetQueryResultsResult;
import com.amazonaws.services.athena.model.QueryExecutionContext;
import com.amazonaws.services.athena.model.QueryExecutionState;
import com.amazonaws.services.athena.model.ResultConfiguration;
import com.amazonaws.services.athena.model.Row;
import com.amazonaws.services.athena.model.StartQueryExecutionRequest;
import com.amazonaws.services.athena.model.StartQueryExecutionResult;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * convenience class for client auth object, Please note you must set your
 * region! AWS does not have your assets everywhere. S3 is region specific. So
 * hope your region is safe!
 * 
 * @author prems
 *
 */
class AthenaClientFactory
{
	/**
	 * AmazonAthenaClientBuilder to build Athena with the following properties: -
	 * Set the region of the client - Use the instance profile from the EC2 instance
	 * as the credentials provider - Configure the client to increase the execution
	 * timeout.
	 */
	private final AmazonAthenaClientBuilder builder = 
			AmazonAthenaClientBuilder.standard().withRegion(Regions.US_EAST_1).withClientConfiguration
			(
					new ClientConfiguration().withClientExecutionTimeout(ExampleConstants.CLIENT_EXECUTION_TIMEOUT)
			);

	/**
	 * convenience builder method, allows multiple calls to the factory for multiple
	 * builders
	 * 
	 * @return
	 */
	public AmazonAthena createClient()
	{
		return builder.build();
	}
}

/**
 * convenience class for AWS inputs
 * 
 * @author prems
 *
 */
class ExampleConstants
{

	/**
	 * total time to wait (nice idea, why?)
	 */
	public static final int CLIENT_EXECUTION_TIMEOUT = 1000;
	/**
	 * must be a different folder and not a sub folder!
	 */
	public static final String ATHENA_OUTPUT_BUCKET = "s3://642premdemo/CrimeOutput/";
	/**
	 *  Your Athena query
	 */
	public static final String ATHENA_SAMPLE_QUERY = "SELECT * from crime;";
	
	/**
	 * time to poll for results being done
	 */
	public static final long SLEEP_AMOUNT_IN_MS = 100;
	
	/**
	 * your Athena database
	 */
	public static final String ATHENA_DEFAULT_DATABASE = "bigjoin1";

}

/**
 * StartQueryExample ------------------------------------- This code shows how
 * to submit a query to Athena for execution, wait till results are available,
 * and then process the results.
 */
public class AthenaTest
{
	/**
	 * in case you want to run it as a program
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		//create an Athena test object
		AthenaTest test = new AthenaTest();
		//call test method
		test.testAthena();
	}

	/**
	 * in case you want to run it as a test
	 * @throws Exception
	 */
	@Test
	public void testAthena() throws Exception
	{
		// Build an Amazon Athena client
		AthenaClientFactory factory = new AthenaClientFactory();
		//get our client
		AmazonAthena client = factory.createClient();
		
		//submit your query and get the execution id to query on
		String queryExecutionId = submitAthenaQuery(client);

		//wait for the execution to complete by polling the execution id
		waitForQueryToComplete(client, queryExecutionId);

		//do some fun output with your 
		processResultRows(client, queryExecutionId);

	}

	/**
	 * Submits a sample query to Athena and returns the execution ID of the query.
	 */
	private static String submitAthenaQuery(AmazonAthena client)
	{
		// The QueryExecutionContext allows us to set the Database.
		QueryExecutionContext queryExecutionContext = new QueryExecutionContext()
				.withDatabase(ExampleConstants.ATHENA_DEFAULT_DATABASE);

		// The result configuration specifies where the results of the query should go
		// in S3 and encryption options
		ResultConfiguration resultConfiguration = new ResultConfiguration()
				// You can provide encryption options for the output that is written.
				// .withEncryptionConfiguration(encryptionConfiguration)
				.withOutputLocation(ExampleConstants.ATHENA_OUTPUT_BUCKET);

		// Create the StartQueryExecutionRequest to send to Athena which will start the
		// query.
		StartQueryExecutionRequest startQueryExecutionRequest = new StartQueryExecutionRequest()
				.withQueryString(ExampleConstants.ATHENA_SAMPLE_QUERY).withQueryExecutionContext(queryExecutionContext)
				.withResultConfiguration(resultConfiguration);

		//launch the query
		StartQueryExecutionResult startQueryExecutionResult = client.startQueryExecution(startQueryExecutionRequest);
		
		//return the query execution id
		return startQueryExecutionResult.getQueryExecutionId();
	}

	/**
	 * Wait for an Athena query to complete, fail or to be cancelled. This is done
	 * by polling Athena over an interval of time. If a query fails or is cancelled,
	 * then it will throw an exception.
	 */
	private static void waitForQueryToComplete(AmazonAthena client, String queryExecutionId) throws InterruptedException
	{
		//request for query execution
		GetQueryExecutionRequest getQueryExecutionRequest = new GetQueryExecutionRequest()
				.withQueryExecutionId(queryExecutionId);

		//result for query execution
		GetQueryExecutionResult getQueryExecutionResult = null;
		boolean isQueryStillRunning = true;
		
		//while the query is still running, wait, else handle conditions
		while (isQueryStillRunning)
		{
			getQueryExecutionResult = client.getQueryExecution(getQueryExecutionRequest);
			String queryState = getQueryExecutionResult.getQueryExecution().getStatus().getState();
			if (queryState.equals(QueryExecutionState.FAILED.toString()))
			{
				throw new RuntimeException("Query Failed to run with Error Message: "
						+ getQueryExecutionResult.getQueryExecution().getStatus().getStateChangeReason());
			}
			else if (queryState.equals(QueryExecutionState.CANCELLED.toString()))
			{
				throw new RuntimeException("Query was cancelled.");
			}
			else if (queryState.equals(QueryExecutionState.SUCCEEDED.toString()))
			{
				isQueryStillRunning = false;
			}
			else
			{
				// Sleep an amount of time before retrying again.
				Thread.sleep(ExampleConstants.SLEEP_AMOUNT_IN_MS);
			}
			System.out.println("Current Status is: " + queryState);
		}
	}

	/**
	 * This code calls Athena and retrieves the results of a query. The query must
	 * be in a completed state before the results can be retrieved and paginated.
	 * The first row of results are the column headers.
	 */
	private static void processResultRows(AmazonAthena client, String queryExecutionId)
	{
		GetQueryResultsRequest getQueryResultsRequest = new GetQueryResultsRequest()
				// Max Results can be set but if its not set,
				// it will choose the maximum page size
				// As of the writing of this code, the maximum value is 1000
				// .withMaxResults(1000)
				.withQueryExecutionId(queryExecutionId);

		GetQueryResultsResult getQueryResultsResult = client.getQueryResults(getQueryResultsRequest);
		List<ColumnInfo> columnInfoList = getQueryResultsResult.getResultSet().getResultSetMetadata().getColumnInfo();

		while (true)
		{
			List<Row> results = getQueryResultsResult.getResultSet().getRows();
			for (Row row : results)
			{
				System.out.println("row=" + row.getData());
				// Process the row. The first row of the first page holds the column names.
				processRow(row, columnInfoList);
			}
			// If nextToken is null, there are no more pages to read. Break out of the loop.
			if (getQueryResultsResult.getNextToken() == null)
			{
				break;
			}
			getQueryResultsResult = client
					.getQueryResults(getQueryResultsRequest.withNextToken(getQueryResultsResult.getNextToken()));
		}
	}

	/**
	 * method to process each column of each row
	 * @param row
	 * @param columnInfoList
	 */
	private static void processRow(Row row, List<ColumnInfo> columnInfoList)
	{
		//for all columns in the row
		for (int i = 0; i < columnInfoList.size(); ++i)
		{
			//I suppose a switch works here
			//start with the 0th index and go until n
			switch (columnInfoList.get(i).getType())
			{
			case "varchar":
				// Convert and Process as String
				break;
			case "tinyint":
				// Convert and Process as tinyint
				break;
			case "smallint":
				// Convert and Process as smallint
				break;
			case "integer":
				// Convert and Process as integer
				break;
			case "bigint":
				// Convert and Process as bigint
				break;
			case "double":
				// Convert and Process as double
				break;
			case "boolean":
				// Convert and Process as boolean
				break;
			case "date":
				// Convert and Process as date
				break;
			case "timestamp":
				// Convert and Process as timestamp
				break;
			default:
				throw new RuntimeException("Unexpected Type is not expected" + columnInfoList.get(i).getType());
			}
		}
	}
}