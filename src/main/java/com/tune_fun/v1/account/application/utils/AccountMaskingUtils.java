package com.tune_fun.v1.account.application.utils;

public class AccountMaskingUtils {

    public static final String MASKING_CHAR = "*";

    // username: 앞 1자리 이후 '*' 처리
    public static String maskUsername(String username) {
        if (username == null || username.length() < 2) {
            return username;
        }
        return username.charAt(0) + MASKING_CHAR.repeat(username.length() - 1);
    }

    // email: 앞 3자리 이후 ‘*’ 처리 '@'뒤 메일 표시
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String localPart = parts[0];
        String domainPart = parts[1];

        if (localPart.length() <= 3) {
            return MASKING_CHAR.repeat(localPart.length()) + "@" + domainPart;
        }

        return localPart.substring(0, 3) + MASKING_CHAR.repeat(localPart.length() - 3) + "@" + domainPart;
    }

    // nickname: 앞 3자리 이후 ‘*’ 처리
    public static String maskNickname(String nickname) {
        if (nickname == null || nickname.length() <= 3) {
            return nickname;
        }
        return nickname.substring(0, 3) + MASKING_CHAR.repeat(nickname.length() - 3);
    }

    // 모든 문자열을 '*'로 마스킹 처리
    public static String maskAll(String str) {
        if (str == null) {
            return null;
        }

        return MASKING_CHAR.repeat(str.length());
    }
}
