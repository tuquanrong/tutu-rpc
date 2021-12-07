package github.tuquanrong.proxy;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;

import github.tuquanrong.exception.RpcServerException;
import github.tuquanrong.model.dto.RequestDto;
import github.tuquanrong.model.dto.ResponseDto;
import github.tuquanrong.model.enums.RpcServerStatusEnum;
import github.tuquanrong.register.ServiceBeans;
import github.tuquanrong.util.ResponseBuilder;

/**
 * tutu
 * 2021/1/13
 */
public class ServerProxy {
    private static final Logger logger = LoggerFactory.getLogger(ServerProxy.class);
    private static final ServerProxy SERVER_PROXY = new ServerProxy();
    private ServiceBeans serviceBeans;

    public ServerProxy() {
        serviceBeans = ServiceBeans.getInstance();
    }

    public static ServerProxy getInstance() {
        return SERVER_PROXY;
    }

    public ResponseDto invoke(RequestDto requestDto) {
        ResponseDto responseDto = null;
        Object data = null;
        Entry entry = null;
        try {
            Class<?> interfaceName = serviceBeans.getClass(requestDto.getInterfaceName());
            Method method1 = interfaceName.getMethod(requestDto.getMethodName(), requestDto.getMethodParamType());
            Object service = serviceBeans.getService(requestDto.getInterfaceName());
            if (service == null) {
                throw new RpcServerException(RpcServerStatusEnum.NO_DISCOVER_SERVER);
            }
            entry = SphU.entry(requestDto.getInterfaceName(), EntryType.IN, 1);
            data = method1.invoke(service, requestDto.getParams());
            responseDto = ResponseBuilder.success(requestDto.getRequestId(), data);
        } catch (FlowException e) {
            responseDto = ResponseBuilder.error(requestDto.getRequestId(), data, RpcServerStatusEnum.FLOW_ERROR);
            logger.error("被限流了.requestDto {}", requestDto);
        } catch (DegradeException e) {
            responseDto = ResponseBuilder.error(requestDto.getRequestId(), data, RpcServerStatusEnum.DEGRADE_ERROR);
            logger.error("异常太多熔断了 {}", requestDto);
        } catch (BlockException blockException) {
            responseDto = ResponseBuilder.error(requestDto.getRequestId(), data, RpcServerStatusEnum.SENTINEL_ERROR);
            logger.error("sentinel错误了");
        } catch (Exception exception) {
            responseDto = ResponseBuilder.error(requestDto.getRequestId(), data, RpcServerStatusEnum.SERVER_ERROR);
            Tracer.trace(exception);
            //exception.printStackTrace();
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
        return responseDto;
    }
}
