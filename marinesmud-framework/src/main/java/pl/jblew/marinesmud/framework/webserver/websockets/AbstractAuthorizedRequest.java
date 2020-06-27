/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.websockets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pl.jblew.marinesmud.framework.webserver.modules.AbstractAuthenticatedModule;

/**
 *
 * @author teofil
 */
public abstract class AbstractAuthorizedRequest<U> implements RequestObject {
    private final Class<? extends U> uClass;
    private WebSocketContext context;

    public AbstractAuthorizedRequest(Class<? extends U> uClass) {
        this.uClass = uClass;
    }
    
    public abstract boolean checkAccess(U u);
    
    @JsonIgnore
    public U getUser() {
        return AbstractAuthenticatedModule.getUser(context.session, uClass);
    }
    
    @JsonIgnore
    public boolean isAuthorized() {
        if(context == null) {
            System.out.println("AbstractAuthorizedRequest.context = null");
            return false;
        }
        else {
            U u = getUser();
            System.out.println("AbstractAuthorizedRequest.isAuthorized: User u = "+u);
            if(u == null) return false;
            else return checkAccess(u);
        }
    }
    
    @Override
    public void setContext(WebSocketContext c) {
        this.context = c;
    }
    
    public void sendResponse(ResponseObject ro) {
        if(!isAuthorized()) throw new RuntimeException("Trying to send response to unauthorized user!");
        else context.sendResponse(ro);        
    }
}
