/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver;

import io.netty.handler.codec.http.FullHttpRequest;
import java.nio.file.Path;

/**
 *
 * @author teofil
 */
public interface WebModule {
    public String getResponse(Path subpath, FullHttpRequest req);
}
