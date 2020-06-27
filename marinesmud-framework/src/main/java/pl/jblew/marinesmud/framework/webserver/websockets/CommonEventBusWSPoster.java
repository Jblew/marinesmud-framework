/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.websockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.netty.channel.Channel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.jblew.marinesmud.framework.mod.io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 *
 * @author teofil
 */
public class CommonEventBusWSPoster {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final Set<Channel> channels = Collections.newSetFromMap(new WeakHashMap<Channel, Boolean>());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    
    public CommonEventBusWSPoster(EventBus eBus) {
        eBus.post(new JSONFrameHandler.AddRequestClassToFilterEvent(RegisterChannelEvent.class));
    }
    
    @Subscribe
    public void onObject(Object o) {
        if(o instanceof RegisterChannelEvent) {
            synchronized(channels) {
                channels.add(((RegisterChannelEvent)o).context.channel);
            }
        }
        else if(o instanceof PostToWebsocket) {
            executor.execute(() -> {
                postToActiveChannels(o);
            });
        }
    }

    private void postToActiveChannels(Object o) {
        try {
            //System.out.println("Posting to active channels: "+o.getClass());
            String text = MAPPER.writeValueAsString(new Object() {
                public String type = o.getClass().getName();
                public Object data = o;
            });
            
            Set<Channel> latchedChannels = new HashSet<>();
            synchronized(channels) {
                latchedChannels.addAll(channels);
            }
            
            for(Channel c : latchedChannels) {
                if(c != null && c.isWritable()) {
                    c.writeAndFlush(new TextWebSocketFrame(text));
                }
                else {
                    synchronized(channels) {
                        channels.remove(c);
                    }
                }
            }
        } catch (JsonProcessingException ex) {
            Logger.getLogger(CommonEventBusWSPoster.class.getName()).log(Level.SEVERE, "Serializing class: "+o.getClass(), ex);
        }
    }
    
    
    
    public static class RegisterChannelEvent implements RequestObject {
        private WebSocketContext context;
        
        public RegisterChannelEvent() {
        }

        @Override
        public void setContext(WebSocketContext c) {
            this.context = c;
        }
    }
    
    public interface PostToWebsocket {
        
    }
    
}
