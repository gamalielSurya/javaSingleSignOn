package com.gama.sso;

import java.net.ConnectException;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gama.db.SchedulerDB;
import com.gama.sha.SHA;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

public class SSO {
	
	private Logger log = LogManager.getLogger(SSO.class.getName());
	
	/**
	 * Method to validate current session. 
	 * All cookies required by this method, should be acquired from the servlet on doGet(HttpServletRequest, HttpServletResponse) method
	 * @param sid : Session ID from SID cookie, user : Current User from USER cookie
	 * @return true, if current session is valid
	 * @throws Exception
	 */	
	public boolean validateSession(String sid, String user) throws Exception {
		
		boolean valid = false;
		
		Timestamp checkNow = new Timestamp(new Date().getTime()); //get current timestamp
		
		SchedulerDB sdb=SchedulerDB.getSchedulerDB();
		
		try{
			sdb.connectDB("sso");
			Map map = sdb.getSession(sid);
			
			if ((map!=null && map.size()>0)){
				
				valid = ( 	map.get("username").equals(user.toLowerCase()) && 
							map.get("status").equals("1") &&
							((Timestamp) map.get("expiryDatetime")).after(checkNow) //compare with current timestamp
							?
									true :
									false );
	        }
			
		} catch(SQLTimeoutException timeoutException) {
			log.error("Validate Session Stopped. Timeout exceed to access Database");
		} catch(CommunicationsException commException) {
			log.error("Validate Session Stopped. Communications link failure. No response from DB server.");
		} catch(ConnectException connException) {
			log.error("Validate Session Stopped. Connection refused.");
		} catch(SQLException sqlException) {
			log.error("Validate Session Stopped. Please check DB configuration on db.config");
		} finally {
			sdb.closeDB();
		}
		
		return valid;
	}
	
	
	/**
	 * Method to create a session
	 * @param username : Current User, mIP : Current IP from User's Machine
	 * @return new Session ID
	 * @throws Exception
	 */	
	public String createSession(String username, String mIP) throws Exception {
		
		String sid = createRID();

		int newId = writeSessionInfo(sid, username, mIP);
		
		return sid;
		
	}
	
	
	/**
	 * Method to invalidate a session
	 * @param sid : Session ID from SID cookie, user : Current User from USER cookie
	 * @throws Exception
	 */	
	public void invalidateSession(String sid, String user) throws Exception{
		
		SchedulerDB sdb=SchedulerDB.getSchedulerDB();
		
		try {
			sdb.connectDB("sso");
			
			sdb.invalidateSession(sid,user);
			
		} catch(SQLTimeoutException timeoutException) {
			log.error("Invalidate Session Stopped. Timeout exceed to access Database");
		} catch(CommunicationsException commException) {
			log.error("Invalidate Session Stopped. Communications link failure. No response from DB server.");
		} catch(ConnectException connException) {
			log.error("Invalidate Session Stopped. Connection refused.");
		} catch(SQLException sqlException) {
			log.error("Invalidate Session Stopped. Please check DB configuration on db.config");
		} finally {
			sdb.closeDB();
		}
	}
	
	
	/**
	 * Method to create unique SHA-256 hash  
	 * @return new unique session ID
	 * @throws Exception
	 */
	private String createRID() throws Exception {

		String getHash = null;
		
		SchedulerDB sdb=SchedulerDB.getSchedulerDB();
		
		try {
			sdb.connectDB("sso");
			
			SHA sha = new SHA();
			
			int counter = 1; // to protect codes from infinite loop
			
			Map checkDB = new HashMap();

			do {
				
				getHash = sha.getRandomSHA256Hash(128);
				
				checkDB = sdb.getSession(getHash);
				
				counter++;
				
			} while (!checkDB.isEmpty() && counter <= 10 ); // stop until get unique RID or counter > 10
			
			if (counter > 10) {
				throw new Exception("Process stopped. Failed to generate unique RID.");
			}
			

		} catch(SQLTimeoutException timeoutException) {
			log.error("Create RID Stopped. Timeout exceed to access Database");
		} catch(CommunicationsException commException) {
			log.error("Create RID Stopped. Communications link failure. No response from DB server.");
		} catch(ConnectException connException) {
			log.error("Create RID Stopped. Connection refused.");
		} catch(SQLException sqlException) {
			log.error("Create RID Stopped. Please check DB configuration on db.config");
		} finally {
			sdb.closeDB();
		}
		
		return getHash;
		
	}
	
	
	/**
	 * Method to write session info to the Database  
	 * @param sid : Session ID, user : Current User, mIP : Current IP from User's Machine
	 * @return id (auto increment) from Database
	 * @throws Exception
	 */
	private int writeSessionInfo(String sid, String username, String mIP) throws Exception {
		
		int id = -1;
		
		SchedulerDB sdb=SchedulerDB.getSchedulerDB();
		
		try {
			sdb.connectDB("sso");
			
			id = sdb.createSession(sid, username, mIP);
			
		} catch(SQLTimeoutException timeoutException) {
			log.error("Write Session Info to DB Stopped. Timeout exceed to access Database");
		} catch(CommunicationsException commException) {
			log.error("Write Session Info to DB Stopped. Communications link failure. No response from DB server.");
		} catch(ConnectException connException) {
			log.error("Write Session Info to DB Stopped. Connection refused.");
		} catch(SQLException sqlException) {
			log.error("Write Session Info to DB Stopped. Please check DB configuration on db.config");
			sqlException.printStackTrace();
		} finally {
			sdb.closeDB();
		}
		
		return id;
	}
	
}