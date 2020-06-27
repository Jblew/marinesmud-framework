/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.websockets;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author teofil
 */
public abstract class AbstractRequestObject implements RequestObject {
    @JsonIgnore
    protected WebSocketContext context;
    @Override
    public void setContext(WebSocketContext c) {
        this.context = c;
    }
    
}
