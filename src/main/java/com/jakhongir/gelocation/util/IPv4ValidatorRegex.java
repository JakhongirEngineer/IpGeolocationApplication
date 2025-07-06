package com.jakhongir.gelocation.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


// source: https://mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/
public class IPv4ValidatorRegex {
    private static final String IPV4_PATTERN =
            "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
    private static final Pattern pattern = Pattern.compile(IPV4_PATTERN);

    public static boolean isValid(final String ipAddress) {
        if (ipAddress == null || ipAddress.isBlank()) {
            return false;
        }
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }
}
