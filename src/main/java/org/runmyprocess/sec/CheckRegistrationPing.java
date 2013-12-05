package org.runmyprocess.sec;

import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.runmyprocess.json.JSONObject;

/**
 *
 * @author Malcolm Haslam <mhaslam@runmyprocess.com>
 *
 * Copyright (C) 2013 Fujitsu RunMyProcess
 *
 * This file is part of RunMyProcess SEC.
 *
 * RunMyProcess SEC is free software: you can redistribute it and/or modify
 * it under the terms of the Apache License Version 2.0 (the "License");
 *
 *   You may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
public class CheckRegistrationPing extends Thread {

    private RegistrationHandler registrationHandler;
    private int chkFrequency;
  
    /**
     * Constructor that checks the registered Adapters pings
     * @param registrationHandler
     * @param chkFrequency
     */
    public CheckRegistrationPing(RegistrationHandler registrationHandler, int chkFrequency) {
        this.registrationHandler = registrationHandler;
        this.chkFrequency = chkFrequency;
    }
	/**
	 * Checks all adapters registered and checks if any of them has
	 * not been pinging the manager since the last check
	 */
    public void run() {

        while (true){
        	try {
				Thread.sleep(chkFrequency);
	        	JSONObject handlerList = registrationHandler.getProtocolList();
	
	            Set<?> keys = handlerList.keySet();
	            for (Object key : keys) {
					JSONObject handler = handlerList.getJSONObject(key.toString());
					if( handlerList.get(handler.getString("protocol")) instanceof JSONObject ){
						/*
						 * Checks if the time since last ping from the adapter is larger than the
						 * check frequency and if the adapter has not been already been set as broken
						 */
	        			if (((new Date().getTime()-handler.getLong("lastPing"))>chkFrequency) 
	        					&& !handler.getBoolean("broken")){
	        				handler.put("broken", true);// Sets the broken flag
	                		new HandlerIdleManager(handler).start();
	        			}
	                }
				}

			} catch (Exception ex) {
        		Logger.getLogger(CheckRegistrationPing.class.getName()).log(Level.SEVERE, null, ex);
			}
        }
    	    

    }
}