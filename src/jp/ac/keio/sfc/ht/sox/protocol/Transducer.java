/*
 * Copyright (C) 2014 Takuro Yonezawa
 * Keio University, Japan
 */

package jp.ac.keio.sfc.ht.sox.protocol;

import org.simpleframework.xml.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


@Root
public class Transducer {

	@ElementList (inline=true,required=false) 
	private List<Property> property;

	@Attribute(required = true)
	private String name;
	
	@Attribute(required = true)
	private String id;
	
	@Attribute(required = false)
	private String units; //Using String instead of defining AllowedUnits
	
	@Attribute(required = false)
	private BigInteger unitScaler;
	
	@Attribute(required = false)
	private Boolean canActuate;
	
	@Attribute(required = false)
	private Boolean hasOwnNode;
	
	@Attribute(required = false)
	private String transducerTypeName;
	
	@Attribute(required = false)
	private String manufacturer;
	
	@Attribute(required = false)
	private String partNumber;
	
	@Attribute(required = false)
	private String serialNumber;
	
	@Attribute(required = false)
	private Float minValue;
	
	@Attribute(required = false)
	private Float maxValue;
	
	@Attribute(required = false)
	private Float resolution;
	
	@Attribute(required = false)
	private Float precision;
	
	@Attribute(required = false)
	private Float accuracy;
	
	
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

	public String getUnits() {
		return units;
	}


	public void setUnits(String value) {
		this.units = value;
	}

	public BigInteger getUnitScaler() {
		if (unitScaler == null) {
			return new BigInteger("0");
		} else {
			return unitScaler;
		}
	}

	
	public void setUnitScaler(BigInteger value) {
		this.unitScaler = value;
	}


	public boolean isCanActuate() {
		if (canActuate == null) {
			return false;
		} else {
			return canActuate;
		}
	}


	public void setCanActuate(Boolean value) {
		this.canActuate = value;
	}

	
	public boolean isHasOwnNode() {
		if (hasOwnNode == null) {
			return false;
		} else {
			return hasOwnNode;
		}
	}

	
	public void setHasOwnNode(Boolean value) {
		this.hasOwnNode = value;
	}

	
	public String getTransducerTypeName() {
		return transducerTypeName;
	}

	
	public void setTransducerTypeName(String value) {
		this.transducerTypeName = value;
	}

	
	public String getManufacturer() {
		return manufacturer;
	}

	
	public void setManufacturer(String value) {
		this.manufacturer = value;
	}

	
	public String getPartNumber() {
		return partNumber;
	}

	
	public void setPartNumber(String value) {
		this.partNumber = value;
	}

	
	public String getSerialNumber() {
		return serialNumber;
	}

	
	public void setSerialNumber(String value) {
		this.serialNumber = value;
	}

	
	public Float getMinValue() {
		return minValue;
	}

	public void setMinValue(Float value) {
		this.minValue = value;
	}

	
	public Float getMaxValue() {
		return maxValue;
	}

	
	public void setMaxValue(Float value) {
		this.maxValue = value;
	}

	
	public Float getResolution() {
		return resolution;
	}

	
	public void setResolution(Float value) {
		this.resolution = value;
	}

	
	public Float getPrecision() {
		return precision;
	}

	
	public void setPrecision(Float value) {
		this.precision = value;
	}

	
	public Float getAccuracy() {
		return accuracy;
	}

	
	public void setAccuracy(Float value) {
		this.accuracy = value;
	}

	
}
