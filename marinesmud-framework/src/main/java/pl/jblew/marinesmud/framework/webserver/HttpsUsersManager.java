/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import static io.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import pl.jblew.marinesmud.framework.crypto.CryptoUtils;
import pl.jblew.marinesmud.framework.util.TwoTuple;

/**
 *
 * @author teofil
 */
public class HttpsUsersManager {
    private static final long SESSION_CLEANUP_INTERVAL_MS = 60 * 1000;
    private final Object sync = new Object();
    private final Map<String, HttpsSession> sessions = new HashMap<>();
    private final WebServerConfig config;
    private final String cookieName;
    private long lastCleanupTimestamp = System.currentTimeMillis();

    public HttpsUsersManager(WebServerConfig config) {
        this.config = config;
        if (config.cookieName == null || config.cookieName.equals("#random")) {
            this.cookieName = CryptoUtils.getRandomString();
        } else {
            this.cookieName = config.cookieName;
        }
    }

    public TwoTuple<HttpsSession, Cookie> parseCookies(String cookieHeader) {
        cleanup();

        Cookie cookie = null;
        if (cookieHeader != null) {
            Set<Cookie> cookies = ServerCookieDecoder.LAX.decode(cookieHeader);
            for (Cookie c : cookies) {
                if (c.name() != null && c.name().equals(cookieName)) {
                    if ((c.domain() == null || c.domain().equals(config.cookieDomain))) {
                        if (sessions.containsKey(c.value())) {
                            cookie = c;
                            break;
                        }
                    }
                }
            }
        }

        synchronized (sync) {
            String cookieValue = "";
            HttpsSession session;
            if (cookie != null && sessions.containsKey(cookie.value())) {
                cookieValue = cookie.value();
                session = sessions.get(cookieValue);
            } else {
                cookieValue = CryptoUtils.getRandomString() + "" + System.currentTimeMillis();
                session = new HttpsSession();
                sessions.put(cookieValue, session);
            }
            session.touch();

            Cookie newCookie = new DefaultCookie(cookieName, cookieValue);
            newCookie.setDomain(config.cookieDomain);
            newCookie.setHttpOnly(true);
            newCookie.setMaxAge(config.cookiesTimeousS);
            newCookie.setSecure(true);

            return new TwoTuple<>(session, newCookie);
        }
    }

    private void cleanup() {
        synchronized (sync) {
            if (System.currentTimeMillis() - this.lastCleanupTimestamp > SESSION_CLEANUP_INTERVAL_MS) {
                for (String key : sessions.keySet()) {
                    HttpsSession session = sessions.get(key);
                    if (System.currentTimeMillis() - session.getLastSeen() > config.cookiesTimeousS * 1000) {
                        sessions.remove(key);
                    }
                }
                this.lastCleanupTimestamp = System.currentTimeMillis();
            }
        }
    }
}
