package com.lzl.experiment11.state.validation;

import com.lzl.experiment11.state.BusinessException;

public class PositiveQuantityRule extends OrderValidationRule {
    @Override
    protected void validate(ValidationContext context) {
        if (context.getQuantity() <= 0) {
            throw new BusinessException(context.getOperation() + "失败：数量必须大于 0。");
        }
    }
}
