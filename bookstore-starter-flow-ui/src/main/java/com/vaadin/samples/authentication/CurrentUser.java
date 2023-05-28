package com.vaadin.samples.authentication;

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;

/**
 * Class for retrieving and setting the name of the current user of the current
 * session (without using JAAS).
 */
@SessionScoped
public class CurrentUser implements Serializable {

    private String currentUser;

    public CurrentUser() {
    }

    /**
     * Returns the name of the current user stored in the current session, or an
     * empty string if no user name is stored.
     * 
     */
    public String get() {
        if (currentUser == null) {
            return "";
        } else {
            return currentUser;
        }
    }

    /**
     * Sets the name of the current user and stores it in the current session.
     * Using a {@code null} username will remove the username from the session.
     * 
     */
    public void set(String currentUser) {
        this.currentUser = currentUser;
    }
}
