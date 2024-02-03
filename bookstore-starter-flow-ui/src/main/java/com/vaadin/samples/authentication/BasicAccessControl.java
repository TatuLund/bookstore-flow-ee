package com.vaadin.samples.authentication;

import org.slf4j.Logger;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;

/**
 * Default mock implementation of {@link AccessControl}. This implementation
 * accepts any string as a password, and considers the user "admin" as the only
 * administrator.
 */
@SuppressWarnings("serial")
@SessionScoped
public class BasicAccessControl implements AccessControl {

    @Inject
    CurrentUser currentUser;
    @Inject
    Logger logger;

    public BasicAccessControl() {    
    }

    @Override
    public boolean signIn(String username, String password) {
        if (username == null || username.isEmpty())
            return false;

        if (!username.equals(password))
            return false;
        currentUser.set(username);
        logger.info("User {} logged in", currentUser.get());
        return true;
    }

    @Override
    public boolean isUserSignedIn() {
        return !currentUser.get().isEmpty();
    }

    @Override
    public boolean isUserInRole(String role) {
        if ("admin".equals(role)) {
            // Only the "admin" user is in the "admin" role
            boolean hasRole = getPrincipalName().equals("admin");
            logger.info("User {}, admin={}", currentUser.get(), hasRole);
            return hasRole;
        }

        // All users are in all non-admin roles
        return true;
    }

    @Override
    public String getPrincipalName() {
        return currentUser.get();
    }

    @Override
    public void signOut() {
        VaadinSession.getCurrent().getSession().invalidate();
        UI.getCurrent().navigate("");
    }
}
