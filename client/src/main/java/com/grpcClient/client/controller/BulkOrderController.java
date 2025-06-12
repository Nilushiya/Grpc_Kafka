package com.grpcClient.client.controller;

import com.grpcClient.client.dto.OrderSummaryDTO;
import com.grpcClient.client.dto.StockOrderDTO;
import com.grpcClient.client.service.BulkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/stoke")
public class BulkOrderController {
    @Autowired
    private BulkOrderService bulkOrderService;
    @PostMapping("/bulkOrder")
    public OrderSummaryDTO placeOrders(@RequestBody List<StockOrderDTO> stockOrderDTOS){
        return bulkOrderService.placeBulkOrders(stockOrderDTOS);
    }
}
