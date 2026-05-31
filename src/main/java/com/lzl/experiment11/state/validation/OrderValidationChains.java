package com.lzl.experiment11.state.validation;

import com.lzl.experiment11.state.UserRole;

public final class OrderValidationChains {
    private OrderValidationChains() {
    }

    public static void validateEdit(String productName, int quantity, String supplier) {
        OrderValidationRule chain = new ProductNameRequiredRule();
        chain.linkWith(new PositiveQuantityRule())
                .linkWith(new SupplierRequiredRule());
        chain.check(ValidationContext.edit(productName, quantity, supplier));
    }

    public static void validateBusinessQuantity(String operation, int quantity, int maxQuantity) {
        OrderValidationRule chain = new PositiveQuantityRule();
        chain.linkWith(new MaxQuantityRule());
        chain.check(ValidationContext.quantity(operation, quantity, maxQuantity));
    }

    public static void validateAdmin(String operation, UserRole role) {
        OrderValidationRule chain = new AdminRoleRule();
        chain.check(ValidationContext.admin(operation, role));
    }
}
