package org.runmyprocess.sec;

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
public class HandlerIdleManager extends Thread {

    private JSONObject protocol;

    /**
     * Constructor that sets the protocol
     * @param protocol
     */
    public HandlerIdleManager(JSONObject protocol) {
        this.protocol = protocol;
    }

    /**
     * Manages how the system will react when a protocol is not responding
     */
    public void run() {
       
        	try {
        		Logger.getLogger(HandlerIdleManager.class.getName()).log(Level.WARNING, null,
        				"Protocol "+protocol.getString("protocol")+" is not responding.");
			} catch (Exception ex) {
        		Logger.getLogger(HandlerIdleManager.class.getName()).log(Level.SEVERE, null, ex);
			}
     
    }
}