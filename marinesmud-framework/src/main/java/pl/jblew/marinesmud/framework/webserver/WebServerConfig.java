/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver;

/**
 *
 * @author teofil
 */
public class WebServerConfig {
    public int httpPort = 8080;
    public int httpsPort = 8443;
    public String listenDomain = "127.0.0.1";
    public String cookieDomain = "127.0.0.1";
    public String externalDomain = "127.0.0.1";
    public String cookieName = "#random";
    public long cookiesTimeousS = 8*60*60; //8 hours
    public boolean useTemporarySelfSignedCertificate = true;
    public String sslPubKeyFile = null;
    public String sslPrivateKeyFile = null;
}
