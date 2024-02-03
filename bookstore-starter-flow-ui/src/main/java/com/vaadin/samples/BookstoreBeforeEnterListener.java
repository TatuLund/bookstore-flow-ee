package com.vaadin.samples;

import java.io.Serializable;

import org.slf4j.Logger;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.samples.authentication.AccessControl;
import com.vaadin.samples.authentication.LoginView;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@SuppressWarnings("serial")
@Dependent
public class BookstoreBeforeEnterListener implements Serializable {

    @Inject
    AccessControl accessControl;
    @Inject
    Logger logger;

    @SuppressWarnings("unused")
    private void onServiceInit(@Observes BeforeEnterEvent enterEvent) {
        logger.info("Attempt navigate to {}",
                enterEvent.getLocation().getPath());
        if (!accessControl.isUserSignedIn()
                && !LoginView.class.equals(enterEvent.getNavigationTarget()))
            enterEvent.rerouteTo(LoginView.class);
    }
}
