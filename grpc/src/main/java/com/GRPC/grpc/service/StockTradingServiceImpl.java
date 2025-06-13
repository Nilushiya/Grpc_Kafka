package com.GRPC.grpc.service;

import com.GRPC.grpc.entity.Stock;
import com.GRPC.grpc.repository.StockRepository;
import com.javatechie.grpc.*;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Override
    public StreamObserver<StockOrder> bulkStockOrder(StreamObserver<OrderSummary> responseObserver) {
        return new StreamObserver<StockOrder>(){
            int total_orders = 0;
            double total_amount = 0;
            int success_count = 0;
            @Override
            public void onNext(StockOrder stockOrder) {
                total_orders++;
                total_amount += stockOrder.getPrice()* stockOrder.getQuantity();
                success_count++;
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Server unable to process the request : "+ throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                OrderSummary orderSummary = OrderSummary.newBuilder()
                        .setTotalOrders(total_orders)
                        .setTotalAmount(total_amount)
                        .setSuccessCount(success_count)
                        .build();
                responseObserver.onNext(orderSummary);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<StockOrder> liveTrading(StreamObserver<TradeStatus> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(StockOrder stockOrder) {
                System.out.println("Received order: " + stockOrder);
                String status = "EXECUTED";
                String message = "Order processed successfully";
                if (stockOrder.getQuantity() <= 0) {
                    status = "FAILED";
                    message = "Invalid quantity";
                }
                TradeStatus response = TradeStatus.newBuilder()
                        .setOrderId(stockOrder.getOrderId())
                        .setStatus(status)
                        .setMessage(message)
                        .setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                        .build();
                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("Error " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
