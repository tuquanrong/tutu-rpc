package github.tuquanrong.transport.handler;

import github.tuquanrong.model.constant.PackageConstant;
import github.tuquanrong.model.dto.MessageDto;
import github.tuquanrong.model.dto.RequestDto;
import github.tuquanrong.model.dto.ResponseDto;
import github.tuquanrong.proxy.ServerProxy;
import github.tuquanrong.util.ResponseState;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * tutu
 * 2021/1/6
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private ServerProxy serverProxy;

    public NettyServerHandler() {
        serverProxy = ServerProxy.getInstance();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof MessageDto) {
                MessageDto messageDto = (MessageDto) msg;
                MessageDto responseMessage = new MessageDto();
                if (messageDto.getMessageType() == PackageConstant.RequestPackage) {
                    RequestDto requestDto = (RequestDto) messageDto.getData();
                    Object data = serverProxy.invoke(requestDto);//通过requestDto中的参数调用函数
                    responseMessage.setVersion(messageDto.getVersion());
                    responseMessage.setSerializationType(messageDto.getSerializationType());
                    responseMessage.setMessageType(PackageConstant.ResposnePackage);
                    ResponseDto responseDto = ResponseState.success(requestDto.getRequestId(), data);
                    responseMessage.setData(responseDto);
                }
                System.out.println(responseMessage);
                ctx.writeAndFlush(responseMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
