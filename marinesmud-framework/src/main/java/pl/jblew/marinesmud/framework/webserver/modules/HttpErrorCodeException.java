/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.modules;

/**
 *
 * @author teofil
 */
public class HttpErrorCodeException extends Exception {
    public final int httpCode;

    public HttpErrorCodeException(int httpCode) {
        this.httpCode = httpCode;
    }
}
