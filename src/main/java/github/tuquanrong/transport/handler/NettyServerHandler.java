package github.tuquanrong.transport.handler;

import github.tuquanrong.model.constant.PackageConstant;
import github.tuquanrong.model.dto.MessageDto;
import github.tuquanrong.model.dto.RequestDto;
import github.tuquanrong.model.dto.ResponseDto;
import github.tuquanrong.proxy.ServerProxy;
import github.tuquanrong.util.MessageBuilder;
import github.tuquanrong.util.ResponseBuilder;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * tutu
 * 2021/1/6
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<MessageDto> {
    private ServerProxy serverProxy;

    public NettyServerHandler() {
        serverProxy = ServerProxy.getInstance();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageDto messageDto) throws Exception {
        System.out.println("server" + messageDto);
        MessageDto responseMessage = new MessageDto();
        if (messageDto.getMessageType() == PackageConstant.RequestPackage) {
            RequestDto requestDto = (RequestDto) messageDto.getData();
            System.out.println("invoke");
            Object data = serverProxy.invoke(requestDto); //通过requestDto中的参数调用函数
            System.out.println(data);
            ResponseDto responseDto = ResponseBuilder.success(requestDto.getRequestId(), data);
            System.out.println(responseDto);
            responseMessage = MessageBuilder.genarateResponseMessage(messageDto, responseDto);
        }
        System.out.println(responseMessage);
        channelHandlerContext.channel().writeAndFlush(responseMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }
}
