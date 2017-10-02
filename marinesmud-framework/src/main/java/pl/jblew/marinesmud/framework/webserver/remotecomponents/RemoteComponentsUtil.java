/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.remotecomponents;

import com.google.common.io.Resources;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import pl.jblew.marinesmud.framework.webserver.websockets.CommonEventBusWSPoster;

/**
 *
 * @author teofil
 */
public class RemoteComponentsUtil {

    private RemoteComponentsUtil() {
    }

    public static String getJavascript() {
        try {
            return Resources.toString(RemoteComponentsUtil.class.getResource("remotecomponents.js"), StandardCharsets.UTF_8)
                    .replace("{{registerChannelEventClass}}", CommonEventBusWSPoster.RegisterChannelEvent.class.getName());
        } catch (IOException ex) {
            throw new RuntimeException("RemoteComponents.js not found in framework resources");
        }
    }
}
