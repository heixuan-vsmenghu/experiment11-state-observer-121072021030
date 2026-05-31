package com.lzl.experiment11.state.validation;

import com.lzl.experiment11.state.BusinessException;

public class MaxQuantityRule extends OrderValidationRule {
    @Override
    protected void validate(ValidationContext context) {
        if (context.getQuantity() > context.getMaxQuantity()) {
            throw new BusinessException(context.getOperation() + "失败：本次数量不能超过剩余可处理数量 "
                    + context.getMaxQuantity() + "。");
        }
    }
}
