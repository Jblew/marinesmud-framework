/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.modules;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.h2.engine.User;
import pl.jblew.marinesmud.framework.crypto.CredentialsManager;
import pl.jblew.marinesmud.framework.webserver.HttpsSession;

/**
 *
 * @author teofil
 */
public abstract class AbstractAuthenticatedModule<U> extends AbstractModule {

    private static final String USER_OBJ_PROPERTY = "user-obj-prop";
    private final CredentialsManager<U> credentials;
    private final String loginTemplate;
    private String flashMessage = "";

    public AbstractAuthenticatedModule(Class<?> templateContext, CredentialsManager<U> credentials) {
        super(templateContext);
        this.credentials = credentials;
        loginTemplate = null;
    }

    public AbstractAuthenticatedModule(Class<?> templateContext, String defaultTemplate, CredentialsManager<U> credentials) {
        super(templateContext, defaultTemplate);
        this.credentials = credentials;
        loginTemplate = null;
    }

    public AbstractAuthenticatedModule(Class<?> templateContext, String defaultTemplate, String loginFormTemplate, CredentialsManager<U> credentials) {
        super(templateContext, defaultTemplate);
        this.credentials = credentials;
        this.loginTemplate = loginFormTemplate;
    }

    public abstract boolean checkAccess(Path subpath, FullHttpRequest req, U user);

    public abstract byte[] getResponse(Path subpath, FullHttpRequest req, HttpsSession session, U user) throws HttpErrorCodeException;

    private final byte[] loginPage(FullHttpRequest req, HttpsSession session) {
        try {
            if (loginTemplate == null) {
                return render("Auth", "<form method=\"POST\" class=\"auth-form\">"
                        + "      <input type=\"text\" name=\"username\" placeholder=\"User name\" required=\"\" autofocus=\"\" />"
                        + "      <input type=\"password\" name=\"password\" placeholder=\"Password\" required=\"\"/>"
                        + "      <button type=\"submit\">Login</button>"
                        + "      <strong style=\"color: red;\">" + flashMessage + "</strong>"
                        + "</form>");
            } else {
                return render("Auth", super.templateLoader.getOrLoadTemplate(loginTemplate)
                        .replace("{{username-field}}", "username")
                        .replace("{{password-field}}", "password")
                        .replace("{{flash-message}}", flashMessage)
                );
            }
        } finally {
            flashMessage = "";
        }
    }
    
    private final byte[] loginRedirectPage(FullHttpRequest req, HttpsSession session) {
        return render("Auth – redirection", "<script type=\"text/javascript\">window.location.replace(\"/\");</script>");
    }

    public static <U> U getUser(HttpsSession session, Class<U> userClass) {
        U userObj = (U) session.getProperty(USER_OBJ_PROPERTY);
        return userObj;
    }

    @Override
    public byte[] getResponse(Path subpath, FullHttpRequest req, HttpsSession session) throws HttpErrorCodeException {
        boolean loginCompleted = tryLogin(req, session);
        if(loginCompleted) {
            return loginRedirectPage(req, session);
        }
        
        QueryStringDecoder queryDecoder = new QueryStringDecoder(req.uri(), true);

        U userObj = (U) session.getProperty(USER_OBJ_PROPERTY);
        if (userObj != null) {
            if (queryDecoder.parameters().containsKey("logout") && logout(req, session)) {
                return loginPage(req, session);
            } else if (checkAccess(subpath, req, userObj)) {
                return getResponse(subpath, req, session, userObj);
            } else {
                throw new HttpErrorCodeException(403);
            }
        } else {
            return loginPage(req, session);
        }
    }

    private boolean tryLogin(FullHttpRequest req, HttpsSession session) {
        if (req.method() == HttpMethod.POST) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), req);
            InterfaceHttpData dataUsername = decoder.getBodyHttpData("username");
            InterfaceHttpData dataPassword = decoder.getBodyHttpData("password");
            if (dataUsername.getHttpDataType() == HttpDataType.Attribute && dataPassword.getHttpDataType() == HttpDataType.Attribute) {
                try {
                    String username = ((Attribute) dataUsername).getValue();
                    String password = ((Attribute) dataPassword).getValue();
                    U user = credentials.login(username, password);

                    if (user == null) {
                        flashMessage = "Wrong username or password";
                    } else {
                        session.setProperty(USER_OBJ_PROPERTY, user);
                        return true;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(AbstractAuthenticatedModule.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return false;
    }

    private boolean logout(FullHttpRequest req, HttpsSession session) {
            session.setProperty(USER_OBJ_PROPERTY, null);
            return true;
    }
}
