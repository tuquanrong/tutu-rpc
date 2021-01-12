package github.tuquanrong.transport;

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

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NettyServer {
    private final int port = 9085;
    private ServerBootstrap serverBootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private DefaultEventExecutorGroup eventExecutorGroup;

    NettyServer() {
        serverBootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup();
        eventExecutorGroup = new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors() * 2);
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
                        channelPipeline.addLast(new EncodePackage());
                        channelPipeline.addLast(new DecodePackage());
                        channelPipeline.addLast(eventExecutorGroup, new NettyServerHandler());
                    }
                });
    }

    public void start() {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            ChannelFuture channelFuture = serverBootstrap.bind(ip, port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (UnknownHostException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            eventExecutorGroup.shutdownGracefully();
        }
    }
}
