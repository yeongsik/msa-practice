package com.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 권한 Enum.
 */
@Getter
@RequiredArgsConstructor
public enum UserRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String key;
}
