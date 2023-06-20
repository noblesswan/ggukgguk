package com.ggukgguk.api.auth.enums;

import java.util.Arrays;

public enum SocialTypeEnum {
	KAKAO("KAKAO"), NAVER("NAVER"), GOOGLE("GOOGLE");
	
	private final String label;
	
	SocialTypeEnum(String label) {
		this.label = label;
	}
	
	public String label() {
		return label;
	}
	
	public static SocialTypeEnum valueOfLabel(String label) {
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findAny()
                .orElse(null);
	}
}
