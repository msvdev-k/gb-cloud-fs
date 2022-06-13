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
import org.msv.sfs.authentication.AuthenticationService;
import org.msv.sfs.authentication.SQLiteAuthService;


@Slf4j
public class NettyServer {

    private final AuthenticationService authenticationService;

    public static void main(String[] args) {

        new NettyServer();

    }

    private static final int PORT = 8189;


    public NettyServer() {

        authenticationService = new SQLiteAuthService();

        EventLoopGroup auth = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        try {

            if (!authenticationService.start()) {
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
                                    new FileServerHandler(authenticationService)
                            );
                        }
                    });

            ChannelFuture future = server.bind(PORT).sync();
            log.debug("Server is ready");

            future.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();

            authenticationService.stop();
        }
    }

}
