package github.tuquanrong.config;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import github.tuquanrong.config.model.FlowRuleModel;
import github.tuquanrong.config.model.PropertiesEnum;

/**
 * @Author: tuquanrong
 * @Date: 2021/11/30 4:51 下午
 */
@Configuration
public class FlowControlConfig {
    private static final Logger logger = LoggerFactory.getLogger(FlowControlConfig.class);

    FlowControlConfig() {
        //加载流量控制
        try {
            List<FlowRuleModel> list = new Gson().fromJson(PropertiesConfig.getInstance()
                    .get(PropertiesEnum.FLOWRULE), new TypeToken<List<FlowRuleModel>>() {
            }.getType());
            addFlowControl(list);
        } catch (Exception e) {
            logger.error("flowRule配置格式有误");
        }
    }

    private void addFlowControl(List<FlowRuleModel> flowRuleModelList) {
        List<FlowRule> flowRuleList = new LinkedList<>();
        flowRuleModelList.stream().forEach(flowRuleModel -> {
            FlowRule rule = new FlowRule();
            rule.setResource(flowRuleModel.getResource());
            rule.setGrade(RuleConstant.FLOW_GRADE_QPS); //QPS控制
            rule.setCount(flowRuleModel.getQps());
            rule.setLimitApp(flowRuleModel.getLimitApp());
            rule.setStrategy(RuleConstant.STRATEGY_DIRECT); //根据调用方限流
            rule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT); //直接拒绝
            flowRuleList.add(rule);
        });
        FlowRuleManager.loadRules(flowRuleList);
    }
}
