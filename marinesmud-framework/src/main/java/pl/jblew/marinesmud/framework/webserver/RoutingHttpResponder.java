/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver;

import com.google.common.io.Resources;
import io.netty.handler.codec.http.FullHttpRequest;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.jblew.marinesmud.framework.webserver.modules.ErrorWebModule;

/**
 *
 * @author teofil
 */
public class RoutingHttpResponder {
    private final Map<String, WebModule> modules;
    private final WebModule errorModule = new ErrorWebModule();

    public RoutingHttpResponder(Map<String, WebModule> modules) {
        this.modules = Collections.synchronizedMap(modules);

    }

    public String getResponse(Path path, FullHttpRequest req) {
        String moduleName = (path.getNameCount() == 0 ? "index" : path.getName(0).toString());
        WebModule module = modules.get(moduleName);

        if (module == null) {
            module = errorModule;
        }

        try {
            return module.getResponse((path.getNameCount() == 0? Paths.get("/") : path.subpath((path.getNameCount() > 1? 1 : 0), path.getNameCount())), req);
        } catch (Exception ex) {
            Logger.getLogger(RoutingHttpResponder.class.getName()).log(Level.SEVERE, null, ex);
            return ex + "";
        }
    }
}
