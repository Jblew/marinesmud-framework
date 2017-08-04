/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.websockets;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.jblew.marinesmud.framework.mod.io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import pl.jblew.marinesmud.framework.mod.io.netty.handler.codec.http.websocketx.WebSocketFrame;
import pl.jblew.marinesmud.framework.webserver.HttpsServer;
import pl.jblew.marinesmud.framework.webserver.HttpsSession;

/**
 *
 * @author teofil
 */
@Sharable
public abstract class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame>{
        public WebSocketFrameHandler() {
        }
        
        public abstract void processText(HttpsSession session, Channel channel, String text);

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
            // ping and pong frames already handled

            if (frame instanceof TextWebSocketFrame) {
                String text = ((TextWebSocketFrame) frame).text();                
                HttpsSession session = ctx.channel().attr(HttpsSession.WSCHANNEL_SESSION_ATTR).get();
                if(session != null) {
                    processText(session, ctx.channel(), text);
                }
                else Logger.getLogger(HttpsServer.class.getName()).log(Level.SEVERE, "Session is null in WebSocketFrame!");
            } else {
                String message = "unsupported frame type: " + frame.getClass().getName();
                throw new UnsupportedOperationException(message);
            }
        }
    }