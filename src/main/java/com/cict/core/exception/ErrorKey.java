package com.cict.core.exception;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static javax.servlet.http.HttpServletResponse.*;

/**
 * Holds default and constant exception messages.
 */
public enum ErrorKey {
    /**
     * Generic Access Error Messages
     */
    VALIDATION_ERROR("validation.rest.error", SC_BAD_REQUEST),
    VALIDATION_INPUT_ERROR("validation.input.error", SC_BAD_REQUEST),

    /**
     * Generic REST Error Messages
     */
    REST_APPLICATION_ERROR("api.service.error", SC_BAD_REQUEST),
    REST_STATUS_ERROR("api.rest.invalidStatus", SC_BAD_REQUEST),
    INTERNAL_SERVER_ERROR("internal.server.error", SC_INTERNAL_SERVER_ERROR),
    ACCESS_DENIED_ERROR("access.denied.error", SC_FORBIDDEN),
    INVALID_CREDENTIAL_ERROR("api.rest.invalidCredentials", SC_FORBIDDEN),
    INVALID_TOKEN_ERROR("api.rest.invalidToken", SC_FORBIDDEN),
    INVALID_SBU_ACCESS("api.rest.sbuAccess", SC_FORBIDDEN),


    /**
     * Generic Data Error / Data Access Error Messages
     */
    CLASS_NOT_SUPPORTED("dev.class.notSupported", SC_BAD_REQUEST),
    DEFAULT_FIELD_ERROR("validation.generic.field.error", SC_BAD_REQUEST),

    RECORD_NOT_FOUND("dev.record.notFound", SC_BAD_REQUEST),
    RECORD_PAYLOAD_NOT_MATCH("dev.record.payload.notMatch", SC_BAD_REQUEST),

    DATA_UPDATE_NOT_ALLOWED("api.data.update.not.allowed", SC_BAD_REQUEST),

    USER_NOT_FOUND("api.user.notFound", SC_BAD_REQUEST),

    USER_USERNAME_BLANK("api.user.username.blank", SC_BAD_REQUEST),
    USER_USERNAME_TAKEN("api.user.username.exists", SC_BAD_REQUEST),
    USER_USERNAME_SHORT("api.user.username.short", SC_BAD_REQUEST),
    USER_FIRSTNAME_BLANK("api.user.firstName.blank", SC_BAD_REQUEST),
    USER_LASTNAME_BLANK("api.user.lastName.blank", SC_BAD_REQUEST),

    USER_EMAIL_BLANK("api.user.email.blank", SC_BAD_REQUEST),
    USER_EMAIL_TAKEN("api.user.email.exists", SC_BAD_REQUEST),

    USER_PASSWORD_SHORT("api.user.password.short", SC_BAD_REQUEST),
    USER_PASSWORD_BLANK("api.user.password.blank", SC_BAD_REQUEST),
    USER_OLD_PASSWORD_NOT_MATCH("api.user.old.password.notMatch", SC_BAD_REQUEST),

    USER_INVALID("api.user.invalid", SC_UNAUTHORIZED),
    USER_INVALID_CREDENTIAL("api.user.invalid.credential", SC_BAD_REQUEST),
    USER_DISABLED("api.user.account.disabled", SC_BAD_REQUEST),

    USER_GROUP_BLANK("api.user.group.blank", SC_BAD_REQUEST),
    USER_GROUP_USED("api.user.group.used", SC_BAD_REQUEST),
    USER_DEPARTMENT_BLANK("api.user.department.blank", SC_BAD_REQUEST),
    USER_POSITION_BLANK("api.user.position.blank", SC_BAD_REQUEST),
    USER_DEACTIVATED("api.user.exists.deactivated", SC_BAD_REQUEST),

    SBU_BLANK("api.sbu.blank", SC_BAD_REQUEST),
    SBU_NOT_FOUND("api.sbu.notFound", SC_BAD_REQUEST),

    DOCUMENT_CODE_EXISTS("document.code.exists", SC_CONFLICT),
    DOCUMENT_MULTI_EXISTS("document.multi.exists", SC_CONFLICT),
    DOCUMENT_CODE_IN_USED("document.code.inUsed", SC_CONFLICT),

    EVENT_CODE_NOT_EXISTS( "event.code.notExists", SC_CONFLICT),
    EVENT_CODE_MISSING( "event.code.missing", SC_CONFLICT),
    EVENT_CODE_MUST_BE_NUMBER( "event.code.number", SC_CONFLICT),

    /*DELETE RESTRICT*/
    DELETE_RESTRICT( "delete.restrict", SC_CONFLICT),
    PONO_ALREADY_EXISTS("api.pono.exists", SC_CONFLICT),
    CARTONS_ALREADY_LOADED( "cartons.already_loaded", SC_CONFLICT),
    NO_LOADED_CARTONS( "cartons.no_loaded", SC_CONFLICT),
    ALREADY_DISPATCHED( "already.dispatched", SC_CONFLICT),
    ;



    /**
     * Holds the message key.
     */
    private final String messageKey;
    ResourceBundle defaultBundle = ResourceBundle.getBundle("messages");
    /**
     * The status code associated with the error.
     */
    private int statusCode;

    /**
     * The constructor for this enum, sets the value of message key and the HTTP status code.
     */
    ErrorKey(String messageKey, int statusCode) {
        this.messageKey = messageKey;
        this.statusCode = statusCode;
    }

    /**
     * Returns the error key for the message key reference.
     */
    public static ErrorKey fromMessageKey(String messageKey) {
        for (ErrorKey errorKey : values()) {
            if (errorKey.messageKey.equals(messageKey)) {
                return errorKey;
            }
        }

        return null;
    }

    /**
     * Checks if the passed name matches with one of the listed enum values.
     */
    public boolean equalsName(String otherName) {
        return otherName != null && messageKey.equals(otherName);
    }

    /**
     * Returns the string value of the enum.
     */
    @Override
    public String toString() {
        return messageKey;
    }

    /**
     * Returns the message key.
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * Returns the status code for the error.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the default message for the error.
     */
    public String getMessage() {
        try {
            return defaultBundle.getString(messageKey);
        } catch (MissingResourceException e) {
            return "A REST error occurred";
        }
    }
}