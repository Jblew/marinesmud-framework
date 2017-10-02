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
public class ServiceInjector<S extends Service> extends ServiceProvider<S> {
    private final S service;
    private final Class<? extends S> serviceClass;

    public ServiceInjector(S s, Class<? extends S> serviceClass) {
        this.service = s;
        this.serviceClass = serviceClass;
        if(s == null) throw new IllegalArgumentException("Cannot inject null service");
    }
    
    @Override
    public S getService(ServiceManager sm) {
        return service;
    }

    @Override
    public Class<? extends S> getServiceClass() {
        return serviceClass;
    }

    @Override
    public boolean isLoadable(ServiceManager sm) {
        return true;
    }
    
}