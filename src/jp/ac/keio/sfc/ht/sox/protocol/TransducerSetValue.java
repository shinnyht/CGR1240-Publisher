/*
 * Copyright (C) 2014 Takuro Yonezawa
 * Keio University, Japan
 */

package jp.ac.keio.sfc.ht.sox.protocol;

import org.simpleframework.xml.*;

@Root
public class TransducerSetValue {

	@Attribute
    private String id;

	@Attribute
    private String typedValue;
    
	@Attribute
	private String rawValue;
}
