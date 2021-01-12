package github.tuquanrong.transport;

import github.tuquanrong.model.dto.ResponseDto;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * tutu
 * 2021/1/10
 */
public class UnDealMessage {
    private static Map<String, CompletableFuture<ResponseDto>> map = new HashMap<>();

    public static void setRequestId(String requestId, CompletableFuture<ResponseDto> completableFuture) {
        map.put(requestId, completableFuture);
    }

    public static void dealRequestId(ResponseDto responseDto) {
        CompletableFuture<ResponseDto> completableFuture = map.get(responseDto.getResponseId());
        if (completableFuture != null) {
            completableFuture.complete(responseDto);
        }
    }
}
