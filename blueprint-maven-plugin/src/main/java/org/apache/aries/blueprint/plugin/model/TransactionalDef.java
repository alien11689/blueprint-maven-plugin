package org.apache.aries.blueprint.plugin.model;


public class TransactionalDef {
    public static final String TYPE_REQUIRED = "Required";
    public static final String TYPE_REQUIRES_NEW = "RequiresNew";
    private String method;
    private String type;
    
    public TransactionalDef(String method, String type) {
        this.method = method;
        this.type = type;
    }

    public String getMethod() {
        return method;
    }
    
    public String getType() {
        return type;
    }
}
