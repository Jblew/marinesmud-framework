/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.remotecomponents;

import java.util.concurrent.atomic.AtomicReference;
import pl.jblew.marinesmud.framework.services.ServiceManager;
import pl.jblew.marinesmud.framework.services.ServiceProvider;
import pl.jblew.marinesmud.framework.webserver.WebServer;

/**
 *
 * @author teofil
 */
public class RemoteComponentsProvider extends ServiceProvider<RemoteComponents> {
    private final AtomicReference<RemoteComponents> ref = new AtomicReference<>();
    
    @Override
    public RemoteComponents getService(ServiceManager sm) {
        if(ref.get() == null) {
            ref.set(new RemoteComponents());
        }
        return ref.get();
    }

    @Override
    public Class<? extends RemoteComponents> getServiceClass() {
        return RemoteComponents.class;
    }

    @Override
    public boolean isLoadable(ServiceManager sm) {
        return true;
    }
    
}
