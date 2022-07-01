package com.minca.ap.drugservice.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.restassured.response.Response;
import net.minidev.json.parser.JSONParser;

public class BaseTestvalidation {
	protected Logger log = LoggerFactory.getLogger(getClass());	
	Hashtable<String,String> data;
	public ExtentTest test;
	
	public BaseTestvalidation() {}
	
	public BaseTestvalidation(Hashtable<String,String> data) {
		this.data = data;
	}
	
	public BaseTestvalidation(Hashtable<String,String> data, ExtentTest test) {
		this.data = data;
		this.test = test;
	}	
	
	public HashMap<String, String> validateResponse(Response response, String verifyThese ){

		log.info("verify these " + verifyThese);
		String errMessage = "";
		String[] verify = verifyThese.split(",");
		log.info("fields to be verified " + Arrays.toString(verify));
		HashMap<String, String> hashmap = new HashMap<String, String>();
		String status = "Success";

		for(String toVerify : verify) {
			
			log.info("toVerify :" + toVerify);
			try {
				if(toVerify.startsWith("count") && !toVerify.startsWith("country=")) { //count.cashcard.cashCardNo-3
					log.info("Here in count");
					int first = toVerify.indexOf("."); //first = 4
					int second = toVerify.indexOf("="); //second = 26
									
					String key = toVerify.substring(first+1, second-1); // key = cashcard.cashCardNo
					int count = Integer.parseInt(toVerify.substring(second + 1)); //expected count = 3
					List<String> actual =  response.jsonPath().getList(key);
					int size = actual.size(); 
					
					log.info("Expected value of " + count + " " + count + " and actual value : " + size);
					test.log(LogStatus.INFO,"Expected value of count" +  count + " and actual value : " + size );
					
					if(count != size) {
						errMessage += "Expected value of " + count + " " + count + " and actual value : " + size;
						status = "Fail";
					}
						
				}		
				else if(toVerify.startsWith("exists")) { //exists.cashcard.id
					int first = toVerify.indexOf("."); //first = 4		
					String key = toVerify.substring(first+1); // key = cashcard.id
					int size = 0;
					
					if(key.contains("id") || key.contains("cashCardNo")) {
						List<Integer> actual =  null;
						actual =  response.jsonPath().getList(key);
						size = actual.size(); 
						actual.forEach( k-> log.info(key + " " + k));
						actual.forEach( k-> test.log(LogStatus.INFO, key + " " + k));
						//test.log(LogStatus.INFO, actual.size() );
						
					}
					else {
						List<String> actual =  null;
						actual =  response.jsonPath().getList(key);
						size = actual.size(); 
						actual.forEach( k-> log.info(key + " " + k));
						actual.forEach( k-> test.log(LogStatus.INFO, key + " " + k));
					}
					
					
					if(size == 0) {
						errMessage += "Could not find the " + key +" in the response!!";
						status = "Fail";
					}
					
				}
				else {
					//e.g : consumers.email or phoneExtension
					//consumers.email=niveditaminca+306@gmail.com
					boolean flag = false;
					ObjectMapper objectMapper = new ObjectMapper();
					int indexVal = toVerify.indexOf("="); 
					String key = toVerify.substring(0, indexVal); //consumers.email
					String expected = toVerify.substring(indexVal +1); //niveditaminca+306@gmail.com
					String[] keyArray = key.split("[.]");
					System.out.println("keyArray length"+ keyArray.length);
					
					Object responseAsObject = response.as(Object.class);
					
					if(keyArray.length == 1) {
						flag = verifyValueInRespose(responseAsObject, keyArray[0], expected);
					}
					else if(keyArray.length == 2) {
						if(responseAsObject instanceof List) {
														 
							List<Map<String, Object>> myObjects = 
									objectMapper.readValue(response.asString() , new TypeReference<List<Map<String, Object>>>(){});
							System.out.println(myObjects.toString());
							
							for (Map<String, Object> map : myObjects) {
							    for (Map.Entry<String, Object> entry : map.entrySet()) {
							        
							    	if(entry.getKey().equalsIgnoreCase(keyArray[0])) {						        									    		
							    		flag = verifyValueInRespose(entry.getValue(), keyArray[1], expected);	
							    		if(flag) {
							    			break;
							    		}
							    	}
							    	
							    }
							    if(flag) {
									break;
								}
							}
							
						}else if(responseAsObject instanceof Map)
						{
							Map<String, Object> result = objectMapper.readValue(response.asString(), HashMap.class);
							if (result.containsKey(keyArray[0])) {
								 for (Map.Entry<String,Object> entry : result.entrySet())
								 {
									 if(entry.getKey().equalsIgnoreCase(keyArray[0])) {						        									    		
								    		flag = verifyValueInRespose(entry.getValue(), keyArray[1], expected);
								    		if(flag) {
								    			break;
								    		}
								    	}
								 }

								 } 
						}
					}		 
						
					else if(keyArray.length == 3) {
						Object firstLevel = returnNestedObject(responseAsObject, keyArray[0]);
						Object secondLevel = returnNestedObject(firstLevel, keyArray[1]);
						flag = verifyValueInRespose(secondLevel, keyArray[2], expected);	
					}
					
					else if(keyArray.length == 4) {
						Object firstLevel = returnNestedObject(responseAsObject, keyArray[0]);
						Object secondLevel = returnNestedObject(firstLevel, keyArray[1]);
						Object thirdLevel = returnNestedObject(secondLevel, keyArray[2]);
						flag = verifyValueInRespose(thirdLevel, keyArray[3], expected);	
					}
					
					
					if(!flag) {
						errMessage += "Could not find the " + key +" in the response!!";
						status = "Fail";
						break;
					}
				
					
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		hashmap.put("errMessage", errMessage);
		hashmap.put("status", status);
		return hashmap;
		
	}
	
	public boolean verifyValueInRespose(Object responseAsObject, String Path, String expected) {
		boolean isResultMatch = false;
		
		if(responseAsObject instanceof List) {
			 System.out.println("Instance of List"); 
			List responseAsList = (List)responseAsObject;
			JSONArray jsonArray = new JSONArray(responseAsList);
			 List resultValues = IntStream.range(0, jsonArray.length())
		      .mapToObj(index -> ((JSONObject)jsonArray.get(index)).optString(Path))
		      .collect(Collectors.toList());
			 System.out.println("Expected : "+ expected + " ** Actual list" + resultValues.toString());
			 isResultMatch = resultValues.stream().anyMatch(t -> t.toString().equalsIgnoreCase(expected));
			 
		}
		else if(responseAsObject instanceof Map) {
			 System.out.println("Instance of Map"); 
			 System.out.println("-----" + responseAsObject +"-----"+ Path +"-----"+ expected); 
			HashMap responseAsMap = (HashMap)responseAsObject;
			System.out.println("Expected : "+ expected + " ** Actual Map" + responseAsMap.entrySet().toString());
			isResultMatch = responseAsMap.entrySet().stream().anyMatch(e -> e.toString().equalsIgnoreCase(Path+"="+expected));
	
		}
		if(isResultMatch) {
			 System.out.println("Result Matched");
			 return true;
		 }
		else {
			return false;
		}

	}
	
	public Object returnNestedObject(Object responseAsObject, String Path) {
		
		Object resultValue = null;
		System.out.println("-------------");
		if(responseAsObject instanceof List) {
			 System.out.println("Instance of List - return Nested"); 
			List responseAsList = (List)responseAsObject;
			JSONArray jsonArray = new JSONArray(responseAsList);
			 List resultValues = IntStream.range(0, jsonArray.length())
		      .mapToObj(index -> ((JSONObject)jsonArray.get(index)).optJSONObject(Path))
		      .collect(Collectors.toList());
			 resultValue = resultValues;
		}
		else if(responseAsObject instanceof Map) {
			 System.out.println("Instance of Map - - return Nested"); 
			 
			HashMap<String, Object> responseAsMap = (HashMap<String, Object>)responseAsObject;
			for (HashMap.Entry mapElement : responseAsMap.entrySet()) {
	            String key = (String) mapElement.getKey();
	            if(key.equalsIgnoreCase(Path)) {
	            	resultValue = mapElement.getValue();
	            }
			}
			
		}
		System.out.println("returning nested object"+ resultValue); 
		return resultValue; 

	}
	
}
