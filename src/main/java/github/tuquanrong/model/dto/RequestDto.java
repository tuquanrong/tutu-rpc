package github.tuquanrong.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RequestDto {
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Class<?>[] methodParamType;
    private Object[] params;
}
