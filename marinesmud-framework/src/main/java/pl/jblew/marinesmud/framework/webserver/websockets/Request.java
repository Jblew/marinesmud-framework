/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.websockets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import pl.jblew.marinesmud.framework.webserver.HttpsSession;

/**
 *
 * @author teofil
 */
//@JsonDeserialize(using = RequestDeserializer.class)
class Request {
    public String type;
    public RequestObject object;
    
    @JsonIgnore
    public HttpsSession session;
}
