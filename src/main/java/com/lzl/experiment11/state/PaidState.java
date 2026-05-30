package com.lzl.experiment11.state;

public class PaidState extends AbstractPurchaseOrderState {
    @Override
    public void returnAndRefund(PurchaseOrder order) {
        reject("退货退款", "本实验将已付款视为付款完成状态，退货退款主要在已入库或已开票状态触发。");
    }

    @Override
    public String getStateName() {
        return "已付款";
    }
}
