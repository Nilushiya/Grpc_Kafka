package com.grpcClient.client.service;

import com.grpcClient.client.dto.OrderSummaryDTO;
import com.grpcClient.client.dto.StockOrderDTO;
import com.javatechie.grpc.OrderSummary;
import com.javatechie.grpc.StockOrder;
import com.javatechie.grpc.StokeTradingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BulkOrderService {
    @GrpcClient("stockService")
    private StokeTradingServiceGrpc.StokeTradingServiceStub stokeTradingServiceStub;
//    public void placeBulkOrders() {
//        StreamObserver<OrderSummary> responseObserver = new StreamObserver<OrderSummary>() {
//            @Override
//            public void onNext(OrderSummary summary) {
//                System.out.println("Order Summary Received from Server:");
//                System.out.println("Total Orders: " + summary.getTotalOrders());
//                System.out.println("Successful Orders: " + summary.getSuccessCount());
//                System.out.println("Total Amount: $" + summary.getTotalAmount());
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//                System.out.println("Order Summary Receivedn error from Server:" + throwable.getMessage());
//            }
//
//            @Override
//            public void onCompleted() {
//                System.out.println("Stream completed , server is done sending summary !");
//            }
//        };
//
//        StreamObserver<StockOrder> requestObserver = stokeTradingServiceStub.bulkStockOrder(responseObserver);
//
//        // send multiple steam of stock order message/request
//        try {
//
//            requestObserver.onNext(StockOrder.newBuilder()
//                    .setOrderId("1")
//                    .setStockSymbol("AAPL")
//                    .setOrderType("BUY")
//                    .setPrice(150.5)
//                    .setQuantity(10)
//                    .build());
//
//            requestObserver.onNext(StockOrder.newBuilder()
//                    .setOrderId("2")
//                    .setStockSymbol("GOOGL")
//                    .setOrderType("SELL")
//                    .setPrice(2700.0)
//                    .setQuantity(5)
//                    .build());
//
//            requestObserver.onNext(StockOrder.newBuilder()
//                    .setOrderId("3")
//                    .setStockSymbol("TSLA")
//                    .setOrderType("BUY")
//                    .setPrice(700.0)
//                    .setQuantity(8)
//                    .build());
//
//            //done sending orders
//            requestObserver.onCompleted();
//        } catch (Exception ex) {
//            requestObserver.onError(ex);
//        }
//
//    }

    public OrderSummaryDTO placeBulkOrders(List<StockOrderDTO> stockOrderDTOS) {
        CountDownLatch latch = new  CountDownLatch(1);
        final OrderSummaryDTO[] resultHolder = new OrderSummaryDTO[1];

        StreamObserver<OrderSummaryDTO> orderSummaryRes = new StreamObserver<>(){

            @Override
            public void onNext(OrderSummaryDTO orderSummary) {
                resultHolder[0] = new OrderSummaryDTO();
                resultHolder[0].setTotal_orders(orderSummary.getTotal_orders());
                resultHolder[0].setTotal_amount(orderSummary.getTotal_amount());
                resultHolder[0].setSuccess_count(orderSummary.getSuccess_count());
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        };

        StreamObserver<StockOrder> requestObserver = stokeTradingServiceStub.bulkStockOrder(orderSummaryRes);

        // Convert the DTOs to Protobuf messages and send them
        StockOrderDTO[] stockOrderDTOs;
        for (StockOrderDTO stockOrderDTO : stockOrderDTOS) {
            StockOrder order = StockOrder.newBuilder()
                    .setOrderId(stockOrderDTO.getOrderId())
                    .setStockSymbol(stockOrderDTO.getStockSymbol())
                    .setOrderType(stockOrderDTO.getOrderType())
                    .setPrice(stockOrderDTO.getPrice())
                    .setQuantity(stockOrderDTO.getQuantity())
                    .build();

            requestObserver.onNext(order);
        }

        requestObserver.onCompleted();

        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return resultHolder[0]; // Return DTO to the controller

    }
}
