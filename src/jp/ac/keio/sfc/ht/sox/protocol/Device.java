/*
 * Copyright (C) 2014 Takuro Yonezawa
 * Keio University, Japan
 */

package jp.ac.keio.sfc.ht.sox.protocol;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.*;

@Root
public class Device {


	@ElementList (inline=true,required=false)
    private List<Transducer> transducer;
	

	@ElementList (inline=true,required=false)
    private List<Property> property;
    
	@Attribute(required = false)
    private String name;
    
	@Attribute(required = false)
    private String id;
    
	@Attribute(required = false)
    private DeviceType type;
    
	@Attribute (required = false)
    private String timestamp;
    
	@Attribute (required = false)
    private String description;
    
	@Attribute (required = false)
    private String serialNumber;
	
    public List<Transducer> getTransducers() {
        if (transducer == null) {
            transducer = new ArrayList<Transducer>();
        }
        return this.transducer;
    }
    
    public void setTransducers(List<Transducer> transducer){
    	this.transducer = transducer;
    }
    
    
    public List<Property> getProperty() {
        if (property == null) {
            property = new ArrayList<Property>();
        }
        return this.property;
    }

   
    public String getName() {
        return name;
    }

    
    public void setName(String value) {
        this.name = value;
    }

    
    public String getId() {
        return id;
    }

   
    public void setId(String value) {
        this.id = value;
    }

    public DeviceType getDeviceType() {
        return type;
    }

    
    public void setDeviceType(DeviceType value) {
        this.type = value;
    }

    
    public String getTimestamp() {
        return timestamp;
    }

    
    public void setTimestamp(String value) {
        this.timestamp = value;
    }

    
    public String getDescription() {
        return description;
    }

    
    public void setDescription(String value) {
        this.description = value;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String value) {
        this.serialNumber = value;
    }
    
}
