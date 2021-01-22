package github.tuquanrong;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AppTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        completableFuture.completeExceptionally(new NullPointerException());
        System.out.println(completableFuture.get());
    }
}
