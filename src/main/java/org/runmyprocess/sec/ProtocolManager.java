package org.runmyprocess.sec;

import org.runmyprocess.json.JSON;
import org.runmyprocess.json.JSONObject;
import org.runmyprocess.json.parser.StreamParser;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;

/**
 *
 * Servlet implementation class ProtocolManager
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
 * 
 */
@WebServlet("/ProtocolManager")
public class ProtocolManager extends HttpServlet {

    protected Logger _log = Logger.getLogger(ProtocolManager.class.getName());
    protected ServletConfig _config;
    protected ServletContext _context;
    private RegistrationHandler registrationHandler = new RegistrationHandler();
    private static final long serialVersionUID = 1L;

    /**
     * Constructor that sets the configuration information of the manager
     * and turns all the Listener for registering Adapters
     * @throws Exception 
     */
    public ProtocolManager() throws Exception {
    	Config config = new Config("."+File.separator+".."+File.separator+"configFiles"+File.separator+"manager.config",true);
    	try {
            new ListenerThread(registrationHandler, Integer.parseInt(config.getProperty("pingPort"))).start();//Listen to port for new connections
        } catch (Exception ex) {
			_log.severe(ex.getMessage());
        }
		try {
			new CheckRegistrationPing(registrationHandler, Integer.parseInt(config.getProperty("pingFrequency"))).start();
		} catch (Exception ex) {
			_log.severe(ex.getMessage());
		}
    }

    /**
     * Creates a new socket 
     * @param host
     * @param port
     * @return socket
     * @throws UnknownHostException
     * @throws IOException
     */
    protected Socket createSocket(String host, int port) throws UnknownHostException, IOException {
        Socket socket = new Socket(host, port);
        return socket;
    }

    /**
     * reads an input stream into a JSONObject
     * @param is
     * @return jsonResponse
     */
    private JSONObject readInputStream(InputStream is) {
        if (is == null) {
            return null;
        }
        JSONObject jsonResponse;
        try {
            JSON result = JSON.parse(new StreamParser(is, "UTF-8"));//gets the result input and parses it to JSON
            jsonResponse = result.toJSONObject(); //Converts JSON to JSONOBJECT
        } catch (Exception e) {
            throw new SECAgentException(e.getMessage());
        }
        return jsonResponse;
    }

    /**
     * Procecess the request. recieves a request object and tunnels it to the respective socket
     * it expects a reply and sends it back
     * @param request
     * @param host
     * @param port
     * @return response
     */
    private Response processRequest(Request request, String host, int port) {
        Response response = null;
        try {
            Socket socket = createSocket(host, port);//Creates the socket to place data
            new DataOutputStream(socket.getOutputStream()).write(request.getData().toString().getBytes("UTF-8"));
            JSONObject JReply = readInputStream(socket.getInputStream());
            response = new Response();
            if (JReply.containsKey("status"))
            	response.setStatus(JReply.getInteger("status"));//Sets the response status
            else
            	response.setStatus(200);//Sets the response status
            response.setData(JReply);

        } catch (Exception e) {
        	SECErrorManager exceptionError = new SECErrorManager();
        	exceptionError.setMessage(Http_SEC_Errors.MANAGER_ERROR.getMessage()+": "+e.getMessage());
        	exceptionError.logError(e.getMessage(), Level.SEVERE);
        	response = new Response();
        	response.setStatus(Http_SEC_Errors.MANAGER_ERROR.getId());
        	response.setData(exceptionError.getErrorObject());
            e.printStackTrace(System.err);
        }
        return response;
    }
    /**
     * Sets the reply status and sends the reply back to the server
     * @param response
     * @param status
     * @param message
     */
    private void replyMessage(HttpServletResponse response, int status, JSONObject message){
		try {
			response.setStatus(status);
			response.setCharacterEncoding("utf-8");
	        response.setContentType("application/json");
	        PrintWriter out;
	        out = response.getWriter();
	        out.print(message);
	        out.flush();
	        out.close();	
		} catch (IOException e) {
			_log.severe(e.getMessage());
		}		
    }
    
	/**
	 * Manages a GET request to display some test information.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter writer = response.getWriter();
		try {
			Config conf = new Config("."+File.separator+".."+File.separator+"configFiles"+File.separator+"manager.config",true);
			response.setContentType("text/html");
			writer.println("The SEC-ProtocolManager is running! ");

			JSONObject handlerList = registrationHandler.getProtocolList();
			
            Set<?> keys = handlerList.keySet();
            writer.println("Registered Protocols");
            for (Object key : keys) {
				JSONObject handler = handlerList.getJSONObject(key.toString());
				//if(!handler.getBoolean("broken")){
					writer.println(handler.getString("protocol"));
				//}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			writer.println("Error running SEC load Test! "+e);
			e.printStackTrace();
		}
		writer.close();
		//doPost(request, response);
	}

	/**
	 * Manages the POST request. 
	 * Gets the configuration information
	 * Sets data to be forwarded and forwards the message
	 * It finally receives the response which it sends back to the server 
	 */
    public void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        try {
            JSON result = JSON.parse(new StreamParser(req.getInputStream(), "UTF-8"));//gets the body and parses it to JSON
            JSONObject jsonRequest = result.toJSONObject(); //Converts JSON to JSONOBJECT
            String protocolName = jsonRequest.getString("protocol");
            JSONObject protocolHandler;
			protocolHandler = registrationHandler.getProtocol(protocolName);
            if (protocolHandler==null){
            	try {
            		SECErrorManager errorManager = new SECErrorManager();
            		JSONObject errorObject = errorManager.generateReponseObject(
            				Http_SEC_Errors.HANDLER_NOTFOUND.getMessage(),
            				Http_SEC_Errors.HANDLER_NOTFOUND.getId(),Level.SEVERE);
            		replyMessage(response,HttpServletResponse.SC_BAD_REQUEST,errorObject);	
            		
				} catch (Exception e) {
		            e.printStackTrace(System.err);
		            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	            	SECErrorManager errorManager = new SECErrorManager();
	            	errorManager.logError(e.getMessage(), Level.SEVERE);
				}
            }else if (protocolHandler.getBoolean("broken")) {
            	SECErrorManager errorManager = new SECErrorManager();
        		JSONObject errorObject = errorManager.generateReponseObject(
        				Http_SEC_Errors.HANDLER_IDLE.getMessage(),
        				Http_SEC_Errors.HANDLER_IDLE.getId(),Level.SEVERE);
        		replyMessage(response,HttpServletResponse.SC_BAD_REQUEST,errorObject);		
			} 

            Request request = new Request();//Creates a new instance of request class
            request.setProtocol(jsonRequest.getString("protocol"));//Sets the protocol's name
            request.setData(jsonRequest.getJSONObject("data"));//sets the data to be posted
            Response resp = processRequest(request, protocolHandler.getString("host"), protocolHandler.getInteger("port"));//Launches the request to a host/port
            if (resp.getStatus()==200){//Check if everything is OK
        		replyMessage(response,200,resp.getData());		
            }else{
            	try {
					replyMessage(response,HttpServletResponse.SC_BAD_REQUEST,resp.getObjectResponse());	
				} catch (Exception e) {
					e.printStackTrace(System.err);
	            	SECErrorManager errorManager = new SECErrorManager();
	            	errorManager.logError(e.getMessage(), Level.SEVERE);
	            	throw new Exception(e);
				}
            }   
        } catch (Exception ex) {
        	JSONObject errorObject = new JSONObject();
        	errorObject.put("error", ex.toString());
        	replyMessage(response,HttpServletResponse.SC_OK,errorObject);	
        }
    }

}
