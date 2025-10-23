package com.srsoft.modapimanager.config;

public final class SecurityRoles {
    // Singoli ruoli
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_MANAGER = "ROLE_MANAGER";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_DEVELOP = "ROLE_DEVELOP";
    
    // Espressioni per autorizzazioni
    public static final String HAS_ROLE_ADMIN = "hasRole('" + ROLE_ADMIN + "')";
    public static final String HAS_ROLE_MANAGER = "hasRole('" + ROLE_MANAGER + "')";
    public static final String HAS_ROLE_USER = "hasRole('" + ROLE_USER + "')";
    public static final String HAS_ROLE_DEVELOP = "hasRole('" + ROLE_DEVELOP + "')";
    
       
    public static final String HAS_DEV_OR_ADMIN = 
            "hasAnyRole('" + ROLE_ADMIN + "','" + ROLE_DEVELOP + "')";
    
       public static final String HAS_DEV_OR_MANAGER = 
            "hasAnyRole('" + ROLE_DEVELOP + "','" + ROLE_MANAGER + "')";
    
    public static final String HAS_ANY_ADMIN = 
            "hasAnyRole('" + ROLE_ADMIN + "','" + ROLE_MANAGER + "','" + ROLE_DEVELOP + "')";
    
        
    public static final String HAS_ANY_STANDARD_USER = 
            "hasAnyRole('" + ROLE_ADMIN + "','" + ROLE_MANAGER + "','" + ROLE_USER + "','" + ROLE_DEVELOP + "')";
    
    private SecurityRoles() {
        // Impedisce l'istanziazione
    }
}