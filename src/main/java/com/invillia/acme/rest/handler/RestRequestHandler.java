package com.invillia.acme.rest.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.io.BufferedReader;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.util.StringUtils;
import com.invillia.acme.rest.exception.DataAccessException;
import com.invillia.acme.rest.exception.UnhandledContentTypeException;
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
	public static final String RESOURCE_NAME_KEY = "resource";
	public static final String HTTP_METHOD_NAME_KEY = "httpMethod";
	public static final String HEADERS_KEY = "headers";
	public static final String CONTENT_TYPE_KEY = "Content-Type";
	public static final String QUERY_STRING_PARAMETERS_KEY = "queryStringParameters";
	public static final String PATH_PARAMETERS_KEY = "pathParameters";
	public static final String BODY_KEY = "body";
	public static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final String ACCEPTED_CONTENT_TYPE = "application/json";
	
	protected static final JSONParser PARSER = new JSONParser();
	protected HttpRequest httpRequest = new HttpRequest();
	protected JSONObject input;
	
	
	public RestRequestHandler(RestRequestHandler parent) {
		this.httpRequest = parent.httpRequest;
	}
	
	public RestRequestHandler() {

	}
	
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

		httpRequest = new HttpRequest();
		LambdaLogger logger = context.getLogger();
		logger.log("Loading Java Lambda handler of ProxyWithStream");

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

		JSONObject responseJson = null;

		try {
			input = (JSONObject) PARSER.parse(reader);
			System.out.println("RestRequestHandler.handleRequest(InputStream, OutputStream, Context): input: ");
			System.out.println(input);

			this.setHeaders();
			UnhandledContentTypeException uctException = null;
			if (httpRequest.getHeaders() == null ||
				!ACCEPTED_CONTENT_TYPE.equals(httpRequest.getHeaders().get(CONTENT_TYPE_KEY))) {
				uctException = new UnhandledContentTypeException(httpRequest.getHeaders().get(ACCEPTED_CONTENT_TYPE));
			}
					
			if (uctException != null) {
				responseJson = new JSONObjectResponseBuilder(HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE, null, null, uctException).build();
			} else {
				this.setMethod();
				this.setPathParameters();
				this.setPath();
				this.setResource();
				this.setQueryStringParameters();
				this.setBody();
				responseJson = new ResourceHandlerFactory(httpRequest.getResource(), this).getImpl().executeMethod(httpRequest, context);

			}

		} catch (ParseException e) {
			e.printStackTrace();
			responseJson = new JSONObjectResponseBuilder(HttpStatus.SC_BAD_REQUEST, null, null, new Exception("Error parsing request.", e)).build();
		} catch (ValidationException e) {
			e.printStackTrace();
			responseJson = new JSONObjectResponseBuilder(HttpStatus.SC_BAD_REQUEST, null, null, e).build();
		}catch (DataAccessException e) {
			e.printStackTrace();
			responseJson = new JSONObjectResponseBuilder(HttpStatus.SC_INTERNAL_SERVER_ERROR, null, null, e).build();
		}
		catch (Exception e) {
			e.printStackTrace();
			responseJson = new JSONObjectResponseBuilder(HttpStatus.SC_INTERNAL_SERVER_ERROR, null, null, e).build();
		}
		OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
		writer.write(responseJson.toJSONString());
		writer.close();
	}


	private void setQueryStringParameters() {
		if (input.get(QUERY_STRING_PARAMETERS_KEY) != null) {
			httpRequest.setQueryStringParameters((Map<String,String>)input.get(QUERY_STRING_PARAMETERS_KEY));
		}
	}

	private void setPathParameters() {
		if (input.get(PATH_PARAMETERS_KEY) != null) {
			httpRequest.setPathParameters((Map<String, String>) input.get(PATH_PARAMETERS_KEY));
		}
	}
	
	private void setMethod() {
		if (input.get(HTTP_METHOD_NAME_KEY) != null) {
			httpRequest.setHttpMethod ((String) input.get(HTTP_METHOD_NAME_KEY));
		}
	}
	
	private void setPath() {
		if (input.get(PATH_NAME_KEY) != null) {
			httpRequest.setPath ((String) input.get(PATH_NAME_KEY));
		}
	}
	private void setBody() throws ParseException {
		if (!StringUtils.isNullOrEmpty((String)input.get("body"))) {
			httpRequest.setBody((JSONObject) PARSER.parse((String)input.get("body")));
		}
	}
	private void setHeaders() {
		if (input.get(HEADERS_KEY) != null) {
			httpRequest.setHeaders((Map<String, String>) input.get(HEADERS_KEY));
		}
	}
	private void setResource() {
		if (input.get(RESOURCE_NAME_KEY) != null) {
			httpRequest.setResource ((String) input.get(RESOURCE_NAME_KEY));
		}
	}
}