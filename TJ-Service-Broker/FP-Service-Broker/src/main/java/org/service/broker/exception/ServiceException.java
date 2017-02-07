package org.service.broker.exception;

/**
 * 
 * The <code>DBException</code> class represents interface for binding storage
 * service created via broker with APP.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 *
 */
public class ServiceException extends Exception {

	/**
	 * Holds serial version UID
	 */
	private static final long serialVersionUID = 5534068858000400312L;

	/**
	 * Default constructor.
	 */
	public ServiceException() {
	}

	/**
	 * Constructor with exception message.
	 * 
	 * @param message
	 *            holds exception message.
	 */
	public ServiceException(String message) {
		super(message);
	}

	/**
	 * Constructor with exception reference.
	 * 
	 * @param cause
	 *            holds throwable reference.
	 */
	public ServiceException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor with exception message and reference.
	 * 
	 * @param message
	 *            holds exception message.
	 * @param cause
	 *            holds throwable reference.
	 */
	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

}
