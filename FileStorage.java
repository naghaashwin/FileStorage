//$Id$
package com.fileStore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FileStorage {
	private String fileName ; 
	private JSONObject data;
	private  static Scanner in = new Scanner(System.in);
	public FileStorage(){
		this.fileName = System.getProperty("user.dir") + File.separator + "data.json";
		System.out.println("File created in : " + this.fileName);
		this.data = new JSONObject();
	}
	public FileStorage(String fileLocation) {
		this.fileName = fileLocation + File.separator + "data.json";
		System.out.println("File created in : " + this.fileName);
		this.data = new JSONObject();
	}
	
	public void updateFile(){ 
		
	try {
 
	BufferedWriter out = new BufferedWriter( 
	new FileWriter(fileName, false)); 
	out.write(data.toString()); 
	out.close(); 
	} 
	catch (IOException e) { 
	System.out.println("exception occoured" + e); 
	} 
	} 
	
	public boolean validateTime(long expiryTime) {
		if(expiryTime != 0 && expiryTime < System.currentTimeMillis())
			return false;
		
		return true;
	}

	
	
	public void create(){
		System.out.println("Enter the key");
		String key = in.next().trim();
		if(key.length() > 32) {
			System.out.println("Key must be under 32 characters");
			return;
		} else {
			System.out.println("Enter the JSON Object");
			String jsonString = in.next();
			JSONObject jsonObject = null;
			try{
			jsonObject = new JSONObject(jsonString);
			int expectedSize = 16 * 1024;
			int actualSize = jsonObject.toString().getBytes().length;
			if(actualSize> expectedSize) {
				System.out.println("Value JSONObject must be below 16 KB");
			}
			System.out.println("Do you want to specify Time to Live property for the key ? Y/N");
			String choice = in.next().trim();
			if(choice.equals("Y") || choice.equals("y")) {
				long timeToLive = in.nextLong()*1000 + System.currentTimeMillis();
				this.create(key,jsonObject,timeToLive);
			} else {
				this.create(key,jsonObject);
			}
			updateFile();
			
			}
			catch(JSONException e) {
				System.out.println("Enter valid json object");
			}
		}
		
		return;
	}
	public void read(){
		System.out.println("Enter the key");
		String key = in.next().trim();
		JSONObject value = read(key);
		if(value != null)
		System.out.println(value);
	}
	public void delete(){
		System.out.println("Enter the key");
		String key = in.next().trim();
		delete(key);
	
	}
	
	
	public void create(String key, JSONObject value) {
		create(key,value,0);
	}
	
	public void create(String key, JSONObject value, long timeToLive) {
		JSONArray valuePart = new JSONArray();
		valuePart.put(value);
		//time to live must be set in the data time after basic working of the crud 
		valuePart.put(timeToLive);
		try {
			this.data.put(key, valuePart);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public JSONObject read(String key) {
		JSONArray valuePart;
		JSONObject value = null;
		try {
			valuePart = (JSONArray) this.data.get(key);
			
			value =   (JSONObject) valuePart.get(0);
		boolean validated = validateTime((long)valuePart.get(1));
		if (!validated) {
			System.out.println("Key has been expired");
			return null;
		}
			
		} catch (JSONException e) {
			System.out.println("Entered key not found");
			
		}
		return value;
		
		
	}
	
	
	
	
	public void delete(String key) { 
		
			JSONObject value = read(key);
			if(value != null) {
				this.data.remove(key);
				updateFile();
				System.out.println("Successfully removed the key " + key);
			}
			
		
			
	}
	
	public static void displayChoice(){
		System.out.println("Choose what to you want to do ?");
		System.out.println("1. Create Value.");
		System.out.println("2. Read Value.");
		System.out.println("3. Delete Value.");
		System.out.println("4. Exit");
	}
	
	
	
	public static void main(String args[]) throws JSONException {
		Scanner in = new Scanner(System.in);
		FileStorage fileStorageHandler = null;
//		f.create("1", new JSONObject("{text : HELLO}"));
//		f.read("1");
//		f.delete("2");
//		f.delete("1");
//		f.read("1");
//		
		System.out.println("Do you want to specify file location ? Y/N");
		String choice = in.next();
		
		if(choice.equals("Y") || choice.equals("y")) {
			System.out.println("Enter file location to create the data.json");
			String fileLocationInput = in.next();
			fileStorageHandler = new FileStorage(fileLocationInput);
		} else {
			fileStorageHandler = new FileStorage();	
			
		}
		boolean isRunning = true;
		while(isRunning) {
			displayChoice();
			int option = in.nextInt();
			switch(option) {
			case 1: 
				fileStorageHandler.create();
				break;
			case 2:
				fileStorageHandler.read(); 
				break;
			case 3:
				fileStorageHandler.delete();
				break;
			case 4:isRunning = false; break;
			default : System.out.println("Invalid Choice");
		
			}
			
		}
		
		
		
		
		
		
		
		
	}
	
}
