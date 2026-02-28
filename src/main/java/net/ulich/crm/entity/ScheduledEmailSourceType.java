package net.ulich.crm.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

public enum ScheduledEmailSourceType implements EnumClass<String> {
    CAMPAIGN("CAMPAIGN"),
    PRODUCT_ADD_ON_REMINDER("PRODUCT_ADD_ON_REMINDER");

    private final String id;

    ScheduledEmailSourceType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public static ScheduledEmailSourceType fromId(String id) {
        for (ScheduledEmailSourceType value : values()) {
            if (value.id.equals(id)) {
                return value;
            }
        }
        return null;
    }
}
