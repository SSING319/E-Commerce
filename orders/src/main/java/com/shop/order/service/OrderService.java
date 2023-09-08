package com.shop.order.service;

import com.shop.order.dto.InventoryResponse;
import com.shop.order.dto.OrderLineItemDto;
import com.shop.order.dto.OrderRequest;
import com.shop.order.model.Order;
import com.shop.order.model.OrderLineItem;
import com.shop.order.repo.OrderRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;
    private final WebClient webClient;

    @Transactional
    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItems);

        //taking the skucodes from the order
        List<String> orderSkuCodes = order.getOrderLineItemsList().stream().map(OrderLineItem::getSkuCode).toList();

        //call inventory service to check if all the skuCodes are in stock
        InventoryResponse[] inventoryResponses = webClient.get()
                                .uri("http://inventory-service/api/inventory",
                                        uriBuilder -> uriBuilder.queryParam("skuCodes", orderSkuCodes).build())
                                .retrieve()
                                .bodyToMono(InventoryResponse[].class)
                                .block();

        boolean allProductsIsInStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

        if(allProductsIsInStock) orderRepo.save(order);
        else throw new IllegalArgumentException("Product is Not in Stock");
    }

    private OrderLineItem mapToDto(OrderLineItemDto orderLineItemDto) {
        return OrderLineItem.builder()
                .price(orderLineItemDto.getPrice())
                .quantity(orderLineItemDto.getQuantity())
                .skuCode(orderLineItemDto.getSkuCode())
                .build();
    }
}
