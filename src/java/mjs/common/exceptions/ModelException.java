package mjs.common.exceptions;

public class ModelException extends CoreException {

    static final long serialVersionUID = -4174504602386548113L;

    /**
     * Constructor.
     *
     * @param s
     */
    public ModelException(String s) {
        super(s);
    }

    /**
     * Constructor.
     *
     * @param s
     * @param e
     */
    public ModelException(String s, Exception e) {
        super(s, e);
    }

}
