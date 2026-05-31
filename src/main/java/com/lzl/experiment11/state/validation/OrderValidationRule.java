package com.lzl.experiment11.state.validation;

public abstract class OrderValidationRule {
    private OrderValidationRule next;

    public OrderValidationRule linkWith(OrderValidationRule next) {
        this.next = next;
        return next;
    }

    public final void check(ValidationContext context) {
        validate(context);
        if (next != null) {
            next.check(context);
        }
    }

    protected abstract void validate(ValidationContext context);
}
