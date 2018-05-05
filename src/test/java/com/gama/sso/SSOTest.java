package com.gama.sso;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SSOTest extends TestCase {
	
	/**
	 * Test SSO.validateSession method 
	 */	
	public void testValidateSession() throws Exception {
		
		SSO sso = new SSO();
		
		try {
			
			boolean isPass = sso.validateSession("619a4de59bf2b7a5753516a8e01030caaeeca5a2799d61aedd942450e2a70590", "gama"); //sid, username
			
			assertTrue(true);
			
			
		} catch (Exception e) {
			
			assertTrue(false);
		}
		

	}
	
	
	/**
	 * Test SSO.createSession method
	 */	
	public void testCreateSession() throws Exception {
		
		
		SSO sso = new SSO();
		
		try {
			
			String sid = sso.createSession("gama", "139.59.58.22"); //user, ip
			assertTrue(true);
			
		} catch (Exception e) {
			
			assertTrue(false);
		}
		
		
		
	}
	
	
	/**
	 * Test SSO.invalidateSession method
	 */	
	public void testInvalidateSession() throws Exception{
		
		SSO sso = new SSO();
		
		try {
			
			sso.invalidateSession("619a4de59bf2b7a5753516a8e01030caaeeca5a2799d61aedd942450e2a70590", "gama"); //sid, username
			assertTrue(true);
			
		} catch (Exception e) {
			
			assertTrue(false);
		}

	}

	
}