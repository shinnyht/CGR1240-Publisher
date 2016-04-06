/*
 * Copyright (C) 2014 Takuro Yonezawa
 * Keio University, Japan
 */


package jp.ac.keio.sfc.ht.sox.protocol;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
public class TransducerValue {

	@Attribute  (required=true)
    private String id;
	
	@Attribute  (required=false)
    private String typedValue;
	
	@Attribute (required=false)
    private String timestamp;
	
	@Attribute  (required=false)
    private String rawValue;
	
		
	
    public String getId() {
        return id;
    }

    
    public void setId(String value) {
        this.id = value;
    }

  
    public String getTypedValue() {
        return typedValue;
    }

    
    public void setTypedValue(String value) {
        this.typedValue = value;
    }

   
    public String getTimestamp() {
        return timestamp;
    }

    
    public void setTimestamp(String value) {
        this.timestamp = value;
    }

    
    public String getRawValue() {
        return rawValue;
    }

    
    public void setRawValue(String value) {
        this.rawValue = value;
    }

    public void setCurrentTimestamp(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df.setTimeZone(TimeZone.getDefault());
        int offset = df.getTimeZone().getRawOffset()/3600000;
        if(offset>=10){
            this.timestamp = df.format(new Date())+"+"+offset+":00";
        }else if(offset>=0){
            this.timestamp = df.format(new Date())+"+0"+offset+":00";
        }else if(offset>-10){
            this.timestamp = df.format(new Date())+"-0"+(-offset)+":00";
        }else{
            this.timestamp = df.format(new Date())+"-"+(-offset)+":00";
        }
    }
	
}
