/*
 * Copyright (C) 2014 Takuro Yonezawa
 * Keio University, Japan
 */

package jp.ac.keio.sfc.ht.sox.protocol;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.*;

@Root
public class Data {

	@ElementList(inline = true, required = false)
	private List<TransducerValue> transducerValue;


	@ElementList(inline = true, required = false)
	private List<TransducerSetValue> transducerSetValue;

	public List<TransducerValue> getTransducerValue() {
		if (transducerValue == null) {
			transducerValue = new ArrayList<TransducerValue>();
		}
		return this.transducerValue;
	}

	public List<TransducerSetValue> getTransducerSetValue() {
		if (transducerSetValue == null) {
			transducerSetValue = new ArrayList<TransducerSetValue>();
		}
		return this.transducerSetValue;
	}

	public void setTransducerValue(List<TransducerValue> transducerValue) {
		this.transducerValue = transducerValue;
	}

	public void setTransducerSetValue(
			List<TransducerSetValue> transducerSetValue) {
		this.transducerSetValue = transducerSetValue;
	}

}
