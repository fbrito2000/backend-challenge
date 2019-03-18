package com.invillia.acme.rest.response;

import java.util.List;

import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;

import com.amazonaws.util.CollectionUtils;

public class JSONObjectResponseBuilder {

	private JSONObject responseJson = new JSONObject();

	public JSONObjectResponseBuilder(int statusCode, JSONObject responseHeaders, List<JSONObject> responsePayload,
			Exception exception) {

		if (statusCode == HttpStatus.SC_CREATED
				|| statusCode == HttpStatus.SC_METHOD_NOT_ALLOWED
				|| statusCode == HttpStatus.SC_NOT_FOUND
				|| statusCode == HttpStatus.SC_OK
				|| statusCode == HttpStatus.SC_BAD_REQUEST) {

			this.responseJson.put("statusCode", String.valueOf(statusCode));

		} else {
			this.responseJson.put("statusCode", String.valueOf(HttpStatus.SC_NOT_IMPLEMENTED));
		}

		JSONObject responseBody = new JSONObject();
		if (!CollectionUtils.isNullOrEmpty(responsePayload)) {
			responseBody.put("payload", responsePayload);
		} else if(exception != null) {
			if (exception.getMessage() != null) {
				responseBody.put("error", exception.getMessage());
			} else {
				responseBody.put("error", stackTraceToString(exception));
			}
		}
		
		JSONObject headerJson = new JSONObject();
		headerJson.put("x-custom-header", "my custom header value");

		responseJson.put("isBase64Encoded", false);
		responseJson.put("statusCode", String.valueOf(statusCode));
		responseJson.put("headers", headerJson);
		responseJson.put("body", responseBody.toString());

	}

	public JSONObject build() {
		return responseJson;
	}
	
   private static String stackTraceToString(Throwable t) {
       String result = t.toString() + "\n";
       StackTraceElement[] trace = t.getStackTrace();
       for (int i=0;i<trace.length;i++) {
           result += trace[i].toString() + "\n";
       }
       return result;
   }
}