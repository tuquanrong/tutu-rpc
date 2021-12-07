package github.tuquanrong.config;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreaker;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.EventObserverRegistry;
import com.alibaba.csp.sentinel.util.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import github.tuquanrong.config.model.DegradeRuleModel;
import github.tuquanrong.config.model.PropertiesEnum;
import github.tuquanrong.register.ServerRegisterLogout;
import lombok.SneakyThrows;

/**
 * @Author: tuquanrong
 * @Date: 2021/12/1 7:49 下午
 */
@Configuration
public class DegradeControlConfig {
    private static final Logger logger = LoggerFactory.getLogger(DegradeControlConfig.class);

    DegradeControlConfig() {
        try {
            List<DegradeRuleModel> list = new Gson().fromJson(PropertiesConfig.getInstance()
                    .get(PropertiesEnum.DEGRADERULE), new TypeToken<List<DegradeRuleModel>>() {
            }.getType());
            addDegradeRule(list);
            addListener();
        } catch (Exception e) {
            logger.error("熔断配置错误");
        }
    }

    public void addDegradeRule(List<DegradeRuleModel> degradeRuleModelList) {
        List<DegradeRule> degradeRuleList = new LinkedList<>();
        degradeRuleModelList.stream().forEach(degradeRuleModel -> {
            DegradeRule rule = new DegradeRule();
            rule.setResource(degradeRuleModel.getResource());
            rule.setGrade(CircuitBreakerStrategy.ERROR_COUNT.getType());
            rule.setCount(degradeRuleModel.getCount());
            rule.setTimeWindow(degradeRuleModel.getTimeWindow());
            rule.setMinRequestAmount(degradeRuleModel.getMinRequestAmount());
            rule.setStatIntervalMs(degradeRuleModel.getStatIntervalMs());
            degradeRuleList.add(rule);
        });
        DegradeRuleManager.loadRules(degradeRuleList);
    }

    public void addListener() {
        EventObserverRegistry.getInstance().addStateChangeObserver("register",
                (prevState, newState, rule, snapshotValue) -> {
                    logger.error("熔断状态{} {}", prevState, newState);
                    if (newState == CircuitBreaker.State.OPEN) {
                        new Thread(new Runnable() {
                            @SneakyThrows
                            @Override
                            public void run() {
                                boolean circleFlag = Boolean.TRUE;
                                while (circleFlag) {
                                    Entry entry = null;
                                    try {
                                        circleFlag = Boolean.FALSE;
                                        entry = SphU.entry(rule.getResource(), EntryType.IN, 1);
                                    } catch (DegradeException e) {
                                        logger.error("异常太多熔断了 {}");
                                        circleFlag = Boolean.TRUE;
                                    } catch (BlockException blockException) {
                                        logger.error("sentinel错误了");
                                    } catch (Exception exception) {
                                        Tracer.trace(exception);
                                    } finally {
                                        if (entry != null) {
                                            entry.exit();
                                        }
                                    }
                                    Thread.sleep(1000);
                                }
                                logger.error("熔断outCircle");
                            }
                        }).start();
                        logger.error(String.format("resouceName=%s 熔断生效 at%d, snapshotValue=%.2f", rule.getResource(),
                                TimeUtil.currentTimeMillis(), snapshotValue));
                        ServerRegisterLogout.getInstance().logoutServer(rule.getResource());
                    } else {
                        logger.error(String.format("resouceName=%s 熔断恢复探测注册节点 at %d", rule.getResource(),
                                TimeUtil.currentTimeMillis()));
                        ServerRegisterLogout.getInstance().registerServer(rule.getResource());
                    }
                });
    }
}
