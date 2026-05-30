package com.lzl.experiment11.state;

public class CancelledState extends AbstractPurchaseOrderState {
    @Override
    public void approve(PurchaseOrder order) {
        reject("审批采购单", "采购单已取消，属于终止状态，不能再次审批。");
    }

    @Override
    public void cancel(PurchaseOrder order) {
        reject("取消采购单", "采购单已取消，不能重复取消。");
    }

    @Override
    public String getStateName() {
        return "已取消";
    }
}
