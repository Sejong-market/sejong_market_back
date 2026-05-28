package com.example.market.dto.product;

import com.example.market.entity.ProductStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductStatusRequestDto {
    private ProductStatus status;
}
