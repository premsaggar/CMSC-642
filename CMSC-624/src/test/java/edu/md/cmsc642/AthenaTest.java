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

class AthenaClientFactory
{
	/**
	 * AmazonAthenaClientBuilder to build Athena with the following properties: -
	 * Set the region of the client - Use the instance profile from the EC2 instance
	 * as the credentials provider - Configure the client to increase the execution
	 * timeout.
	 */
	private final AmazonAthenaClientBuilder builder = AmazonAthenaClientBuilder.standard()
			.withRegion(Regions.US_EAST_1);/*
											 * .withCredentials(InstanceProfileCredentialsProvider.getInstance())
											 * .withClientConfiguration(new
											 * ClientConfiguration().withClientExecutionTimeout(ExampleConstants.
											 * CLIENT_EXECUTION_TIMEOUT));
											 */

	public AmazonAthena createClient()
	{
		return builder.build();
	}
}

class ExampleConstants
{

	public static final int CLIENT_EXECUTION_TIMEOUT = 100000;
	/**
	 * must be a different folder and not a sub folder!
	 */
	public static final String ATHENA_OUTPUT_BUCKET = "s3://642premdemo/CrimeOutput/";
	// This is querying a table created by the getting started tutorial in Athena
	public static final String ATHENA_SAMPLE_QUERY = "SELECT * from crime;";
	public static final long SLEEP_AMOUNT_IN_MS = 100;
	public static final String ATHENA_DEFAULT_DATABASE = "bigjoin1";

}

/**
 * StartQueryExample ------------------------------------- This code shows how
 * to submit a query to Athena for execution, wait till results are available,
 * and then process the results.
 */
public class AthenaTest
{
	public static void main(String[] args) throws InterruptedException
	{
		// Build an AmazonAthena client
		AthenaClientFactory factory = new AthenaClientFactory();
		AmazonAthena client = factory.createClient();

		String queryExecutionId = submitAthenaQuery(client);

		waitForQueryToComplete(client, queryExecutionId);

		processResultRows(client, queryExecutionId);
	}

	@Test
	public void testAthena() throws Exception
	{
		// Build an Amazon Athena client
		AthenaClientFactory factory = new AthenaClientFactory();
		AmazonAthena client = factory.createClient();

		String queryExecutionId = submitAthenaQuery(client);

		waitForQueryToComplete(client, queryExecutionId);

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

		StartQueryExecutionResult startQueryExecutionResult = client.startQueryExecution(startQueryExecutionRequest);
		return startQueryExecutionResult.getQueryExecutionId();
	}

	/**
	 * Wait for an Athena query to complete, fail or to be cancelled. This is done
	 * by polling Athena over an interval of time. If a query fails or is cancelled,
	 * then it will throw an exception.
	 */

	private static void waitForQueryToComplete(AmazonAthena client, String queryExecutionId) throws InterruptedException
	{
		GetQueryExecutionRequest getQueryExecutionRequest = new GetQueryExecutionRequest()
				.withQueryExecutionId(queryExecutionId);

		GetQueryExecutionResult getQueryExecutionResult = null;
		boolean isQueryStillRunning = true;
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

	private static void processRow(Row row, List<ColumnInfo> columnInfoList)
	{
		for (int i = 0; i < columnInfoList.size(); ++i)
		{
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