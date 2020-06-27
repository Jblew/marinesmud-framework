/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.services;

import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @author teofil
 */
public abstract class ServiceSingletonProvider<S extends Service> extends ServiceProvider<S> {

    private final AtomicReference<S> serviceRef = new AtomicReference<S>();

    protected abstract S provideService(ServiceManager sm);

    @Override
    public S getService(ServiceManager sm) {
        synchronized (serviceRef) {
            if (serviceRef.get() == null) {
                serviceRef.set(provideService(sm));
                if (serviceRef.get() == null) {
                    throw new IllegalArgumentException("Cannot inject null service");
                }
            }

            return serviceRef.get();
        }
    }

    

    @Override
    public boolean isLoadable(ServiceManager sm) {
        return true;
    }

}
