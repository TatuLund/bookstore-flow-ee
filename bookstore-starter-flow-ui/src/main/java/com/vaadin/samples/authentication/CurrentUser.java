package com.vaadin.samples.authentication;

import javax.enterprise.context.Dependent;

import com.vaadin.cdi.annotation.VaadinSessionScoped;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;

/**
 * Class for retrieving and setting the name of the current user of the current
 * session (without using JAAS).
 */
@Dependent
public final class CurrentUser {

	private String currentUser;
	
    private CurrentUser() {
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
