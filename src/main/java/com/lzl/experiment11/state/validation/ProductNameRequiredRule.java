package com.lzl.experiment11.state.validation;

import com.lzl.experiment11.state.BusinessException;

public class ProductNameRequiredRule extends OrderValidationRule {
    @Override
    protected void validate(ValidationContext context) {
        String productName = context.getProductName();
        if (productName == null || productName.isBlank()) {
            throw new BusinessException(context.getOperation() + "失败：采购商品名称不能为空。");
        }
    }
}
