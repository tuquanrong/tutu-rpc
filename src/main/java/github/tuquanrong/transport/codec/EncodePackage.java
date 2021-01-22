package github.tuquanrong.transport.codec;

import github.tuquanrong.model.constant.PackageConstant;
import github.tuquanrong.model.dto.MessageDto;
import github.tuquanrong.model.dto.RequestDto;
import github.tuquanrong.serializer.ProtostuffSerializer;
import github.tuquanrong.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * tutu
 * 2021/1/6
 */
public class EncodePackage extends MessageToByteEncoder<MessageDto> {
    private static final AtomicInteger atomicInteger = new AtomicInteger(1);
    private Serializer serializer;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageDto messageDto, ByteBuf out) throws Exception {
        out.writeBytes(PackageConstant.MagicNumber);
        out.writerIndex(out.writerIndex() + 4);
        out.writeByte(PackageConstant.BetaVersion);
        out.writeByte(messageDto.getSerializationType());
        out.writeByte(messageDto.getMessageType());
        out.writeInt(atomicInteger.getAndIncrement());

        if (messageDto.getSerializationType() == PackageConstant.ProtostufSerializer) {
            serializer = ProtostuffSerializer.getInstance();
        }
        byte[] dataBytes = serializer.serialize(messageDto.getData());
        out.writeBytes(dataBytes);

        int packageLength = out.writerIndex();
        out.writerIndex(PackageConstant.MagicNumber.length);
        out.writeInt(packageLength);
        out.writerIndex(packageLength);

    }
}
