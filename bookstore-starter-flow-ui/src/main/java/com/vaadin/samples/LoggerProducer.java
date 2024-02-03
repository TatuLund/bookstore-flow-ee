package com.vaadin.samples;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Example of Producer, Logger does not have non-parameter constructor. We will get
// the class name needed for the constructor via InjectionPoint that gives info
// about Bean where we are injecting to 
@ApplicationScoped
public class LoggerProducer {

    @Produces
    public Logger produceLogger(InjectionPoint injectionPoint) {
        return LoggerFactory.getLogger(injectionPoint.getBean().getBeanClass());
    }

}