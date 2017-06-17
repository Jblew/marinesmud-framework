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

/**
 *
 * @author teofil
 */
public final class HttpsUser {
    public static final AttributeKey<HttpsUser> WSCHANNEL_USER_ATTR = AttributeKey.valueOf("httpsuser");

    private final Map<String, Object> properties = new HashMap<>();
    private final Object sync = new Object();
    private Channel webSocketChannel = null;
    private long lastSeen = System.currentTimeMillis();

    public Channel getWebSocketChannel() {
        synchronized (sync) {
            return webSocketChannel;
        }
    }

    public void setWebSocketChannel(Channel webSocketChannel) {
        webSocketChannel.attr(WSCHANNEL_USER_ATTR).set(this);
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

}
