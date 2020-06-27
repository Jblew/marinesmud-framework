/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.remotecomponents;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import pl.jblew.marinesmud.framework.services.DefaultServiceProvider;
import pl.jblew.marinesmud.framework.services.Service;
import pl.jblew.marinesmud.framework.services.ServiceManager;
import pl.jblew.marinesmud.framework.services.ServiceProvider;
import pl.jblew.marinesmud.framework.services.ServiceSingletonProvider;
import pl.jblew.marinesmud.framework.webserver.websockets.CommonEventBusWSPoster;
import pl.jblew.marinesmud.framework.webserver.websockets.JSONFrameHandler;
import pl.jblew.marinesmud.framework.webserver.websockets.RequestObject;
import pl.jblew.marinesmud.framework.webserver.websockets.WebSocketFrameHandler;

/**
 *
 * @author teofil
 */
@DefaultServiceProvider(provider=RemoteComponents.DefaultSingletonProvider.class)
public class RemoteComponents implements Service {
    private final AtomicReference<AsyncEventBus> aebRef = new AtomicReference<>();
    
    public WebSocketFrameHandler initialize(AsyncEventBus aeb) {
        if(aebRef.get() != null) throw new RuntimeException("RemoteComponents already initialized");
        aebRef.set(aeb);
        aeb.register(new CommonEventBusWSPoster(aeb));
        WebSocketFrameHandler wsHandler = new JSONFrameHandler(aeb, new Class[]{CommonEventBusWSPoster.RegisterChannelEvent.class});
        return wsHandler;

    }
    
    public void registerRequestClass(Class<? extends RequestObject> classToAdd) {
        this.getEventBus().post(new JSONFrameHandler.AddRequestClassToFilterEvent(classToAdd));
    }
    
    public EventBus getEventBus() {
        if(aebRef.get() == null) throw new RuntimeException("RemoteComponents not yet initialized");
        return aebRef.get();
    }
    
    public static class DefaultSingletonProvider extends ServiceSingletonProvider<RemoteComponents> {

        @Override
        protected RemoteComponents provideService(ServiceManager sm) {
            return new RemoteComponents();
        }

        @Override
        public Class<? extends RemoteComponents> getServiceClass() {
            return RemoteComponents.class;
        }
        
    }
}
