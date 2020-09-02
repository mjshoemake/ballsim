/*
package mjs.common.core;

import org.springframework.web.servlet.DispatcherServlet;
import mjs.server.ServerSetup;


public class SpringServlet extends DispatcherServlet {

    ServerSetup setup = null;

    public SpringServlet() {
        super();
        setup = new ServerSetup();
        try {
            setup.init();
        } catch (Exception e) {
            System.out.println("Failed to initialize logging infrastructure and configuration properties.");
            e.printStackTrace();
        }
    }
}
*/