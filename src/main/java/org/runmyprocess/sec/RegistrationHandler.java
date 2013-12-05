package org.runmyprocess.sec;

import java.util.Date;

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
public class RegistrationHandler implements JSONSocketListener{
	
	private JSONObject handlers = new JSONObject();
	
	@Override
	/**
	 * Registers Adapters that have pinged the manager
     * @param jsonObject
	 */
	public void acceptJson(JSONObject jsonObject) {
		if (jsonObject.containsKey("protocol")){
			String protocol = jsonObject.getString("protocol");
			JSONObject handler = new JSONObject();
			handler.put("lastPing",new Date().getTime());
			handler.put("protocol",protocol);
			handler.put("host",jsonObject.getString("host"));
			handler.put("port",jsonObject.getInteger("port"));
			handler.put("broken", false);
			handlers.put(protocol, handler);
		}
	}
	/**
	 * Gets the registered protocol information
	 * @param protocol name of the protocol
	 * @return protocol
	 */
	public JSONObject getProtocol (String protocol){
		return handlers.getJSONObject(protocol);
	}
	/**
	 * Gets the complete list of protocols
	 * @return handlers
	 */
	public JSONObject getProtocolList (){
		return handlers;
	}

}
