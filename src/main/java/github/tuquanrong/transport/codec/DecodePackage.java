package github.tuquanrong.transport.codec;

import github.tuquanrong.exception.RpcServerException;
import github.tuquanrong.model.constant.PackageConstant;
import github.tuquanrong.model.dto.MessageDto;
import github.tuquanrong.model.dto.RequestDto;
import github.tuquanrong.model.dto.ResponseDto;
import github.tuquanrong.model.enums.RpcServerStatusEnum;
import github.tuquanrong.model.enums.SerializerEnum;
import github.tuquanrong.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * tutu
 * 2021/1/6
 */
public class DecodePackage extends LengthFieldBasedFrameDecoder {

    public DecodePackage() {
        this(PackageConstant.PackageMaxLength, 4, 4, -8, 0);
    }

    /**
     * @param maxFrameLength      最大包长度
     * @param lengthFieldOffset   长度字段偏移量
     * @param lengthFieldLength   长度域长度
     * @param lengthAdjustment    长度修正 -(长度域偏移量+长度域长度)
     * @param initialBytesToStrip 为0保留header，其他值进行进行跳过处理
     */
    public DecodePackage(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object message = super.decode(ctx, in);
        if (message instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) message;
            if (byteBuf.readableBytes() >= PackageConstant.HeaderLength) {
                return decodePackage(byteBuf);
            }
        }
        return message;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public Object decodePackage(ByteBuf in) throws Exception {
        byte[] magicNumber = new byte[4];
        in.readBytes(magicNumber);
        for (int i = 0; i < 4; i++) {
            if (magicNumber[i] != PackageConstant.MagicNumber[i]) {
                throw new RpcServerException(RpcServerStatusEnum.PROCOCOL_NOT_SUPPORT);
            }
        }
        int packageLength = in.readInt();
        byte version = in.readByte();
        byte serializationType = in.readByte();
        byte messageType = in.readByte();
        byte[] requestIdBytes = new byte[36];
        in.readBytes(requestIdBytes);
        String requestId = new String(requestIdBytes);
        int dataLength = packageLength - PackageConstant.HeaderLength;
        byte[] dataBytes = new byte[dataLength];
        in.readBytes(dataBytes);
        MessageDto messageDto = new MessageDto();
        messageDto.setVersion(version);
        messageDto.setSerializationType(serializationType);
        messageDto.setRequestId(requestId);
        messageDto.setMessageType(messageType);
        Serializer serializer = (Serializer) SerializerEnum
                .fromValue(messageDto.getSerializationType()).getDealClass().getMethod("getInstance").invoke(null);
        if (messageType == PackageConstant.RequestPackage) {
            messageDto.setData(serializer.deserialize(dataBytes, RequestDto.class));
        } else if (messageType == PackageConstant.ResposnePackage) {
            messageDto.setData(serializer.deserialize(dataBytes, ResponseDto.class));
        }
        return messageDto;
    }

}
