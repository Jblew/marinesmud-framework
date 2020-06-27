/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.remotecomponents;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.eventbus.EventBus;
import java.util.concurrent.atomic.AtomicInteger;
import pl.jblew.marinesmud.framework.util.TrueUID;
import pl.jblew.marinesmud.framework.webserver.websockets.RequestObject;

/**
 *
 * @author teofil
 */
public abstract class RemoteComponent {
    @JsonIgnore
    private final RemoteComponents rc;
    @JsonIgnore
    private static final AtomicInteger IDINCREMENTER = new AtomicInteger(0);
    @JsonIgnore
    private final String id;
    
    protected RemoteComponent(String name, RemoteComponents rc) {
        id = name+"-" + IDINCREMENTER.incrementAndGet();
        this.rc = rc;
    }
    
    public String getId() {
        return id;
    }
    
    protected String obtainUID() {
        return id+"-"+TrueUID.obtainUniqueId();
    }
    
    protected EventBus getEventBus() {
        return this.rc.getEventBus();
    }
    
    protected void registerRequestClass(Class<? extends RequestObject> classToAdd) {
        rc.registerRequestClass(classToAdd);
    }
    
    public abstract String getHtml();
}
