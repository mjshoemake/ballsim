package mjs.common.exceptions;

/**
 * An InvalidSessionException is a subclass of 
 * FemtoException.
 */
public class ValidationException extends mjs.common.exceptions.CoreException {
    
    static final long serialVersionUID = -4174504602386548113L;

    /**
     * Constructor.
     * 
     * @param s
     */
    public ValidationException(String s) {
        super(s);
    }

    /**
     * Constructor.
     * 
     * @param s
     * @param e
     */
    public ValidationException(String s, java.lang.Exception e) {
        super(s, e);
    }

}
