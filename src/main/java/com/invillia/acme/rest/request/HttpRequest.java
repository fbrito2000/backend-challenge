package com.invillia.acme.rest.request;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class HttpRequest {

	private static final long serialVersionUID = 2800194276471858539L;
	
	public static final String HTTP_METHOD_POST = "POST";
	public static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_METHOD_PUT = "PUT";
	public static final String HTTP_METHOD_DELETE = "DELETE";
	
	private Map<String, String> queryStringParameters = new HashMap<String, String>();
	private Map<String, String> pathParameters = new HashMap<String, String>();
	private String path = "";
	private String httpMethod = "";
	private Object body = null;
	
	public Map<String, String> getQueryStringParameters() {
		return queryStringParameters;
	}
	public void setQueryStringParameters(Map<String,String> queryStringParameters) {
		this.queryStringParameters = queryStringParameters;
	}
	public Map<String, String> getPathParameters() {
		return pathParameters;
	}
	public void setPathParameters(Map<String, String> pathParameters) {
		this.pathParameters = pathParameters;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getHttpMethod() {
		return httpMethod;
	}
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}
	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}
}
