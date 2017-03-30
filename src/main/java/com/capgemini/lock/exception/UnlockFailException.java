package com.capgemini.lock.exception;

/**
 * JIRA-ID
 * DESCRIPTION
 * <p>
 * Created by jzbhhx on 25/08/16.
 */
public class UnlockFailException extends Exception{

	private static final long serialVersionUID = 1L;

	public UnlockFailException(String message) {
        super(message);
    }

    public UnlockFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
