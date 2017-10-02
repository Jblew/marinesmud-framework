/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver;

import pl.jblew.marinesmud.framework.services.ServiceManager;
import pl.jblew.marinesmud.framework.services.ServiceProvider;

/**
 *
 * @author teofil
 */
public class WebServerInjector extends ServiceProvider<WebServer> {
    private final WebServer ws;

    public WebServerInjector(WebServer ws) {
        this.ws = ws;
        if(ws == null) throw new IllegalArgumentException("Cannot inject null webserver");
    }
    
    @Override
    public WebServer getService(ServiceManager sm) {
        return ws;
    }

    @Override
    public Class<? extends WebServer> getServiceClass() {
        return WebServer.class;
    }

    @Override
    public boolean isLoadable(ServiceManager sm) {
        return true;
    }
    
}
