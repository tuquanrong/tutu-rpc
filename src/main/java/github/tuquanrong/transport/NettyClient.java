package github.tuquanrong.transport;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import github.tuquanrong.exception.RpcServerException;
import github.tuquanrong.model.dto.MessageDto;
import github.tuquanrong.model.dto.RequestDto;
import github.tuquanrong.model.dto.ResponseDto;
import github.tuquanrong.model.enums.RpcServerStatusEnum;
import github.tuquanrong.register.ServerDiscover;
import github.tuquanrong.transport.codec.DecodePackage;
import github.tuquanrong.transport.codec.EncodePackage;
import github.tuquanrong.transport.handler.NettyClientHandler;
import github.tuquanrong.util.MessageBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static final NettyClient NETTY_CLIENT = new NettyClient();
    private static final Integer CONNECT_TIMEOUT = 5000;
    private Bootstrap bootstrap;
    private Map<String, Channel> channelMap = new ConcurrentHashMap<>();
    private UnDealMessage unDealMessage;
    private ServerDiscover serverDiscover;

    public static NettyClient getInstance() {
        return NETTY_CLIENT;
    }

    public NettyClient() {
        unDealMessage = UnDealMessage.getInstance();
        serverDiscover = ServerDiscover.getInstance();
        bootstrap = new Bootstrap();
        EventLoopGroup eventGroup = new NioEventLoopGroup();
        bootstrap
                .group(eventGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new DecodePackage());
                        socketChannel.pipeline().addLast(new NettyClientHandler());
                        socketChannel.pipeline().addLast(new EncodePackage());
                    }
                });
    }

    public CompletableFuture sendMessage(RequestDto requestDto) {
        CompletableFuture<ResponseDto> completableFuture = new CompletableFuture<>();

        InetSocketAddress inetSocketAddress = serverDiscover.discoverServer(requestDto.getInterfaceName());
        Channel channel = getChannel(inetSocketAddress);
        if (channel.isActive()) {
            unDealMessage.setRequestId(requestDto.getRequestId(), completableFuture);
            MessageDto messageDto = MessageBuilder.genarateRequestMessage(requestDto);
            channel.writeAndFlush(messageDto).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    logger.info("请求成功" + messageDto.toString());
                } else {
                    logger.info("请求失败" + messageDto.toString());
                    future.channel().close();
                    completableFuture.completeExceptionally(future.cause());
                }
            });
        } else {
            throw new RpcServerException(RpcServerStatusEnum.TRANSPORT_CLOSED);
        }
        return completableFuture;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelMap.get(inetSocketAddress.toString());
        try {
            if (channel == null) {
                CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
                bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        logger.info("客户端链接成功" + inetSocketAddress.toString());
                        completableFuture.complete(future.channel());
                    } else {
                        throw new RpcServerException(RpcServerStatusEnum.CONNECT_ERROR);
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
