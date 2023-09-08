package com.shop.order.dto;

import com.shop.order.model.OrderLineItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private List<OrderLineItemDto> orderLineItemsDtoList;
}
