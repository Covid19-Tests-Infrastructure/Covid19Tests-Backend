package de.drkhannover.tests.api.conf;

public class ControllerPath {
    public static final String GLOBAL_PREFIX = "/api";
    
    public static final String SWAGGER = "swagger-ui.html";
    
    public static final String FORMULAR_PREFIX = GLOBAL_PREFIX + "/form";
    public static final String FORMULAR_PRIVATE = FORMULAR_PREFIX + "/private";
    public static final String FORMULAR_KVN = FORMULAR_PREFIX + "/kvn";
    public static final String FORMULAR_DEFAULT = FORMULAR_PREFIX + "/default";
    
    public static final String PRICE_GET = FORMULAR_PREFIX + "/price";
    
    public static final String USER_PREFIX = GLOBAL_PREFIX + "/user";
    public static final String USER_ADD = USER_PREFIX + "/add";
    public static final String USER_EDIT = USER_PREFIX + "/edit";
    
    public static final String USERS_PREFIX = GLOBAL_PREFIX + "/users";
    
    public static final String AUTHENTICATION_AUTH = GLOBAL_PREFIX + "/authenticate";
    public static final String AUTHENTICATION_CHECK = AUTHENTICATION_AUTH + "/check";
}
