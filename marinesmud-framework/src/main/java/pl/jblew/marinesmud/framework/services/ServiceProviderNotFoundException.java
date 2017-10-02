/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.services;

/**
 *
 * @author teofil
 */
public class ServiceProviderNotFoundException extends RuntimeException {

    public ServiceProviderNotFoundException() {
    }

    public ServiceProviderNotFoundException(String message) {
        super(message);
    }

    public ServiceProviderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceProviderNotFoundException(Throwable cause) {
        super(cause);
    }

    public ServiceProviderNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
