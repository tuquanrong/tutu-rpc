package github.tuquanrong.transport.handler;

import github.tuquanrong.model.constant.PackageConstant;
import github.tuquanrong.model.dto.MessageDto;
import github.tuquanrong.model.dto.ResponseDto;
import github.tuquanrong.transport.UnDealMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 作用：解码之后将返回结果进行写入completeFuture
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private UnDealMessage unDealMessage;

    public NettyClientHandler() {
        unDealMessage = new UnDealMessage();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof MessageDto) {
            MessageDto messageDto = (MessageDto) msg;
            byte messageType = messageDto.getMessageType();
            if (messageType == PackageConstant.ResposnePackage) {
                ResponseDto<Object> responseDto = (ResponseDto<Object>) messageDto.getData();
                unDealMessage.dealRequestId(responseDto);
            }
        }
    }
}
