package github.tuquanrong.transport.handler;

import github.tuquanrong.model.constant.PackageConstant;
import github.tuquanrong.model.dto.MessageDto;
import github.tuquanrong.model.dto.ResponseDto;
import github.tuquanrong.transport.UnDealMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 作用：解码之后将返回结果进行写入completeFuture
 * 提供异步处理消息题能力
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<MessageDto> {
    private UnDealMessage unDealMessage;

    public NettyClientHandler() {
        unDealMessage = UnDealMessage.getInstance();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageDto messageDto) throws Exception {
        System.out.println("clientRead");
        byte messageType = messageDto.getMessageType();
        System.out.println(messageType);
        if (messageType == PackageConstant.ResposnePackage) {
            ResponseDto<Object> responseDto = (ResponseDto<Object>) messageDto.getData();
            System.out.println(responseDto);
            unDealMessage.dealRequestId(responseDto);
        }
    }
}
