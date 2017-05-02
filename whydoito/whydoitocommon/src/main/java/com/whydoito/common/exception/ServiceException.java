package com.whydoito.common.exception;

import java.util.HashMap;
import java.util.Map;


public class ServiceException extends Exception {

    private static final long serialVersionUID = 7118344898584330772L;

    protected String          errorCode;

    protected Object          context;

    /**
     * {@inheritDoc}
     */
    public ServiceException() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @param message
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public ServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServiceException(Throwable cause, String errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public ServiceException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    /**
     * the context may contains sensitive data, so it is required to set and get
     * it specifically
     * 
     * @return
     */
    public Object getContext() {
        return this.context;
    }

    /**
     * the context may contains sensitive data, so it is required to set and get
     * it specifically
     * 
     * @param context
     *            the context when the exception raised. an example is to use a
     *            Map&lt;String, Object> or something else to hold the actual
     *            data caused exception raising
     * @return exception itself - convenient for throw clause
     */
    public ServiceException setContext(Object context) {
        this.context = context;
        return this;
    }

    /**
     * similar as setContext(Object context) except that the context in provided
     * in array which will be transformed as map by this method
     * 
     * @param mapInArray
     *            "k1","v1","k2","v2",...
     * @return
     */
    public ServiceException setMapContext(Object... mapInArray) {
        if (mapInArray == null)
            return this;

        Map<Object, Object> ctx = new HashMap<Object, Object>();

        for (int i = 0; i < mapInArray.length - mapInArray.length % 2; i += 2) {
            ctx.put(mapInArray[i], mapInArray[i + 1]);
        }

        if (mapInArray.length % 2 != 0) {
            ctx.put(mapInArray[mapInArray.length - 1], null);
        }

        return setContext(ctx);
    }

    /**
     * {@inheritDoc} If the error code was set, then it will be appended to the
     * message too
     */
    @Override
    public String getMessage() {
        return super.getMessage() + (this.errorCode == null ? "" : "; Error Code(" + this.errorCode + ")");
    }

    public String getMessageWithoutErroCode() {
        return super.getMessage();
    }

    /**
     * Retrieve the innermost cause of this exception, if any.
     * 
     * @return the innermost exception, or <code>null</code> if none
     */
    public Throwable getRootCause() {
        Throwable rootCause = null;
        Throwable cause = getCause();
        while (cause != null && cause != rootCause) {
            rootCause = cause;
            cause = cause.getCause();
        }
        return rootCause;
    }

    /**
     * Retrieve the most specific cause of this exception, that is, either the
     * innermost cause (root cause) or this exception itself.
     * <p>
     * Differs from {@link #getRootCause()} in that it falls back to the present
     * exception if there is no root cause.
     * 
     * @return the most specific cause (never <code>null</code>)
     */
    public Throwable getMostSpecificCause() {
        Throwable rootCause = getRootCause();
        return (rootCause != null ? rootCause : this);
    }

    // http://docs.oracle.com/javase/6/docs/api/java/util/Formatter.html
    public static String fmtMsg(String msg, Object... args) {
        if (msg == null)
            return msg;
        if (msg.trim().isEmpty())
            return msg;

        return String.format(msg, args);
    }
}
