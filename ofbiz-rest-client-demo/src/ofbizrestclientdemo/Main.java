/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 * Author: atb@isep.ipp.pt
 *******************************************************************************/
package ofbizrestclientdemo;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class Main {
	
	
	/**
	 * This method demonstrates the invocation of the rest ping service of ofbiz
	 * The rest ping service calls the "native" ping service:
	 * https://localhost:8443/webtools/control/ServiceList?sel_service_name=ping
	 * Note: see the "restcomponent" for details on how the rest service is implemented on the server
	 */
	public static void testPing() {
        Client client = ClientBuilder.newClient();

    	MultivaluedMap<String, Object> headerMap = new MultivaluedHashMap<String, Object> ();
    	headerMap.put("login.username", Arrays.asList(new Object [] { "admin" }));
       	headerMap.put("login.password", Arrays.asList(new Object [] { "ofbiz" }));
 
        Response response = client.target("http://localhost:8080/restcomponent/ping/ola").request("text/plain")
        		.headers(headerMap).get();

        if (response.getStatus()==200) {
        	System.out.println(response.readEntity(String.class));
        }
        else
        {
        	System.out.println("Error!");        	
        }
	}

	/**
	 * This method demonstrates how to use rest to get information about a product
	 * To get information about the product entity follow the url:
	 * https://localhost:8443/webtools/control/ArtifactInfo?name=Product&type=entity
	 * Note: see the "restcomponent" for details on how the rest service is implemented on the server
	 */
	public static void testGetProduct(String productId) {
        Client client = ClientBuilder.newClient();

    	MultivaluedMap<String, Object> headerMap = new MultivaluedHashMap<String, Object> ();
    	headerMap.put("login.username", Arrays.asList(new Object [] { "admin" }));
       	headerMap.put("login.password", Arrays.asList(new Object [] { "ofbiz" }));
 
        Response response = client.target("http://localhost:8080/restcomponent/product/"+productId).request("application/json")
        		.headers(headerMap).get();

        if (response.getStatus()==200) {
        	String stringObj=response.readEntity(String.class);
        	
        	JsonReader jsonReader = Json.createReader(new StringReader(stringObj));
        	
        	JsonObject jsonObj = jsonReader.readObject();

        	System.out.println("Get Product:");    
			System.out.print("Product ID="+jsonObj.getString("productId")+", Internal Name="+jsonObj.getString("internalName"));
			if (jsonObj.isNull("productName")) System.out.println(", Product Name= <>");
			else System.out.println(", Product Name="+jsonObj.getString("productName"));
        }
        else
        {
        	System.out.println("Error!");        	
        }
	}
	
	public static void testGetProducts() {
        Client client = ClientBuilder.newClient();

    	MultivaluedMap<String, Object> headerMap = new MultivaluedHashMap<String, Object> ();
    	headerMap.put("login.username", Arrays.asList(new Object [] { "admin" }));
       	headerMap.put("login.password", Arrays.asList(new Object [] { "ofbiz" }));
 
        Response response = client.target("http://localhost:8080/restcomponent/product").request("application/json")
        		.headers(headerMap).get();

        if (response.getStatus()==200) {
        	String stringObj=response.readEntity(String.class);
        	
        	JsonReader jsonReader = Json.createReader(new StringReader(stringObj));
        	
        	JsonArray jsonArray = jsonReader.readArray();
        	        	
        	System.out.println(">>> List of all Products:");
        	for (int i=0; i<jsonArray.size(); i=i+1) {
    			JsonObject jsonObj=jsonArray.getJsonObject(i);
    			
    			System.out.print("Product ID="+jsonObj.getString("productId")+", Internal Name="+jsonObj.getString("internalName"));
    			if (jsonObj.isNull("productName")) System.out.println(", Product Name= <>");
    			else System.out.println(", Product Name="+jsonObj.getString("productName"));
        	}
        	System.out.println(">>>");        	
        }
        else
        {
        	System.out.println("Error!");        	
        }
	}
	
	public static String testCreateProduct() {
        Client client = ClientBuilder.newClient();

    	MultivaluedMap<String, Object> headerMap = new MultivaluedHashMap<String, Object> ();
    	headerMap.put("login.username", Arrays.asList(new Object [] { "admin" }));
       	headerMap.put("login.password", Arrays.asList(new Object [] { "ofbiz" }));
       	
       	JsonObject value = Json.createObjectBuilder()
       	     .add("internalName", "internal teste1")
       	     .add("productName", "teste1")
       	     .add("productTypeId", "FINISHED_GOOD")
       	     .build();

        Response response = client.target("http://localhost:8080/restcomponent/product").request("application/json")
        		.headers(headerMap).post(Entity.text(value.toString()));

        if (response.getStatus()==200) {
        	String stringObj=response.readEntity(String.class);
        	
        	JsonReader jsonReader = Json.createReader(new StringReader(stringObj));
        	
        	JsonObject jsonObj = jsonReader.readObject();

        	System.out.println("Created Product:");    
			System.out.print("Product ID="+jsonObj.getString("productId")+", Internal Name="+jsonObj.getString("internalName"));
			if (jsonObj.isNull("productName")) System.out.println(", Product Name= <>");
			else System.out.println(", Product Name="+jsonObj.getString("productName"));
        	
        	
        	return jsonObj.getString("productId");
        }
        else
        {
        	System.out.println("Error!");  
        	return null;
        }
	}

	public static void testUpdateProduct(String productId, String newInternalName, String newProductName) {
        Client client = ClientBuilder.newClient();

    	MultivaluedMap<String, Object> headerMap = new MultivaluedHashMap<String, Object> ();
    	headerMap.put("login.username", Arrays.asList(new Object [] { "admin" }));
       	headerMap.put("login.password", Arrays.asList(new Object [] { "ofbiz" }));
       	
       	JsonObject value = Json.createObjectBuilder()
       	     .add("internalName", newInternalName)
       	     .add("productName", newProductName)
       	     .add("productTypeId", "FINISHED_GOOD")
       	     .build();

        Response response = client.target("http://localhost:8080/restcomponent/product/"+productId).request("application/json")
        		.headers(headerMap).put(Entity.text(value.toString()));

        if (response.getStatus()==200) {
        	String stringObj=response.readEntity(String.class);
        	
        	JsonReader jsonReader = Json.createReader(new StringReader(stringObj));
        	
        	JsonObject jsonObj = jsonReader.readObject();

        	System.out.println("Updated Product:");    
			System.out.print("Product ID="+jsonObj.getString("productId")+", Internal Name="+jsonObj.getString("internalName"));
			if (jsonObj.isNull("productName")) System.out.println(", Product Name= <>");
			else System.out.println(", Product Name="+jsonObj.getString("productName"));
        }
        else
        {
        	System.out.println("Error!");  
        }
	}

	public static void main(String[] args) {
		String productId=null;
		testPing();
		testGetProduct("GC-001-C100");
		testGetProducts();
		productId=testCreateProduct();
		if (productId!=null) {
			testGetProduct(productId);
			// Delete the previous product - unavailable operation
			testUpdateProduct(productId, "Update Internal Name Test", "Update Product Name Test");
		}
	}
}
