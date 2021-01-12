package github.tuquanrong.transport;

import github.tuquanrong.model.dto.RequestDto;
import github.tuquanrong.model.dto.ResponseDto;
import github.tuquanrong.serializer.ProtostuffSerializer;
import github.tuquanrong.serializer.Serializer;
import io.protostuff.Response;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * tutu
 * 2021/1/10
 */
public class NettyClientTest {
    public static void main(String[] args) throws UnknownHostException, ExecutionException, InterruptedException {
        NettyClient nettyClient = new NettyClient();
        RequestDto requestDto = new RequestDto();
        requestDto.setRequestId(UUID.randomUUID().toString());
        CompletableFuture<ResponseDto> completableFuture = nettyClient.sendMessage(requestDto);
        System.out.println(completableFuture.get());
//        Object object = (Object) requestDto;
//        Serializer serializer = new ProtostuffSerializer();
//        byte[] requestBytes = serializer.serialize(object);
//        System.out.println(requestDto + "   " + Arrays.toString(requestBytes));
//        RequestDto requestDto1 = serializer.deserialize(requestBytes, RequestDto.class);
//        System.out.println(requestDto1 + "   ");
    }
}
