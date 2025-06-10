package com.GRPC.grpc.service;

import com.GRPC.grpc.entity.Stock;
import com.GRPC.grpc.repository.StockRepository;
import com.javatechie.grpc.StokeRequest;
import com.javatechie.grpc.StokeResponse;
import com.javatechie.grpc.StokeTradingServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@GrpcService
public class StockTradingServiceImpl extends StokeTradingServiceGrpc.StokeTradingServiceImplBase {
    @Autowired
    StockRepository stockRepository;

    @Override
    public void getStokePrice(StokeRequest request, StreamObserver<StokeResponse> responseObserver) {
        String stockSymbol = request.getStockSymbol();
        Stock stock = stockRepository.findByStockSymbol(stockSymbol);

        StokeResponse stokeResponse = StokeResponse.newBuilder()
                .setStockSymbol(stock.getStockSymbol())
                .setPrice(stock.getPrice())
                .setTimestamp(stock.getLastUpdated().toString())
                .build();
        responseObserver.onNext(stokeResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void subscribeStockPrice(StokeRequest request, StreamObserver<StokeResponse> responseObserver) {
        String stockSymbol = request.getStockSymbol();
        try {
            for (int i = 0; i <= 10; i++) {
                StokeResponse stockResponse = StokeResponse.newBuilder()
                        .setStockSymbol(stockSymbol)
                        .setPrice(new Random().nextDouble(200))
                        .setTimestamp(Instant.now().toString())
                        .build();
                responseObserver.onNext(stockResponse);
                TimeUnit.SECONDS.sleep(1);
            }
            responseObserver.onCompleted();
        }catch (Exception ex){
            responseObserver.onError(ex);
        }
    }
}
