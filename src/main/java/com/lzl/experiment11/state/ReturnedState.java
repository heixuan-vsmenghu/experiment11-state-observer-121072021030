package com.lzl.experiment11.state;

public class ReturnedState extends AbstractPurchaseOrderState {
    @Override
    public void returnAndRefund(PurchaseOrder order) {
        reject("退货退款", "采购单已退货完结，不能重复退货退款。");
    }

    @Override
    public String getStateName() {
        return "已退货完结";
    }
}
