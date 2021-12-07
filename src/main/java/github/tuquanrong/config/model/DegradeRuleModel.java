package github.tuquanrong.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: tuquanrong
 * @Date: 2021/12/1 7:45 下午
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DegradeRuleModel {
    private String resource; //资源名
    private Double count; //异常比例
    private Integer timeWindow; //暂停时间(秒)
    private Integer minRequestAmount; //最小触发请求数
    private Integer statIntervalMs; //最小收集请求时间
}
