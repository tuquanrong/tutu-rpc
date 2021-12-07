package github.tuquanrong.transport;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import github.tuquanrong.model.dto.ResponseDto;

/**
 * tutu
 * 2021/1/10
 */
public class UnDealMessage {
    private static final Logger logger = LoggerFactory.getLogger(UnDealMessage.class);
    private static final UnDealMessage UN_DEAL_MESSAGE = new UnDealMessage();
    private Map<String, CompletableFuture<ResponseDto>> map = new HashMap<>();

    public static UnDealMessage getInstance() {
        return UN_DEAL_MESSAGE;
    }

    public void setRequestId(String requestId, CompletableFuture<ResponseDto> completableFuture) {
        map.put(requestId, completableFuture);
    }

    /**
     * 把结果给客户端代理，由代理转发给上层
     *
     * @param responseDto
     */
    public void dealRequestId(ResponseDto responseDto) {
        CompletableFuture<ResponseDto> completableFuture = map.get(responseDto.getResponseId());
        if (completableFuture != null) {
            completableFuture.complete(responseDto);
        } else {
            logger.error("failDeal");
        }
    }
}
