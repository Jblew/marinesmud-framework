/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.services;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author teofil
 */
public class ServiceManager {
    private final Map<String, ServiceProvider> serviceProviders = new HashMap<>();
    
    public ServiceManager(ServiceProvider [] providers) {
        for(ServiceProvider sp : providers) {
            serviceProviders.put(sp.getServiceClass().getName(), sp);
        }
    }
    
    public void addProvider(ServiceProvider sp) {
        System.out.println("Provider registered for "+sp.getServiceClass()+": (provider="+sp.getClass()+")");
        serviceProviders.put(sp.getServiceClass().getName(), sp);
    }
    
    public <A extends Service> A getService(Class<A> serviceClass) {
        if(serviceProviders.containsKey(serviceClass.getName())) {
            return (A) serviceProviders.get(serviceClass.getName()).getService(this);
        }
        else {
            if(serviceClass.isAnnotationPresent(DefaultServiceProvider.class)) {
                try {
                    ServiceProvider sp = serviceClass.getAnnotation(DefaultServiceProvider.class).provider().newInstance();
                    if(sp != null && sp instanceof ServiceProvider) {
                        Service s = sp.getService(this);
                        if(s != null) {
                            this.addProvider(sp);
                            return (A) s;
                        }
                        else if(s == null) return null;
                    }
                } catch (InstantiationException ex) {
                    Logger.getLogger(ServiceManager.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException("Cannot construct default provider");
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(ServiceManager.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException("Cannot construct default provider");
                }
            }
            throw new ServiceProviderNotFoundException(serviceClass.getName());
        }
    }
    
    public boolean isLoadable(Class<? extends Service> serviceClass) {
        return serviceProviders.containsKey(serviceClass.getName()) && serviceProviders.get(serviceClass.getName()).isLoadable(this);
    }
}
