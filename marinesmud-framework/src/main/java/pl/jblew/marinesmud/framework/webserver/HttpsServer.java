/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpObjectAggregator;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;
import pl.jblew.marinesmud.framework.mod.io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import pl.jblew.marinesmud.framework.mod.io.netty.handler.codec.http.websocketx.WebSocketFrame;
import pl.jblew.marinesmud.framework.mod.io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import pl.jblew.marinesmud.framework.mod.io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import pl.jblew.marinesmud.framework.mod.io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.CharsetUtil;
import pl.jblew.marinesmud.framework.event.ListenersManager;

/**
 *
 * @author teofil
 */
public class HttpsServer {
    private final HttpsUsersManager usersManager;
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final RoutingHttpResponder responder;
    private final StaticFileLoader fileLoader;
    private final WebServerConfig config;

    public HttpsServer(WebServerConfig config, RoutingHttpResponder responder, StaticFileLoader fileLoader) {
        this.responder = responder;
        this.config = config;
        this.fileLoader = fileLoader;
        
        usersManager = new HttpsUsersManager(config);

        SslContext sslContext = loadSSLContext(config);

        // Configure the server.
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.ERROR))
                    .childHandler(new HttpServerInitializer(sslContext));

            Channel ch = b.bind(config.httpsPort).sync().channel();

        } catch (InterruptedException ex) {
            Logger.getLogger(HttpsServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

        }
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    private SslContext loadSSLContext(WebServerConfig config) {
        try {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            return sslCtx;
        } catch (CertificateException ex) {
            Logger.getLogger(HttpsServer.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (SSLException ex) {
            Logger.getLogger(HttpsServer.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    private class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
        private final SslContext sslCtx;

        public HttpServerInitializer(SslContext sslCtx) {
            this.sslCtx = sslCtx;
        }

        @Override
        public void initChannel(SocketChannel ch) {
            ChannelPipeline p = ch.pipeline();
            if (sslCtx != null) {
                p.addLast(sslCtx.newHandler(ch.alloc()));
            }
            p.addLast(new HttpServerCodec());
            p.addLast(new HttpObjectAggregator(65536));
            p.addLast(new WebSocketServerProtocolHandler(usersManager, "/websocket", null, true));
            p.addLast(new HttpServerHandler());
            p.addLast(new WebSocketFrameHandler());
            

            /*
            From file server handler: 
            p.addLast(
                             new StringEncoder(CharsetUtil.UTF_8),
                             new LineBasedFrameDecoder(8192),
                             new StringDecoder(CharsetUtil.UTF_8),
                             new ChunkedWriteHandler(),
                             new FileServerHandler());
             */
        }
    }

    private class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
                String mimeType = "text/html";
                byte[] responseBytes;
                Path path = Paths.get(req.getUri().replace("..", ""));

                if (fileLoader != null && path.getNameCount() > 1 && path.getName(0).toString().toLowerCase().equals("static")) { //static files
                    Path p = path.subpath(1, path.getNameCount());
                    responseBytes = fileLoader.loadFile(p);
                    mimeType = fileLoader.getMime(p);
                    System.out.println("Mime: " + mimeType);
                } else { //dynamic files
                    responseBytes = responder.getResponse(path, req).getBytes("UTF-8");
                }

                if (HttpHeaders.is100ContinueExpected(req)) {
                    ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
                }
                boolean keepAlive = HttpHeaders.isKeepAlive(req);
                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(responseBytes));
                response.headers().set(CONTENT_TYPE, (mimeType == null? "text/plain" : mimeType));
                response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

                HttpsUser user = usersManager.parseCookies(req.headers().get(COOKIE), response.headers());
                
                if (!keepAlive) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, Values.KEEP_ALIVE);
                    ctx.write(response);
                }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }

    private static class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame>{
        public WebSocketFrameHandler() {
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
            // ping and pong frames already handled

            if (frame instanceof TextWebSocketFrame) {
                // Send the uppercase string back.
                String request = ((TextWebSocketFrame) frame).text();
                
                String lastMSg = "---";
                
                HttpsUser user = ctx.channel().attr(HttpsUser.WSCHANNEL_USER_ATTR).get();
                if(user != null) {
                    lastMSg = (String) user.getProperty("lastMsg");
                    user.setProperty("lastMsg", request);
                }
                
                Logger.getLogger(HttpsServer.class.getName()).log(Level.INFO, "{0} received {1}", new Object[]{ctx.channel(), request});
                ctx.channel().writeAndFlush(new TextWebSocketFrame(request.toUpperCase()+"; lastMSg="+lastMSg));
            } else {
                String message = "unsupported frame type: " + frame.getClass().getName();
                throw new UnsupportedOperationException(message);
            }
        }
    }
}
