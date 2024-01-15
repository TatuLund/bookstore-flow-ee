package com.vaadin.samples;

import com.vaadin.flow.server.VaadinRequest;

import jakarta.servlet.http.Cookie;

public class CookieUtil {

    public static Cookie getCookieByName(String name, VaadinRequest request) {
        // Fetch all cookies from the request
        Cookie[] cookies = request.getCookies();

        // Iterate to find cookie by its name
        try {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        } catch (NullPointerException e) {
        }
        return null;
    }

}
