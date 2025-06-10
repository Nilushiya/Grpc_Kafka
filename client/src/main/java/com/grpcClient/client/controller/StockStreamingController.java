package com.grpcClient.client.controller;

import com.google.protobuf.util.JsonFormat;
import com.javatechie.grpc.StokeRequest;
import com.javatechie.grpc.StokeResponse;
import com.javatechie.grpc.StokeTradingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/stocks")
public class StockStreamingController {
    @GrpcClient("stockService")
    private StokeTradingServiceGrpc.StokeTradingServiceStub stockServiceStub;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @GetMapping(value = "/subscribe/{symbol}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeStockPrice(@PathVariable String symbol) {
        SseEmitter emitter = new SseEmitter();
        executor.execute(() -> {
            StokeRequest request = StokeRequest.newBuilder().setStockSymbol(symbol).build();

            stockServiceStub.subscribeStockPrice(request, new StreamObserver<>() {
                @Override
                public void onNext(StokeResponse response) {
                    try {
                        String jsonResponse = JsonFormat.printer().print(response);
                        emitter.send(jsonResponse);
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    emitter.completeWithError(t);
                }

                @Override
                public void onCompleted() {
                    emitter.complete();
                }
            });
        });
        return emitter;
    }
}
