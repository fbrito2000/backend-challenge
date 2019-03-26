package com.invillia.acme.rest.dao.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.util.StringUtils;
//import com.baeldung.dynamodb.entity.ProductInfo;
//import com.baeldung.dynamodb.orderDAO.OrderDynamoDBDAOImpl;
//import com.baeldung.dynamodb.rule.LocalDbCreationRule;
import com.invillia.acme.rest.dao.impl.OrderDynamoDBDAOImpl;
import com.invillia.acme.rest.dynamodb.DynamoDBLocalConfig;
import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.filter.OrderFilter;
import com.invillia.acme.rest.model.Order;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

//import static org.hamcrest.Matchers.greaterThan;
//import static org.hamcrest.core.Is.is;
//import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class OrderDynamoDBDAOTest {

	@ClassRule
    public static DynamoDBLocalConfig dynamoDB = new DynamoDBLocalConfig();

    private static DynamoDBMapper dynamoDBMapper;
    private static AmazonDynamoDB amazonDynamoDB;

    private OrderDynamoDBDAOImpl orderDAO;

    private static final String DYNAMODB_ENDPOINT = "amazon.dynamodb.endpoint";
    private static final String AWS_ACCESSKEY = "amazon.aws.accesskey";
    private static final String AWS_SECRETKEY = "amazon.aws.secretkey";
    private static final String AWS_REGION = "amazon.aws.region";

    @BeforeClass
    public static void setupClass() {
        Properties testProperties = loadFromFileInClasspath("test.properties")
                .filter(properties -> !StringUtils.isNullOrEmpty(properties.getProperty(AWS_ACCESSKEY)))
                .filter(properties -> !StringUtils.isNullOrEmpty(properties.getProperty(AWS_SECRETKEY)))
                .filter(properties -> !StringUtils.isNullOrEmpty(properties.getProperty(DYNAMODB_ENDPOINT)))
                .orElseThrow(() -> new RuntimeException("Unable to get all of the required test property values"));

        String amazonAWSAccessKey = testProperties.getProperty(AWS_ACCESSKEY);
        String amazonAWSSecretKey = testProperties.getProperty(AWS_SECRETKEY);
        String amazonDynamoDBEndpoint = testProperties.getProperty(DYNAMODB_ENDPOINT);

        EndpointConfiguration endpointConfiguration = new EndpointConfiguration(amazonDynamoDBEndpoint, AWS_REGION); 
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey);
        amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(endpointConfiguration).withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
    }

    @Before
    public void setup() {
        try {
            orderDAO = new OrderDynamoDBDAOImpl();
            orderDAO.setMapper(dynamoDBMapper);

            CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(Order.class);
            tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
            amazonDynamoDB.createTable(tableRequest);

        } catch (ResourceInUseException e) {
            // Do nothing, table already created
        }

        try {
			dynamoDBMapper.batchDelete((List<Order>) orderDAO.query(new OrderFilter()));
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
    }

    @After
    public void tearDown() {
    	DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
        Table table = dynamoDB.getTable("Order");

        try {
            table.delete();
            table.waitForDelete();
        }
        catch (Exception e) {
            System.err.println("Unable to delete table: ");
            System.err.println(e.getMessage());
        }
    }
    
    @Test
    public void createOrderAndRetrieveById() {

        Order order = new Order();
        try {
			orderDAO.createOrUpdate(order);
		} catch (DataAccessException e) {
			e.printStackTrace();
			fail();
		}

        List<Order> result;
		try {
			result = (List<Order>) orderDAO.query(new OrderFilter());
	        assertEquals(1, result.size());
		} catch (DataAccessException e) {
			e.printStackTrace();
			fail();
		}
    }

    private static Optional<Properties> loadFromFileInClasspath(String fileName) {
        InputStream stream = null;
        try {
            Properties config = new Properties();
            Path configLocation = Paths.get(ClassLoader.getSystemResource(fileName).toURI());
            stream = Files.newInputStream(configLocation);
            config.load(stream);
            return Optional.of(config);
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }	
	
}
	
	
	
	
	
	
	
	
	
	
	
	

