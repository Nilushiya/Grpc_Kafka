package com.grpcClient.client.service;

import com.javatechie.grpc.StokeRequest;
import com.javatechie.grpc.StokeResponse;
import com.javatechie.grpc.StokeTradingServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class StockClientService {
    @GrpcClient("stockService")
    private StokeTradingServiceGrpc.StokeTradingServiceBlockingStub serviceBlockingStub;

    public StokeResponse getStockPrice(String stockSymbol) {
        StokeRequest request = StokeRequest.newBuilder()
                .setStockSymbol(stockSymbol)
                .build();
        return serviceBlockingStub.getStokePrice(request);
    }


}
