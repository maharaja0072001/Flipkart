package org.abc.launcher;

import org.abc.authentication.view.AuthenticationView;
import org.abc.product.view.homepage.HomepageView;

import org.apache.logging.log4j.LogManager;

/**
 * <p>
 * Launches the flipkart application.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class Launcher {

    /**
     * <p>
     * Main entry to the flipkart application.
     * </p>
     *
     */
    public static void launch() {
        LogManager.getLogger(Launcher.class).info("Flipkart application started");
        final AuthenticationView authenticationView = AuthenticationView.getInstance();

        authenticationView.setHomePageView(HomepageView.getInstance());
        authenticationView.showAuthenticationPage();
    }
}
