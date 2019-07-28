package com.rabbit.backend.Utilities;

public class OAuthRedirectURLUtil {
    public static String generate(String platform) {
        return "https://www.6rabbit.com/oauth/" + platform + "/anonymous";
    }

    public static String generate(String platform, String token) {
        return "https://www.6rabbit.com/oauth/" + platform + "/token/" + token;
    }
}
