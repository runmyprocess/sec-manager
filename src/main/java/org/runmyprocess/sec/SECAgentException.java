package org.runmyprocess.sec;

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
public class SECAgentException extends RuntimeException {
	private static final long serialVersionUID = 5592298800830452296L;
	private boolean _socketCreationError = false;

	/**
	 * Constructor that sets the message of an exception in the Adapter
	 * @param message
	 */
	public SECAgentException(String message) {
		super(message);
	}
	/**
	 * Constructor that sets the message and cause of an exception in the Adapter
	 * @param message
	 * @param cause
	 */
	public SECAgentException(String message, Throwable cause) {
		super( message, cause );
	}
	/**
	 * Constructor that sets the message and cause and socketCreationError of an exception in the Adapter
	 * @param message
	 * @param cause
	 * @param socketCreationError
	 */
	public SECAgentException(String message, Throwable cause, boolean socketCreationError) {
		super( message, cause );
		this.setSocketCreationError( socketCreationError );
	}

	/**
	 * Set setSocketCreationError
	 * @param socketCreationError
	 */
	private void setSocketCreationError(boolean socketCreationError) {
		_socketCreationError = socketCreationError;
	}
	/**
	 * get the setSocketCreationError
	 * @return socketCreationError
	 */
	public boolean hasSocketCreationError() {
		return _socketCreationError;
	}
}
