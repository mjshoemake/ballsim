package mjs.model

import mjs.common.model.ModelLoggable

/**
 * This is the data object or suitcase for an HTTP response to the caller.
 */
class ServiceResponse extends ModelLoggable {

    /**
     * The response message.
     */
    String message = "Success"

    /**
     * The response code.
     */
    String code = 200

    /**
     * The status message to display to the user.
     */
    String statusMsgToUser = ""

    /**
     * Constructor.
     * @param msg
     * @param responseCode
     * @param statusMsgToUser
     */
    ServiceResponse(responseCode, String msg, statusMsgToUser) {
        this.message = msg
        this.code = responseCode
        this.statusMsgToUser = statusMsgToUser
    }
}
