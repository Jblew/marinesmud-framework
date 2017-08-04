/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.modules;

import io.netty.handler.codec.http.FullHttpRequest;
import java.nio.file.Path;
import pl.jblew.marinesmud.framework.webserver.HttpsSession;

/**
 *
 * @author teofil
 */
public interface WebModule {
    public byte [] getResponse(Path subpath, FullHttpRequest req, HttpsSession session) throws HttpErrorCodeException;
}
