package com.vaadin.samples;

import com.vaadin.cdi.annotation.VaadinServiceEnabled;
import com.vaadin.cdi.annotation.VaadinServiceScoped;
import com.vaadin.flow.server.CustomizedSystemMessages;
import com.vaadin.flow.server.SystemMessages;
import com.vaadin.flow.server.SystemMessagesInfo;
import com.vaadin.flow.server.SystemMessagesProvider;

@VaadinServiceEnabled
@VaadinServiceScoped
public class CustomSystemMessagesProvider implements SystemMessagesProvider {

    @Override
    public SystemMessages getSystemMessages(
            SystemMessagesInfo systemMessagesInfo) {
        CustomizedSystemMessages messages = new CustomizedSystemMessages();
        messages.setInternalErrorMessage("Sorry, something went wrong :(");
        return messages;
    }
}
