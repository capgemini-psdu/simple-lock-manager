package com.capgemini.lock.exception;

/**
 * JIRA-ID
 * DESCRIPTION
 * <p>
 * Created by jzbhhx on 25/08/16.
 */
public class LockFailException extends Exception{

	private static final long serialVersionUID = 1L;

	public LockFailException(String message) {
        super(message);
    }

    public LockFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
