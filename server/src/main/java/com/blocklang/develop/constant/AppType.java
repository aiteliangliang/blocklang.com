package com.blocklang.develop.constant;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public enum AppType {
	
	WEB("01", "web", IconClass.WEB, "Web"),
	ANDROID("02", "android", IconClass.ANDROID, "Android"),
	IOS("03", "ios", IconClass.IOS, "ios"),
	WECHAT_MINI_APP("04", "wechat", IconClass.WECHAT, "微信小程序"),
	ALIPAY_MINI_APP("05", "alipay", IconClass.ALIPAY, "支付宝小程序"),
	QUICK_APP("06", "quickapp", IconClass.QUICK_APP, "快应用"),
	UNKNOWN("99", "", "", "Unknown");

	private final String key;
	private final String value;
	private final String icon;
	private final String label;

	private AppType(String key, String value, String icon, String label) {
		this.key = key;
		this.value = value;
		this.icon = icon;
		this.label = label;
	}

	public String getKey() {
		return this.key;
	}

	public String getValue() {
		return this.value;
	}
	
	public String getIcon() {
		return this.icon;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public static AppType fromValue(String value) {
		if (StringUtils.isBlank(value)) {
			return UNKNOWN;
		}
		return Arrays.stream(AppType.values())
				.filter((each) -> value.toLowerCase().equals(each.getValue().toLowerCase()))
				.findFirst()
				.orElse(UNKNOWN);
	}

	public static AppType fromKey(String key) {
		if (StringUtils.isBlank(key)) {
			return UNKNOWN;
		}
		return Arrays.stream(AppType.values())
				.filter((each) -> key.equals(each.getKey()))
				.findFirst()
				.orElse(UNKNOWN);
	}

}
