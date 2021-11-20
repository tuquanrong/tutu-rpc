package github.tuquanrong.transport.codec;

import github.tuquanrong.model.constant.PackageConstant;
import github.tuquanrong.model.dto.MessageDto;
import github.tuquanrong.model.dto.RequestDto;
import github.tuquanrong.model.enums.SerializerEnum;
import github.tuquanrong.serializer.ProtostuffSerializer;
import github.tuquanrong.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * tutu
 * 2021/1/6
 */
public class EncodePackageTest {
    private static final AtomicInteger atomicInteger = new AtomicInteger(1);
    public static void main(String[] argv) throws UnsupportedEncodingException {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer(100, 1000);
        MessageDto messageDto=new MessageDto();
        messageDto.setMessageType(PackageConstant.RequestPackage);
        messageDto.setSerializationType(SerializerEnum.PROTOSTUF_SERIALIZER.getType());
        messageDto.setVersion(PackageConstant.BetaVersion);
        RequestDto requestDto=new RequestDto();
        requestDto.setInterfaceName("tutututu");
        messageDto.setData(requestDto);


        out.writeBytes(PackageConstant.MagicNumber);
        out.writerIndex(out.writerIndex() + 4);
        out.writeByte(PackageConstant.BetaVersion);
        out.writeByte(messageDto.getSerializationType());
        out.writeByte(messageDto.getMessageType());
        out.writeInt(atomicInteger.getAndIncrement());
        System.out.println(out);

        Serializer serializer = null;
        if (messageDto.getSerializationType() == SerializerEnum.PROTOSTUF_SERIALIZER.getType()) {
            serializer = new ProtostuffSerializer();
        }
        byte[] dataBytes = serializer.serialize(messageDto.getData());
        int dataLength=dataBytes.length;
        System.out.println(dataLength);
        out.writeBytes(dataBytes);

        int packageLength = out.writerIndex();
        out.writerIndex(PackageConstant.MagicNumber.length);
        out.writeInt(dataLength);
        out.writerIndex(packageLength);

        byte[] readBytes=new byte[out.readableBytes()];
        out.readBytes(readBytes);
        System.out.println(Arrays.toString(readBytes));
    }
}
