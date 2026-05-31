package com.lzl.experiment11.state.validation;

import com.lzl.experiment11.state.BusinessException;

public class SupplierRequiredRule extends OrderValidationRule {
    @Override
    protected void validate(ValidationContext context) {
        String supplier = context.getSupplier();
        if (supplier == null || supplier.isBlank()) {
            throw new BusinessException(context.getOperation() + "失败：供应商不能为空。");
        }
    }
}
