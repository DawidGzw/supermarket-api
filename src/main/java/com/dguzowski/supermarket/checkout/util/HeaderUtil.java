package com.dguzowski.supermarket.checkout.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

/**
 * Utility class for HTTP headers creation.
 */
public final class HeaderUtil {

    private static final Logger log = LoggerFactory.getLogger(HeaderUtil.class);

    private static final String APPLICATION_NAME = "checkoutApiApp";
    private static final String ALERT_HEADER = "X-"+APPLICATION_NAME+"-alert";
    private static final String PARAM_HEADER = "X-"+APPLICATION_NAME+"-params";
    private static final String ERROR_HEADER = "X-"+APPLICATION_NAME+"-error";

    private HeaderUtil() {
    }

    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(ALERT_HEADER, message);
        headers.add(PARAM_HEADER, param);
        return headers;
    }

    public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
        return createAlert(APPLICATION_NAME + "." + entityName + ".created", param);
    }

    public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
        return createAlert(APPLICATION_NAME + "." + entityName + ".updated", param);
    }

    public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
        return createAlert(APPLICATION_NAME + "." + entityName + ".deleted", param);
    }

    public static HttpHeaders createFailureAlert(String entityName, String errorKey, String defaultMessage) {
        log.error("Entity processing failed, {}", defaultMessage);
        HttpHeaders headers = new HttpHeaders();
        headers.add(ERROR_HEADER, "error." + errorKey);
        headers.add(PARAM_HEADER, entityName);
        return headers;
    }

    public static String getParamHeaderName(){
        return PARAM_HEADER;
    }

    public static String getAlertHeaderName() {
        return ALERT_HEADER;
    }
}
