/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.netty.channel.Channel;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.jblew.marinesmud.framework.webserver.HttpsSession;
import pl.jblew.marinesmud.framework.webserver.websockets.WebSocketFrameHandler;

/**
 *
 * @author teofil
 */
public class JSONFrameHandler extends WebSocketFrameHandler {
    private final ObjectMapper mapper;
    private final Map<String, Class<? extends RequestObject>> requestClasses;
    private final EventBus eBus;

    public JSONFrameHandler(EventBus eBus, Class<? extends RequestObject>[] requestClasses) {
        this.eBus = eBus;

        Map<String, Class<? extends RequestObject>> requestClassesMap = new HashMap<>();
        Arrays.stream(requestClasses).forEach(clazz -> requestClassesMap.put(clazz.getName(), clazz));
        this.requestClasses = requestClassesMap;

        mapper = new ObjectMapper();
        SimpleModule mod = new SimpleModule();
        mod.addDeserializer(Request.class, new RequestDeserializer(this.requestClasses));
        mapper.registerModule(mod);
        
        eBus.register(new Object() {
            @Subscribe
            public void onAddRequestClassToFilterEvent(AddRequestClassToFilterEvent evt) {
                JSONFrameHandler.this.requestClasses.put(evt.classToAdd.getName(), evt.classToAdd);
            }
        });
        
    }

    @Override
    public void processText(HttpsSession session, Channel channel, String text) {
        try {
            Request r = mapper.readValue(text, Request.class);
            r.object.setContext(new WebSocketContext(session, channel));
            eBus.post(r.object);
        } catch (Exception ex) {
            Logger.getLogger(JSONFrameHandler.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Text that produced error: "+text);
        }
    }

    //@JsonSerialize(using = RequestSerializer.class)
    /*private static final class RequestSerializer extends StdSerializer<Request> {
        public RequestSerializer() {
            this(null);
        }

        public RequestSerializer(Class<Request> vc) {
            super(vc);
        }

        @Override
        public void serialize(Request t, JsonGenerator jg, SerializerProvider sp) throws IOException {
            jgen.writeStartObject(););
        jgen.writeStringField("itemName", value.itemName);
        jgen.writeNumberField("owner", value.owner.id);
        jgen.writeEndObject();
        }

    }*/
    
    public static class AddRequestClassToFilterEvent {
        public final Class<? extends RequestObject> classToAdd;

        public AddRequestClassToFilterEvent(Class<? extends RequestObject> classToAdd) {
            this.classToAdd = classToAdd;
        }
    }
}
