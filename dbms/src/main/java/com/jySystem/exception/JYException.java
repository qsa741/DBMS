package com.jySystem.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class JYException extends Exception {

	private static final long serialVersionUID = 1L;

	public JYException(String msg) {
		super(msg);
	}
	
	public JYException(String msg, Throwable cause) {
		super(msg, cause);
		cause.printStackTrace();
	}
	
}
