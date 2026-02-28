package net.ulich.crm.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

public enum LeadStatus implements EnumClass<String> {
    NEW("NEW"),
    SUCCESS("SUCCESS"),
    CANCELED("CANCELED");

    private final String id;

    LeadStatus(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public static LeadStatus fromId(String id) {
        for (LeadStatus value : values()) {
            if (value.id.equals(id)) {
                return value;
            }
        }
        return null;
    }
}
