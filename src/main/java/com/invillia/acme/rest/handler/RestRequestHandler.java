package com.invillia.acme.rest.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.io.BufferedReader;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.invillia.acme.rest.exception.ValidationException;
import com.invillia.acme.rest.handler.resource.factory.ResourceHandlerFactory;
import com.invillia.acme.rest.request.HttpRequest;
import com.invillia.acme.rest.response.JSONObjectResponseBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class RestRequestHandler implements RequestStreamHandler {

	protected static final String API_NAME = "prod";

	public static final String PATH_NAME_KEY = "path";
	public static final String HTTP_METHOD_NAME_KEY = "httpMethod";
	public static final String QUERY_STRING_PARAMETERS_KEY = "queryStringParameters";
	public static final String PATH_PARAMETERS_KEY = "pathParameters";
	public static final String BODY_KEY = "body";
	
	protected static final JSONParser PARSER = new JSONParser();

	/*public static final String HTTP_METHOD_POST = "POST";
	public static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_METHOD_PUT = "PUT";
	public static final String HTTP_METHOD_DELETE = "DELETE";*/


	/*protected Map<String, String> queryStringParameters = new HashMap<String, String>();
	protected Map<String, String> pathParameters = new HashMap<String, String>();
	protected String path = "";
	protected String httpMethod = "";*/
	
	protected HttpRequest httpRequest = new HttpRequest();
	
	protected JSONObject input;
	
	
	/*public RestRequestHandler(RestRequestHandler parent) {
		this.httpMethod = parent.httpMethod;
		this.pathParameters = parent.pathParameters;
		this.queryStringParameters = parent.queryStringParameters;
		this.path = parent.path;
	}*/
	
	public RestRequestHandler(RestRequestHandler parent) {
		/*this.httpMethod = parent.httpMethod;
		this.pathParameters = parent.pathParameters;
		this.queryStringParameters = parent.queryStringParameters;
		this.path = parent.path;*/
		this.httpRequest = parent.httpRequest;
	}
	
	public RestRequestHandler() {

	}
	
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

		LambdaLogger logger = context.getLogger();
		logger.log("Loading Java Lambda handler of ProxyWithStream");

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

		JSONObject responseJson = new JSONObject();

		try {

			// EVENT
			//JSONObject request = (JSONObject) PARSER.parse(reader);
			input = (JSONObject) PARSER.parse(reader);
			System.out.println("---------------- input (BEGIN) ---------------- ");
			System.out.println(input);
			System.out.println("---------------- input (END) ---------------- ");

			System.out.println("---------------- path (BEGIN) ---------------- ");
			System.out.println(input.get(PATH_NAME_KEY));
			System.out.println("---------------- path (END) ---------------- ");

			this.setMethod();
			this.setPathParameters();
			this.setPath();
			this.setQueryStringParameters();
			this.setBody();


			responseJson = new ResourceHandlerFactory(httpRequest.getPath(), this).getImpl().executeMethod(httpRequest, context);

		} catch (ParseException e) {
			responseJson = new JSONObjectResponseBuilder(HttpStatus.SC_BAD_REQUEST, null, null, e).build();
		} catch (ValidationException e) {
			responseJson = new JSONObjectResponseBuilder(HttpStatus.SC_BAD_REQUEST, null, null, e).build();
		}

		OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
		writer.write(responseJson.toJSONString());
		writer.close();
	}


	private void setQueryStringParameters() {
		// QUERY STRING PARAMETERS
		if (input.get(QUERY_STRING_PARAMETERS_KEY) != null) {
			//this.queryStringParameters = (Map<String, String>) request.get(QUERY_STRING_PARAMETERS_KEY);
			httpRequest.setQueryStringParameters((Map<String,String>)input.get(QUERY_STRING_PARAMETERS_KEY));
			System.out.println("input.queryStringParameters --> " + httpRequest.getQueryStringParameters());
		}
	}

	private void setPathParameters() {
		// PATH PARAMETERS
		if (input.get(PATH_PARAMETERS_KEY) != null) {
			httpRequest.setPathParameters((Map<String, String>) input.get(PATH_PARAMETERS_KEY));
			System.out.println("input.pathParameters --> " + httpRequest.getPathParameters());
		}
	}
	
	private void setMethod() {
		if (input.get(HTTP_METHOD_NAME_KEY) != null) {
			httpRequest.setHttpMethod ((String) input.get(HTTP_METHOD_NAME_KEY));
			System.out.println("input.method --> " + httpRequest.getHttpMethod());
		}
	}
	
	private void setPath() {
		if (input.get(PATH_NAME_KEY) != null) {
			httpRequest.setPath ((String) input.get(PATH_NAME_KEY));
			System.out.println("input.path --> " + httpRequest.getPath() );
		}
	}
	private void setBody() {
		httpRequest.setBody(input.get("body"));
	}
}