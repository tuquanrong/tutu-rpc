package github.tuquanrong.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * version:当前版本号为0001，表示当前包格式版本
 * serializationType:序列化算法类型
 * messageId:自增数据包id
 * messageType:包类型，请求包还是应答包还是心跳包都可以用这个表示
 * Object:数据
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MessageDto {
    private byte version;
    private byte serializationType;
    private byte messageType;
    private String requestId;
    private Object data;
}
