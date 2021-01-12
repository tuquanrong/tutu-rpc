package github.tuquanrong.transport;

import github.tuquanrong.exception.RpcException;
import github.tuquanrong.model.constant.PackageConstant;
import github.tuquanrong.model.dto.MessageDto;
import github.tuquanrong.model.dto.RequestDto;
import github.tuquanrong.model.dto.ResponseDto;
import github.tuquanrong.transport.codec.DecodePackage;
import github.tuquanrong.transport.codec.EncodePackage;
import github.tuquanrong.transport.handler.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class NettyClient {
    private Bootstrap bootstrap;

    private Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    NettyClient() {
        bootstrap = new Bootstrap();
        EventLoopGroup eventGroup = new NioEventLoopGroup();
        bootstrap
                .group(eventGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new DecodePackage());//1
                        socketChannel.pipeline().addLast(new NettyClientHandler());//2
                        socketChannel.pipeline().addLast(new EncodePackage());//3
                    }
                });
    }

    public CompletableFuture sendMessage(RequestDto requestDto) {
        CompletableFuture<ResponseDto> completableFuture = new CompletableFuture<>();
        InetSocketAddress inetSocketAddress = getInet();
        Channel channel = getChannel(inetSocketAddress);
        if (channel.isActive()) {
            UnDealMessage.setRequestId(requestDto.getRequestId(), completableFuture);
            MessageDto messageDto = new MessageDto();
            messageDto.setVersion(PackageConstant.BetaVersion);
            messageDto.setMessageType(PackageConstant.RequestPackage);
            messageDto.setSerializationType(PackageConstant.ProtostufSerializer);
            messageDto.setData(requestDto);
            channel.writeAndFlush(messageDto).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    System.out.println("请求成功" + messageDto.toString());
                } else {
                    System.out.println("请求失败" + messageDto.toString());
                    future.channel().close();
                    completableFuture.completeExceptionally(future.cause());
                }
            });
        } else {
            throw new RpcException("该ip+port通道被关闭");
        }
        return completableFuture;
    }

    public InetSocketAddress getInet() {
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int port = 9085;
        return new InetSocketAddress(ip, port);
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelMap.get(inetSocketAddress.toString());
        try {
            if (channel == null) {
                CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
                bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        System.out.println("客户端连接成功" + inetSocketAddress.toString());
                        completableFuture.complete(future.channel());
                    } else {
                        throw new RpcException("客户端连接错误" + inetSocketAddress.toString());
                    }
                });
                channel = completableFuture.get();
                channelMap.put(inetSocketAddress.toString(), channel);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return channel;
    }

}
