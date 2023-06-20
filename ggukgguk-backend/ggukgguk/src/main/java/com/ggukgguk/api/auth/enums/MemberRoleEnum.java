package com.ggukgguk.api.auth.enums;

import java.util.Arrays;

public enum MemberRoleEnum {

	ROLE_MEMBER("NORMAL"), ROLE_ADMIN("SERVICE_ADMIN"), ROLE_SYSTEM_ADMIN("SYSTEM_ADMIN");

	private final String label;
	
	MemberRoleEnum(String label) {
		this.label = label;
	}
	
	public String label() {
		return label;
	}
	
	public static MemberRoleEnum valueOfLabel(String label) {
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findAny()
                .orElse(null);
	}
}
