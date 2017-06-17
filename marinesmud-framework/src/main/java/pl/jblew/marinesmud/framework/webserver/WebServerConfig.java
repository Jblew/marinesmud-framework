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
    public String domain = "127.0.0.1";
    public String cookieName = "#random";
    long cookiesTimeousS = 15*24*60*60; //15 days
}
