package com.vaadin.samples;

import java.io.Serializable;

import org.slf4j.Logger;

import com.vaadin.flow.server.ServiceInitEvent;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.servlet.http.Cookie;

/**
 * This class is used to listen to BeforeEnter event of all UIs in order to
 * check whether a user is signed in or not before allowing entering any page.
 * It is registered in a file named
 * com.vaadin.flow.server.VaadinServiceInitListener in META-INF/services.
 */
@SuppressWarnings("serial")
@Dependent
public class BookstoreInitListener implements Serializable {

    @Inject
    Logger logger;
    private String locale;

    // ServiceInitEvent CDI event is dispatched by CDI add-on upon
    // VaadinService start, no listener registration is needed when using this
    @SuppressWarnings("unused")
    private void onServiceInit(@Observes ServiceInitEvent initEvent) {
        logger.info("Service started");

        initEvent.addRequestHandler((session, request, response) -> {
            Cookie localeCookie = CookieUtil.getCookieByName("language",
                    request);

            session.access(() -> {
                locale = (String) session.getAttribute("locale");
            });

            if (locale != null) {
                if (localeCookie == null) {
                    localeCookie = new Cookie("language", locale);
                    localeCookie.setPath(request.getContextPath());
                    localeCookie.setMaxAge(60 * 60);
                } else {
                    localeCookie.setValue(locale);
                }
                response.addCookie(localeCookie);
            }
            return false;
        });
    }
}
