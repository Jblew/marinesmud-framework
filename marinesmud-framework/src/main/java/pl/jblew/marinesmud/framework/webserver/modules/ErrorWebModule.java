/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.modules;

import io.netty.handler.codec.http.FullHttpRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.jblew.marinesmud.framework.webserver.WebModule;

/**
 *
 * @author teofil
 */
public class ErrorWebModule implements WebModule {
    public ErrorWebModule() {
    }

    @Override
    public String getResponse(Path url, FullHttpRequest req) {
        return "Error: "+url;
    }
    
}
