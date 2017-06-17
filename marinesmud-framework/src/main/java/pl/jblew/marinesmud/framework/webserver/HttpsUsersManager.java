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

/**
 *
 * @author teofil
 */
public class HttpsUsersManager {
    private final Object sync = new Object();
    private final Map<String, HttpsUser> users = new HashMap<>();
    private final Map<String, HttpsUser> wsChannels = new HashMap<>();
    private final WebServerConfig config;
    private final String cookieName;

    public HttpsUsersManager(WebServerConfig config) {
        this.config = config;
        if (config.cookieName == null || config.cookieName.equals("#random")) {
            this.cookieName = CryptoUtils.getRandomString();
        } else {
            this.cookieName = config.cookieName;
        }
    }

    public HttpsUser parseCookies(String cookieHeader, HttpHeaders headersObj) {
        Cookie cookie = null;
        if (cookieHeader != null) {
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieHeader);
            for (Cookie c : cookies) {
                if (c.name() != null) {
                    //System.out.println("Cookie: {name=" + c.name() + "; domain=" + c.domain() + "}");
                    if (c.name().equals(cookieName) && (c.domain() == null || c.domain().equals(config.domain)) && c.isHttpOnly() && c.isSecure()) {
                        cookie = c;
                        break;
                    }
                }
            }
        }

        synchronized (sync) {
            String cookieValue = "";
            HttpsUser user;
            if (cookie != null && users.containsKey(cookie.value())) {
                cookieValue = cookie.value();
                user = users.get(cookieValue);
            } else {
                cookieValue = CryptoUtils.getRandomString();
                user = new HttpsUser();
                users.put(cookieValue, user);
            }
            user.touch();

            Cookie newCookie = new DefaultCookie(cookieName, cookieValue);
            newCookie.setDomain(config.domain);
            newCookie.setHttpOnly(true);
            newCookie.setMaxAge(config.cookiesTimeousS);
            newCookie.setSecure(true);
            headersObj.add(SET_COOKIE, ServerCookieEncoder.STRICT.encode(newCookie));

            return user;
        }

    }
}
