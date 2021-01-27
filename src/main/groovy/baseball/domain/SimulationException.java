package baseball.domain;

import mjs.common.exceptions.CoreException;

public class SimulationException extends CoreException {

    static final long serialVersionUID = -4174504602386548113L;

    /**
     * Constructor.
     *
     * @param s
     */
    public SimulationException(String s) {
        super(s);
    }

    /**
     * Constructor.
     *
     * @param s
     * @param e
     */
    public SimulationException(String s, Exception e) {
        super(s, e);
    }

}
