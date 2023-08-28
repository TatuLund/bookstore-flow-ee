package com.vaadin.samples;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;

import com.vaadin.cdi.annotation.VaadinServiceEnabled;
import com.vaadin.cdi.annotation.VaadinServiceScoped;
import com.vaadin.flow.i18n.I18NProvider;

import jakarta.inject.Inject;

@VaadinServiceEnabled
@VaadinServiceScoped
public class CustomI18NProvider implements I18NProvider {

    @Inject
    Logger logger;

    private static final String BUNDLE_PREFIX = "translate";

    private final Locale LOCALE_FI = new Locale("fi", "FI");
    private final Locale LOCALE_EN = new Locale("en", "GB");

    private List<Locale> locales = Collections
            .unmodifiableList(Arrays.asList(LOCALE_FI, LOCALE_EN));

    @Override
    public List<Locale> getProvidedLocales() {
        return locales;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) {
            logger.warn("Got lang request for key with null value!");
            return "";
        }

        ResourceBundle bundle;
        if (getProvidedLocales().contains(locale)) {
            bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale);
        } else {
            bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, LOCALE_EN);
        }

        String value;
        try {
            value = bundle.getString(key);
        } catch (final MissingResourceException e) {
            logger.warn("Missing resource", e);
            return "!" + locale.getLanguage() + ": " + key;
        }
        if (params.length > 0) {
            value = MessageFormat.format(value, params);
        }
        return value;
    }

}
