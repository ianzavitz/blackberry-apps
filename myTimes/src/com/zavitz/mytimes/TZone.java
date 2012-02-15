package com.zavitz.mytimes;

import net.rim.device.api.util.Persistable;

public class TZone implements Persistable {

	public String name;
	public String zone;
	
	public TZone(String _name, boolean _plus, int _offsetHours, int _offsetMinutes) {
		name = _name;
		zone = "GMT" + (_plus ? "+" : "-") + (_offsetHours < 10 ? "0" : "") + _offsetHours + ":" + (_offsetMinutes < 10 ? "0" : "") + _offsetMinutes;
	}

}
