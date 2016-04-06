/*
 * Copyright (C) 2014 Takuro Yonezawa
 * Keio University, Japan
 */

package jp.ac.keio.sfc.ht.sox.soxlib;



import jp.ac.keio.sfc.ht.sox.protocol.DeviceType;
import org.simpleframework.xml.transform.Transform;


public class SoxEnumTransform implements Transform<Enum> {
	private final Class type;

	public SoxEnumTransform(Class type) {
		this.type = type;
	}

	public Enum read(String value) throws Exception {
		for (Object o : type.getEnumConstants()) {
			if(o instanceof DeviceType){
				return ((DeviceType) o).fromValue(value);
			}

		}
		return null;
	}

	public String write(Enum value) throws Exception {

		for (Object o : type.getEnumConstants()) {
			if(o instanceof DeviceType){
				if(((DeviceType) o).name().equals(value.toString())){
					return ((DeviceType)o).value();
				}
			}
		}

		return value.toString();
	}
}