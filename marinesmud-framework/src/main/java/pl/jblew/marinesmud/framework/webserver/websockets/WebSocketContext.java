/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.websockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.jblew.marinesmud.framework.webserver.HttpsSession;

/**
 *
 * @author teofil
 */
public final class WebSocketContext {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    public final HttpsSession session;
    public final Channel channel;

    public WebSocketContext(HttpsSession session, Channel channel) {
        this.session = session;
        this.channel = channel;
    }
    
    public void sendResponse(ResponseObject ro) {
        Response response = new Response();
        response.type = ro.getClass().getName();
        response.object = ro;
        
        try {
            session.sendToWebSocket(MAPPER.writeValueAsString(response));
        } catch (JsonProcessingException ex) {
            Logger.getLogger(WebSocketContext.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static final class Response {
        public String type;
        public ResponseObject object;
    }
}
