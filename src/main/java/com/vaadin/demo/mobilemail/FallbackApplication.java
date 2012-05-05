package com.vaadin.demo.mobilemail;

import com.vaadin.Application;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class FallbackApplication extends Application {

    private static final String MSG = "<h1>Ooops...</h1> You accessed this demo "
            + "with a browser that is currently not supported by TouchKit. "
            + "TouchKit is "
            + "ment to be used with modern webkit based mobile browsers, "
            + "e.g. with iPhone. Curretly those "
            + "cover huge majority of actively used mobile browsers. "
            + "Support will be extended as other mobile browsers develop "
            + "and gain popularity. Testing ought to work with desktop "
            + "Safari or Chrome as well.";

    @Override
    public void init() {
        Window window = new Window("Unsupported browser");
        window.addComponent(new Label(MSG, Label.CONTENT_XHTML));
        setMainWindow(window);

    }
}
