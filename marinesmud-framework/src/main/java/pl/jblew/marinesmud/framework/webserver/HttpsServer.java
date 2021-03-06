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
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpObjectAggregator;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;
import pl.jblew.marinesmud.framework.mod.io.netty.handler.codec.http.websocketx.WebSocketFrame;
import pl.jblew.marinesmud.framework.mod.io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import java.io.File;
import pl.jblew.marinesmud.framework.util.TwoTuple;
import pl.jblew.marinesmud.framework.webserver.modules.HttpErrorCodeException;

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
    private final SimpleChannelInboundHandler<WebSocketFrame> webSocketFrameHandler;
    
    public HttpsServer(WebServerConfig config, RoutingHttpResponder responder, StaticFileLoader fileLoader) {
        this(config, responder, fileLoader, null);
    }
    
    public HttpsServer(WebServerConfig config, RoutingHttpResponder responder, StaticFileLoader fileLoader, SimpleChannelInboundHandler<WebSocketFrame> webSocketFrameHandler) {
        this.responder = responder;
        this.config = config;
        this.fileLoader = fileLoader;
        this.webSocketFrameHandler = webSocketFrameHandler;
        
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
            SslContext sslCtx;
            if(config.useTemporarySelfSignedCertificate) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            }
            else {
                if(config.sslCertPemFile == null || config.sslKeyPemFile == null) throw new RuntimeException("SSL key files cannot be null when WebServerConfig.useTemporarySelfSignedCertificate=false");
                if(!new File(config.sslCertPemFile).canRead()|| !new File(config.sslKeyPemFile).canRead()) throw new RuntimeException("SSL key files must exist and be readable when WebServerConfig.useTemporarySelfSignedCertificate=false");
                
                if(config.sslPassword == null) {
                    sslCtx = SslContextBuilder.forServer(new File(config.sslCertPemFile), new File(config.sslKeyPemFile)).build();
                }
                else {
                    sslCtx = SslContextBuilder.forServer(new File(config.sslCertPemFile), new File(config.sslKeyPemFile), config.sslPassword).build();
                }
            }
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
            if (HttpsServer.this.webSocketFrameHandler != null) {
                p.addLast(new WebSocketServerProtocolHandler(usersManager, "/websocket", null, true));
            }
            p.addLast(new HttpServerHandler());
            if (HttpsServer.this.webSocketFrameHandler != null) {
                p.addLast(HttpsServer.this.webSocketFrameHandler);
            }


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
            FullHttpResponse response;
            if (HttpHeaders.is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }
            boolean keepAlive = HttpHeaders.isKeepAlive(req);
            
            try {
                String mimeType = "text/html";
                byte[] responseBytes;
                Path path = Paths.get(req.getUri().replace("..", ""));
                TwoTuple<HttpsSession, Cookie> resp = usersManager.parseCookies(req.headers().get(COOKIE));
                HttpsSession session = resp.a;
                Cookie newCookie = resp.b;
                
                if (fileLoader != null && path.getNameCount() > 1 && path.getName(0).toString().toLowerCase().equals("static")) { //static files
                    Path p = path.subpath(1, path.getNameCount());
                    responseBytes = fileLoader.loadFile(p);
                    mimeType = fileLoader.getMime(p);
                } else { //dynamic files
                    responseBytes = responder.getResponse(path, req, session);
                }
                
                response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(responseBytes));
                response.headers().set(CONTENT_TYPE, (mimeType == null ? "text/plain" : mimeType));
                response.headers().set(CONTENT_LENGTH, response.content().readableBytes());                
                response.headers().add(SET_COOKIE, ServerCookieEncoder.STRICT.encode(newCookie));
            } catch (HttpErrorCodeException ex) {
                System.out.println("Caught http error code exception: "+ex.httpCode);
                byte[] content = ("Error: " + ex.toString()).getBytes();
                response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(ex.httpCode), Unpooled.wrappedBuffer(content));
                response.headers().set(CONTENT_LENGTH, response.content().readableBytes());                
            }
            if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, Values.KEEP_ALIVE);
                ctx.write(response);
            }
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            System.out.println("Exception caught: "+cause.toString());
            cause.printStackTrace();
            ctx.close();
        }
    }
}
