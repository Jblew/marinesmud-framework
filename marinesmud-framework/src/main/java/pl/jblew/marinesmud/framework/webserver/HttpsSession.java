/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import java.util.HashMap;
import java.util.Map;
import pl.jblew.marinesmud.framework.mod.io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 *
 * @author teofil
 */
public final class HttpsSession {
    public static final AttributeKey<HttpsSession> WSCHANNEL_SESSION_ATTR = AttributeKey.valueOf("httpssession");

    private final Map<String, Object> properties = new HashMap<>();
    private final Object sync = new Object();
    private Channel webSocketChannel = null;
    private long lastSeen = System.currentTimeMillis();

    public HttpsSession() {
        
    }
    
    public Channel getWebSocketChannel() {
        synchronized (sync) {
            return webSocketChannel;
        }
    }

    public void setWebSocketChannel(Channel webSocketChannel) {
        webSocketChannel.attr(WSCHANNEL_SESSION_ATTR).set(this);
        synchronized (sync) {
            this.webSocketChannel = webSocketChannel;
        }
    }

    void touch() {
        synchronized (sync) {
            lastSeen = System.currentTimeMillis();
        }
    }

    public Object getProperty(String name) {
        synchronized (sync) {
            return properties.get(name);
        }
    }

    public void setProperty(String name, Object value) {
        synchronized (sync) {
            properties.put(name, value);
        }
    }

    public long getLastSeen() {
        synchronized (sync) {
            return lastSeen;
        }
    }

    public void sendToWebSocket(String text) {
        Channel channel = this.getWebSocketChannel();
        channel.writeAndFlush(new TextWebSocketFrame(text));
    }
}
