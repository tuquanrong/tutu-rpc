package github.tuquanrong.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: tuquanrong
 * @Date: 2021/11/24 6:52 下午
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FlowRuleModel {
    private String resource;
    private int qps;
    private String limitApp;
}
