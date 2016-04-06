/*
 * Copyright (C) 2014 Takuro Yonezawa
 * Keio University, Japan
 */

package jp.ac.keio.sfc.ht.sox.protocol;

import org.simpleframework.xml.*;

public class Property {

	  @Attribute(required = true)
	  protected String name;
	  @Attribute(required = true)
	  protected String value;
}
