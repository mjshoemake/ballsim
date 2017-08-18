package mjs.common.exceptions;

public class LoginException extends CoreException {

    static final long serialVersionUID = -4174504602386548113L;

    /**
     * Constructor.
     *
     * @param s
     */
    public LoginException(String s) {
        super(s);
    }

    /**
     * Constructor.
     *
     * @param s
     * @param e
     */
    public LoginException(String s, Exception e) {
        super(s, e);
    }

}
