/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import io.netty.handler.codec.http.HttpServerCodec;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author teofil
 */
public class HttpRedirectingServer {
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final WebServerConfig config;
    
    public HttpRedirectingServer(WebServerConfig config) {
        this.config = config;
        // Configure the server.
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.ERROR))
                    .childHandler(new HttpServerInitializer());

            Channel ch = b.bind(config.httpPort).sync().channel();

            //System.err.println("Open your web browser and navigate to http://127.0.0.1:" + PORT + '/');
            //ch.closeFuture().sync();
        } catch (InterruptedException ex) {
            Logger.getLogger(HttpsServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

        }
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    private class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
        public HttpServerInitializer() {
        }

        @Override
        public void initChannel(SocketChannel ch) {
            ChannelPipeline p = ch.pipeline();

            p.addLast(new HttpServerCodec());
            p.addLast(new HttpServerHandler());
        }
    }

    private class HttpServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
            if (msg instanceof HttpRequest) {
                HttpRequest req = (HttpRequest) msg;
                
                String movedUrl = "https://"+config.domain+":"+config.httpsPort+"/";

                if (HttpHeaders.is100ContinueExpected(req)) {
                    ctx.write(new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.MOVED_PERMANENTLY));
                }
                boolean keepAlive = HttpHeaders.isKeepAlive(req);
                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.MOVED_PERMANENTLY, Unpooled.wrappedBuffer(new byte [] {}));
                response.headers().set(HttpHeaders.Names.LOCATION, movedUrl);

                if (!keepAlive) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                    ctx.write(response);
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
    
    
}
