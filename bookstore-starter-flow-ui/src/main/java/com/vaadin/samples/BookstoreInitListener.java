package com.vaadin.samples;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.vaadin.cdi.annotation.VaadinSessionScoped;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.samples.authentication.AccessControl;
//import com.vaadin.samples.authentication.AccessControlFactory;
import com.vaadin.samples.authentication.LoginScreen;

/**
 * This class is used to listen to BeforeEnter event of all UIs in order to
 * check whether a user is signed in or not before allowing entering any page.
 * It is registered in a file named
 * com.vaadin.flow.server.VaadinServiceInitListener in META-INF/services.
 */
@VaadinSessionScoped
public class BookstoreInitListener {

	@Inject
	AccessControl accessControl;
	
    private void onServiceInit(@Observes ServiceInitEvent initEvent) {
        initEvent.getSource().addUIInitListener(uiInitEvent -> {
            uiInitEvent.getUI().addBeforeEnterListener(enterEvent -> {
                if (!accessControl.isUserSignedIn() && !LoginScreen.class
                        .equals(enterEvent.getNavigationTarget()))
                    enterEvent.rerouteTo(LoginScreen.class);
            });
        });
    }	
}
