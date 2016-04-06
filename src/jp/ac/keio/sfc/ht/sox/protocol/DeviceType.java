/*
 * Copyright (C) 2014 Takuro Yonezawa
 * Keio University, Japan
 */

package jp.ac.keio.sfc.ht.sox.protocol;



public enum DeviceType {

    INDOOR_WEATHER("indoor weather"),
    OUTDOOR_WEATHER("outdoor weather"),
    OUTDOOR_ATMOSPHERE("outdoor atmosphere"),
    HVAC("hvac"),
    OCCUPANCY("occupancy"),
    MULTIMEDIA_INPUT("multimedia input"),
    MULTIMEDIA_OUTPUT("multimedia output"),
    SCALE("scale"),
    VEHICLE("vehicle"),
    RESOURCE_CONSUMPTION("resource consumption"),
    RESOURCE_GENERATION("resource generation"),
    PARTICIPATORY("participatory"),
    OTHER("other");
    
    private final String value;

    private DeviceType(final String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DeviceType fromValue(String v) {
        for (DeviceType c: DeviceType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
            if(c.name().equals(v)){
            	return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
