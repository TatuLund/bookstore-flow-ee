package com.vaadin.samples.authentication;

import com.vaadin.cdi.annotation.CdiComponent;
import com.vaadin.cdi.annotation.VaadinSessionScoped;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;

/**
 * Default mock implementation of {@link AccessControl}. This implementation
 * accepts any string as a password, and considers the user "admin" as the only
 * administrator.
 */
@SessionScoped
public class BasicAccessControl implements AccessControl {

    @Inject
    CurrentUser currentUser;

    public BasicAccessControl() {    
    }

    @Override
    public boolean signIn(String username, String password) {
        if (username == null || username.isEmpty())
            return false;

        if (!username.equals(password))
            return false;

        currentUser.set(username);
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
            return getPrincipalName().equals("admin");
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
