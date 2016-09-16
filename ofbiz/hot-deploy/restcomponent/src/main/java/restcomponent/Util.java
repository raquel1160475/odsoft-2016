package restcomponent;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.Response;

import org.apache.ofbiz.base.conversion.ConversionException;
import org.apache.ofbiz.base.lang.JSON;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.entity.GenericDelegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.Converters.GenericValueToJSON;


public class Util {
	
	synchronized static String convertListGenericValueToJSON(List<GenericValue> listIn) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for(GenericValue value: listIn) {
			JSON json=null;
			try {
				json = new GenericValueToJSON().convert(value);
			} catch (ConversionException e) {
				return null;
			}
			
			
			JsonReader jsonReader = Json.createReader(new StringReader(json.toString()));
			JsonObject object = jsonReader.readObject();
			jsonReader.close();
			 
			builder.add(object);
		}
		
		JsonArray arr = builder.build();
		return arr.toString();
	}	

	synchronized static String getProduct(String productId) {
    	GenericValue product = null;
    	JsonObject object=null;

    	GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");

    	try {
    	       product = delegator.findOne("Product",
    	                          UtilMisc.toMap("productId", productId), false);
    	       
   			JSON json=null;

   			json = new GenericValueToJSON().convert(product);
   			
   			JsonReader jsonReader = Json.createReader(new StringReader(json.toString()));
   			object = jsonReader.readObject();
   			jsonReader.close();
    	}
    	catch (GenericEntityException e) {
			return null;
    	}
    	catch (ConversionException e) {
   				return null;
   		}
    	
    	return object.toString();
	}
}

