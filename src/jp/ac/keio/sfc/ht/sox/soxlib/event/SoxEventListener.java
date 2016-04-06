/*
 * Copyright (C) 2014 Takuro Yonezawa
 * Keio University, Japan
 */

package jp.ac.keio.sfc.ht.sox.soxlib.event;

import java.util.EventListener;

public interface SoxEventListener extends EventListener{

	public void handlePublishedSoxEvent(SoxEvent e);
	
}
