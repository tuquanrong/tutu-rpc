package github.tuquanrong.transport.codec;

import github.tuquanrong.exception.RpcException;
import github.tuquanrong.model.constant.PackageConstant;
import github.tuquanrong.model.dto.MessageDto;
import github.tuquanrong.model.dto.RequestDto;
import github.tuquanrong.model.dto.ResponseDto;
import github.tuquanrong.serializer.ProtostuffSerializer;
import github.tuquanrong.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * tutu
 * 2021/1/7
 */
public class DecodePackageTest {
    private static final AtomicInteger atomicInteger = new AtomicInteger(1);

    public static void main(String[] args) {
        ByteBuf in = getByteBuf();
        System.out.println(in.readableBytes());
        byte[] magicNumber = new byte[4];
        in.readBytes(magicNumber);
        for (int i = 0; i < 4; i++) {
            if (magicNumber[i] != PackageConstant.MagicNumber[i]) {
                throw new RpcException("不是当前协议的包");
            }
        }
        int packageLength = in.readInt();
        byte version = in.readByte();
        byte serializationType = in.readByte();
        byte messageType = in.readByte();
        int requestId = in.readInt();
        System.out.println(in.readableBytes());
        System.out.println(packageLength);
        System.out.println(PackageConstant.HeaderLength);

        int dataLength = packageLength - PackageConstant.HeaderLength;
        byte[] dataBytes = new byte[dataLength];
        in.readBytes(dataBytes);
        MessageDto messageDto = new MessageDto();
        messageDto.setVersion(version);
        messageDto.setSerializationType(serializationType);
        messageDto.setRequestId(requestId);
        messageDto.setMessageType(messageType);
        Serializer serializer = new ProtostuffSerializer();
        if (messageType == PackageConstant.RequestPackage) {
            messageDto.setData(serializer.deserialize(dataBytes, RequestDto.class));
        } else if (messageType == PackageConstant.ResposnePackage) {
            messageDto.setData(serializer.deserialize(dataBytes, ResponseDto.class));
        }
        System.out.println(messageDto);
    }

    public static ByteBuf getByteBuf() {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer(100, 1000);
        MessageDto messageDto = new MessageDto();
        messageDto.setMessageType(PackageConstant.RequestPackage);
        messageDto.setSerializationType(PackageConstant.ProtostufSerializer);
        messageDto.setVersion(PackageConstant.BetaVersion);
        RequestDto requestDto = new RequestDto();
        requestDto.setInterfaceName("tutututu");
        messageDto.setData(requestDto);


        out.writeBytes(PackageConstant.MagicNumber);
        out.writerIndex(out.writerIndex() + 4);
        out.writeByte(PackageConstant.BetaVersion);
        out.writeByte(messageDto.getSerializationType());
        out.writeByte(messageDto.getMessageType());
        out.writeInt(atomicInteger.getAndIncrement());

        Serializer serializer = null;
        if (messageDto.getSerializationType() == PackageConstant.ProtostufSerializer) {
            serializer = new ProtostuffSerializer();
        }
        byte[] dataBytes = serializer.serialize(messageDto.getData());
        int dataLength = dataBytes.length;
        out.writeBytes(dataBytes);

        int packageLength = out.writerIndex();
        out.writerIndex(PackageConstant.MagicNumber.length);
        out.writeInt(packageLength);
        out.writerIndex(packageLength);
        return out;
    }
}
