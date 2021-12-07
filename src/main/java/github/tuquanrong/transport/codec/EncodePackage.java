package github.tuquanrong.transport.codec;

import github.tuquanrong.model.constant.PackageConstant;
import github.tuquanrong.model.dto.MessageDto;
import github.tuquanrong.model.enums.SerializerEnum;
import github.tuquanrong.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * tutu
 * 2021/1/6
 */
public class EncodePackage extends MessageToByteEncoder<MessageDto> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageDto messageDto, ByteBuf out) throws Exception {
        out.writeBytes(PackageConstant.MagicNumber);
        out.writerIndex(out.writerIndex() + 4);
        out.writeByte(PackageConstant.BetaVersion);
        out.writeByte(messageDto.getSerializationType());
        out.writeByte(messageDto.getMessageType());
        out.writeBytes(messageDto.getRequestId().getBytes());

        Serializer serializer = (Serializer) SerializerEnum
                .fromValue(messageDto.getSerializationType()).getDealClass().getMethod("getInstance").invoke(null);

        byte[] dataBytes = serializer.serialize(messageDto.getData());
        out.writeBytes(dataBytes);

        int packageLength = out.writerIndex();
        out.writerIndex(PackageConstant.MagicNumber.length);
        out.writeInt(packageLength);
        out.writerIndex(packageLength);

    }
}
