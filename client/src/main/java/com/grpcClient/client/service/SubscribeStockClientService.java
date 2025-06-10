package com.grpcClient.client.service;

import com.javatechie.grpc.StokeRequest;
import com.javatechie.grpc.StokeResponse;
import com.javatechie.grpc.StokeTradingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;

public class SubscribeStockClientService {
    @GrpcClient("stockService")
    private StokeTradingServiceGrpc.StokeTradingServiceStub stokeTradingServiceStub;

    public void subscribeStockPrice(String stockSymbol){
        StokeRequest request = StokeRequest.newBuilder()
                .setStockSymbol(stockSymbol)
                .build();

        stokeTradingServiceStub.subscribeStockPrice(request, new StreamObserver<StokeResponse>() {
            @Override
            public void onNext(StokeResponse stokeResponse) {
                System.out.println("Stoke price updated: "+ stokeResponse.getStockSymbol() +
                        "Price: "+ stokeResponse.getPrice() +
                        "Time: "+ stokeResponse.getTimestamp());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error: "+throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stoke price updated completed");
            }
        });

    }
}
