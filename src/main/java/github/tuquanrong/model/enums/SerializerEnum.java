package github.tuquanrong.model.enums;

import java.util.stream.Stream;

import github.tuquanrong.serializer.ProtostuffSerializer;
import lombok.Getter;

/**
 * @Author: tuquanrong
 * @Date: 2021/11/18 6:03 下午
 */
@Getter
public enum SerializerEnum {
    PROTOSTUF_SERIALIZER((byte) 1, ProtostuffSerializer.class);

    private byte type;
    private Class<?> dealClass;

    SerializerEnum(byte type, Class<?> dealClass) {
        this.type = type;
        this.dealClass = dealClass;
    }

    public static SerializerEnum fromValue(byte type) {
        return Stream.of(values())
                .filter(e -> e.getType() == type)
                .findFirst()
                .orElse(PROTOSTUF_SERIALIZER);
    }
}
