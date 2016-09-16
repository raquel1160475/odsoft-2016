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
package restcomponent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.ofbiz.base.conversion.ConversionException;
import org.apache.ofbiz.base.lang.JSON;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.entity.GenericDelegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;

import org.apache.ofbiz.entity.util.Converters.GenericValueToJSON;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;

import javolution.util.FastMap;

/**
 * 
 * Nota: A criacao de um produto e' algo que e' um pouco complexo pelo que e'
 * executada por um servico Na pagina de criacao de produto quando se confirma a
 * criacao e' feito um POST .../catalog/control/CreateProduct Esse POST esta'
 * mapeado para o servico de criacao de produtos que e' um script simples:
 * component://product/script/org/ofbiz/product/product/ProductServices.xml
 * 
 * @author alexandrebraganca
 *
 */

@Path("/product")
public class ProductResource {

	@Context
	HttpServletRequest httpRequest;

	// ../url_resource
	// handling the collection
	//
	// GET return all
	// POST create new entry, returns 201
	// PUT not allowed
	// DELETE not allowed
	//

	/**
	 * This method returns a collection with all the products...
	 * 
	 * @return
	 */
	@GET
	@Produces("application/json")
	public Response getAllProducts() {
		String username = null;
		String password = null;

		try {
			username = httpRequest.getHeader("login.username");
			password = httpRequest.getHeader("login.password");
		} catch (NullPointerException e) {
			return Response.serverError().entity("Problem reading http header(s): login.username or login.password")
					.build();
		}

		if (username == null || password == null) {
			return Response.serverError().entity("Problem reading http header(s): login.username or login.password")
					.build();
		}

		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		List<GenericValue> products = null;

		try {
			products = delegator.findAll("Product", false);
		} catch (GenericEntityException e) {
			return Response.serverError().entity(e.toString()).build();
		}

		if (products != null) {

			String response = Util.convertListGenericValueToJSON(products);

			if (response == null) {
				return Response.serverError().entity("Erro na conversao do JSON!").build();
			}

			return Response.ok(response).type("application/json").build();
		}

		// shouldn't ever get here ... should we?
		throw new RuntimeException("Invalid ");
	}

	/**
	 * This method creates a new product in the collection
	 * 
	 * @return
	 */
	@POST
	@Produces("application/json")
	public Response createProduct() {
		String username = null;
		String password = null;

		try {
			username = httpRequest.getHeader("login.username");
			password = httpRequest.getHeader("login.password");
		} catch (NullPointerException e) {
			return Response.serverError().entity("Problem reading http header(s): login.username or login.password")
					.build();
		}

		if (username == null || password == null) {
			return Response.serverError().entity("Problem reading http header(s): login.username or login.password")
					.build();
		}

		JsonReader jsonReader;
		try {
			jsonReader = Json.createReader(httpRequest.getReader());
		} catch (IOException e) {
			return Response.serverError().entity("Problem reading json body").build();
		}

		JsonObject jsonObj = jsonReader.readObject();

		// Lets now invoke the ofbiz service that creates a product
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		LocalDispatcher dispatcher = org.apache.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);

		Map<String, String> paramMap = UtilMisc.toMap("internalName", jsonObj.getString("internalName"), "productName",
				jsonObj.getString("productName"), "productTypeId", jsonObj.getString("productTypeId"), "login.username",
				username, "login.password", password);

		Map<String, Object> result = FastMap.newInstance();
		try {
			result = dispatcher.runSync("createProduct", paramMap);
		} catch (GenericServiceException e1) {
			Debug.logError(e1, PingResource.class.getName());
			return Response.serverError().entity(e1.toString()).build();
		}

		String productId = result.get("productId").toString(); 
		String product = Util.getProduct(productId);
		if (product != null) {
			return Response.ok(product).type("application/json").build();
		} else {
			return Response.serverError().entity("Problem reading the new product after created!").build();
		}
	}

	// ../url_resource/{id}
	// handling individual itens in the collection
	//
	// item id must be present in the request object
	//
	// GET return specific item or 404
	// POST update existing entry or 404
	// PUT overwrite existing or create new given the id.
	// DELETE deletes the item
	//

	/**
	 * This method returns a product given its id.
	 * 
	 * @param productId
	 * @return
	 */
	@GET
	@Produces("application/json")
	@Path("{id}")
	public Response getProductById(@PathParam("id") String productId) {
		// id example="GC-001-C100"

		String username = null;
		String password = null;

		try {
			username = httpRequest.getHeader("login.username");
			password = httpRequest.getHeader("login.password");
		} catch (NullPointerException e) {
			return Response.serverError().entity("Problem reading http header(s): login.username or login.password")
					.build();
		}

		if (username == null || password == null) {
			return Response.serverError().entity("Problem reading http header(s): login.username or login.password")
					.build();
		}

		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		GenericValue product = null;

		try {
			product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
		} catch (GenericEntityException e) {
			return Response.serverError().entity(e.toString()).build();
		}

		if (product != null) {

			JsonObject object = null;

			JSON json = null;

			try {
				json = new GenericValueToJSON().convert(product);
			} catch (ConversionException e) {
				return Response.serverError().entity("Problem converting the product to json!").build();
			}

			JsonReader jsonReader = Json.createReader(new StringReader(json.toString()));
			object = jsonReader.readObject();
			jsonReader.close();

			return Response.ok(object.toString()).type("application/json").build();
		}

		// shouldn't ever get here ... should we?
		throw new RuntimeException("Invalid ");
	}

	/**
	 * This method returns a product given its id.
	 * 
	 * @param productId
	 * @return
	 */
	@DELETE
	@Produces("application/json")
	@Path("{id}")
	public Response deleteProductById(@PathParam("id") String productId) {

		// To delete a product there is no service in ofbiz...
		// it is necessary to execute the following SQL commands:
		// delete from `PRODUCT_KEYWORD` where `PRODUCT_ID`='SP-1000';
		// delete from `PRODUCT_CALCULATED_INFO` where `PRODUCT_ID`='SP-1000';
		// delete from `PRODUCT_PRICE` where `PRODUCT_ID`='SP-1000';
		// delete from `INVENTORY_ITEM_DETAIL` where `INVENTORY_ITEM_ID` IN
		// (SELECT
		// `INVENTORY_ITEM_ID` FROM `INVENTORY_ITEM` where
		// `PRODUCT_ID`='SP-1000');
		// delete from `INVENTORY_ITEM_VARIANCE` where `INVENTORY_ITEM_ID` IN
		// (SELECT
		// `INVENTORY_ITEM_ID` FROM `INVENTORY_ITEM` where
		// `PRODUCT_ID`='SP-1000');
		// delete from `SHIPMENT_RECEIPT` where `INVENTORY_ITEM_ID` IN (SELECT
		// `INVENTORY_ITEM_ID` FROM `INVENTORY_ITEM` where
		// `PRODUCT_ID`='SP-1000');
		// delete from `INVENTORY_ITEM` where `PRODUCT_ID`='SP-1000';
		// delete from `PRODUCT_CATEGORY_MEMBER` where `PRODUCT_ID`='SP-1000';
		// delete from `PRODUCT` where `PRODUCT_ID`='SP-1000';

		return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
	}

	// Put para actualizar...
	// https://localhost:8443/webtools/control/ServiceList?sel_service_name=updateProduct
	/**
	 * This method updates a product (given its id).
	 * 
	 * @param productId
	 * @return
	 */
	@PUT
	@Produces("application/json")
	@Path("{id}")
	public Response updateProductById(@PathParam("id") String productId) {
		String username = null;
		String password = null;

		try {
			username = httpRequest.getHeader("login.username");
			password = httpRequest.getHeader("login.password");
		} catch (NullPointerException e) {
			return Response.serverError().entity("Problem reading http header(s): login.username or login.password")
					.build();
		}

		if (username == null || password == null) {
			return Response.serverError().entity("Problem reading http header(s): login.username or login.password")
					.build();
		}

		JsonReader jsonReader;
		try {
			jsonReader = Json.createReader(httpRequest.getReader());
		} catch (IOException e) {
			return Response.serverError().entity("Problem reading json body").build();
		}

		JsonObject jsonObj = jsonReader.readObject();

		// Lets now invoke the ofbiz service that updates a product
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		LocalDispatcher dispatcher = org.apache.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);

		Map<String, String> paramMap = UtilMisc.toMap("productId", productId, "internalName",
				jsonObj.getString("internalName"), "productName", jsonObj.getString("productName"), "productTypeId",
				jsonObj.getString("productTypeId"), "login.username", username, "login.password", password);
				// "brandName", jsonObj.getString("brandName"));

		Map<String, Object> result = FastMap.newInstance();
		try {
			result = dispatcher.runSync("updateProduct", paramMap);
		} catch (GenericServiceException e1) {
			Debug.logError(e1, PingResource.class.getName());
			return Response.serverError().entity(e1.toString()).build();
		}

		if (result.get("responseMessage").toString().compareTo("success") == 0) {
			String product = Util.getProduct(productId);

			if (product != null) {

				return Response.ok(product).type("application/json").build();
			} else {
				return Response.serverError().entity("Problem reading the new product after updated!").build();
			}
		} else {
			return Response.serverError().entity(result.get("responseMessage").toString()).build();
		}
	}
}
