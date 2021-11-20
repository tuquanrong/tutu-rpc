package github.tuquanrong.transport;

import java.net.InetAddress;
import java.net.UnknownHostException;

import github.tuquanrong.register.ZkShutdownHook;
import github.tuquanrong.transport.codec.DecodePackage;
import github.tuquanrong.transport.codec.EncodePackage;
import github.tuquanrong.transport.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class NettyServer {
    private static int port = 9085;
    private ServerBootstrap serverBootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private DefaultEventExecutorGroup eventExecutorGroup;

    public NettyServer() {
        serverBootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup();
        eventExecutorGroup = new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors() * 2);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public void start() {
        try {
            ZkShutdownHook.clearServiceNode();
            String ip = InetAddress.getLocalHost().getHostAddress();
            serverBootstrap
                    .group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline channelPipeline = socketChannel.pipeline();
                            channelPipeline.addLast(new DecodePackage());
                            channelPipeline.addLast(eventExecutorGroup, new NettyServerHandler());
                            channelPipeline.addLast(new EncodePackage());
                        }
                    });
            System.out.println(ip + "  " + port);
            ChannelFuture channelFuture = bind(serverBootstrap, ip, port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (UnknownHostException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            eventExecutorGroup.shutdownGracefully();
        }
    }

    private ChannelFuture bind(ServerBootstrap nowServerBootstrap, String ip, int nowPort) {
        return nowServerBootstrap.bind(ip, nowPort).addListener(new GenericFutureListener<Future<? super Void>>() {
            public void operationComplete(Future<? super Void> future) {
                if (future.isSuccess()) {
                    System.out.println("端口[" + nowPort + "]绑定成功!");
                    port = nowPort;
                } else {
                    System.err.println("端口[" + nowPort + "]绑定失败!");
                    bind(nowServerBootstrap, ip, nowPort + 1);
                }
            }
        });
    }

    public static int getPort() {
        return port;
    }
}
