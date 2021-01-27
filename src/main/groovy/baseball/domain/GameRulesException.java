package baseball.domain;

import mjs.common.exceptions.CoreException;

public class GameRulesException extends CoreException {

    static final long serialVersionUID = -4174504602386548113L;

    /**
     * Constructor.
     *
     * @param s
     */
    public GameRulesException(String s) {
        super(s);
    }

    /**
     * Constructor.
     *
     * @param s
     * @param e
     */
    public GameRulesException(String s, Exception e) {
        super(s, e);
    }

}
