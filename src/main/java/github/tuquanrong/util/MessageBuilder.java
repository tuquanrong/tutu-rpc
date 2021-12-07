package github.tuquanrong.util;

import java.util.UUID;

import github.tuquanrong.config.PropertiesConfig;
import github.tuquanrong.config.model.PropertiesEnum;
import github.tuquanrong.model.constant.PackageConstant;
import github.tuquanrong.model.dto.MessageDto;
import github.tuquanrong.model.dto.RequestDto;
import github.tuquanrong.model.dto.ResponseDto;
import github.tuquanrong.model.enums.SerializerEnum;

/**
 * @Author: tuquanrong
 * @Date: 2021/11/17 8:38 下午
 */
public class MessageBuilder {

    public static MessageDto genarateRequestMessage(RequestDto requestDto) {
        MessageDto messageDto = new MessageDto();
        messageDto.setVersion(PackageConstant.BetaVersion);
        messageDto.setMessageType(PackageConstant.RequestPackage);
        String serializerConfig = PropertiesConfig.getInstance().get(PropertiesEnum.SERIALIZER);
        byte serializerType = Byte.valueOf(serializerConfig);
        if (SerializerEnum.fromValue(serializerType) == null) {
            serializerType = Byte.valueOf(PropertiesConfig.getInstance().getDefault(PropertiesEnum.SERIALIZER));
        }
        messageDto.setSerializationType(serializerType);
        messageDto.setRequestId(UUID.randomUUID().toString());
        messageDto.setData(requestDto);
        return messageDto;
    }

    public static MessageDto genarateResponseMessage(MessageDto requestMessageDto, ResponseDto responseDto) {
        MessageDto responseMessageDto = new MessageDto();
        responseMessageDto.setVersion(requestMessageDto.getVersion());
        responseMessageDto.setSerializationType(requestMessageDto.getSerializationType());
        responseMessageDto.setMessageType(PackageConstant.ResposnePackage);
        responseMessageDto.setRequestId(UUID.randomUUID().toString());
        responseMessageDto.setData(responseDto);
        return responseMessageDto;
    }
}
