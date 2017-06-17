/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver;

import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @author teofil
 */
public class WebServer {
    private final WebServerConfig config;
    private final RoutingHttpResponder router;
    private final StaticFileLoader fileLoader;
    
    private final AtomicReference<HttpRedirectingServer> httpServer = new AtomicReference<>(null);
    private final AtomicReference<HttpsServer> httpsServer = new AtomicReference<>(null);
    private final Object sync = new Object();
    
    public WebServer(WebServerConfig config, RoutingHttpResponder router, StaticFileLoader fileLoader) {
        this.config = config;
        this.router = router;
        this.fileLoader = fileLoader;
    }
    
    public void start() {
        synchronized(sync) {
            if(httpServer.get() != null || httpsServer.get() != null) throw new RuntimeException("WebServer already started");
            httpServer.set(new HttpRedirectingServer(config));
            httpsServer.set(new HttpsServer(config, router, fileLoader));
        }
    }
    
    public void stop() {
        synchronized(sync) {
            if(httpServer.get() != null) {
                httpServer.get().stop();
                httpServer.set(null);
            }
            
            if(httpsServer.get() != null) {
                httpsServer.get().stop();
                httpsServer.set(null);
            }
        }
    }
}
