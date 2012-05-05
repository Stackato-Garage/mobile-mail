/* 
 * Copyright 2009 IT Mill Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.demo.mobilemail;

import com.vaadin.addon.touchkit.ui.TouchKitApplication;
import com.vaadin.demo.mobilemail.ui.MobileMailWindow;
import com.vaadin.demo.mobilemail.ui.SmartphoneMainView;
import com.vaadin.demo.mobilemail.ui.TabletMainView;
import com.vaadin.terminal.gwt.server.WebBrowser;

@SuppressWarnings("serial")
public class MobileMailApplication extends TouchKitApplication {

    static CustomizedSystemMessages customizedSystemMessages = new CustomizedSystemMessages();

    static {
        // reload on session expired
        customizedSystemMessages.setSessionExpiredCaption(null);
        customizedSystemMessages.setSessionExpiredMessage(null);
    }

    public static SystemMessages getSystemMessages() {
        return customizedSystemMessages;
    }

    @Override
    public void init() {
        setMainWindow(new MobileMailWindow());

        // Using mobile mail theme
        setTheme("mobilemail");

        getMainWindow().addApplicationIcon(
                getURL() + "VAADIN/themes/mobilemail/apple-touch-icon.png");
    }

    @Override
    public void onBrowserDetailsReady() {
        WebBrowser browser = getBrowser();
        if (!browser.isTouchDevice()) {
            getMainWindow()
                    .showNotification(
                            "You appear to be running on a desktop software or other non touch device. We'll show you the tablet (or smartphone view if small screen size) for debug purposess.");
        }

        if (isSmallScreenDevice()) {
            getMainWindow().setContent(new SmartphoneMainView());
        } else {
            getMainWindow().setContent(new TabletMainView());
        }
    }

    public boolean isSmallScreenDevice() {
        float viewPortWidth = getMainWindow().getWidth();
        return viewPortWidth < 600;
    }
}
