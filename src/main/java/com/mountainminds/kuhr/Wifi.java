package com.mountainminds.kuhr;

public class Wifi {

	static enum Encryption {
		WPA
	}

	private Encryption encryption;
	private String ssid;
	private String password;
	private boolean hidden;

	public Wifi(Encryption encryption, String ssid, String password, boolean hidden) {
		this.encryption = encryption;
		this.ssid = ssid;
		this.password = password;
		this.hidden = hidden;
	}

	@Override
	public String toString() {
		var hiddenstr = hidden ? "H:true" : "";
		return String.format("WIFI:T:%s;S:%s;P:%s;%s;", encryption, ssid, password, hiddenstr);
	}

}
