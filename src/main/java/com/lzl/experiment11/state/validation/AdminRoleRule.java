package com.lzl.experiment11.state.validation;

import com.lzl.experiment11.state.BusinessException;
import com.lzl.experiment11.state.UserRole;

public class AdminRoleRule extends OrderValidationRule {
    @Override
    protected void validate(ValidationContext context) {
        if (context.getRole() != UserRole.ADMIN) {
            throw new BusinessException(context.getOperation() + "失败：只有管理员可以执行该操作。");
        }
    }
}
