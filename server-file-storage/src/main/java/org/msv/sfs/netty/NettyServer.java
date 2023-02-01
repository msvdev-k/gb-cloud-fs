package org.msv.sfs.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class NettyServer {

    public static void main(String[] args) {
        new NettyServer();
    }


    // Конфигурация сервера
    private final ServerConfiguration config;


    /**
     * Основной конструктор запускающий работу сервера
     */
    public NettyServer() {

        config = new ServerConfiguration();

        EventLoopGroup auth = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();


        try {

            if (!config.getAuthenticationService().start()) {
                throw new RuntimeException("Authentication service not started!!");
            }

            ServerBootstrap server = new ServerBootstrap();
            server.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    new AuthenticationInboundHandler(config),
                                    new FileServerInboundHandler()
                            );
                        }
                    });

            ChannelFuture future = server.bind(config.getPort()).sync();
            log.debug("Server is ready");

            future.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();

            config.getAuthenticationService().stop();
        }
    }

}
