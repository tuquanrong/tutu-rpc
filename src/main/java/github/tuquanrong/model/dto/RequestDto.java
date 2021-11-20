package github.tuquanrong.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
