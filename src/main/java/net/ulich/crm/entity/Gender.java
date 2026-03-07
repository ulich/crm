package net.ulich.crm.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

public enum Gender implements EnumClass<String> {
    MR("MR"),
    MRS("MRS"),
    UNKNOWN("UNKNOWN");

    private final String id;

    Gender(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public static Gender fromId(String id) {
        for (Gender value : values()) {
            if (value.id.equals(id)) {
                return value;
            }
        }
        return null;
    }
}
