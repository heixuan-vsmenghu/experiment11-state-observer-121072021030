package com.lzl.experiment11.state;

import com.lzl.experiment11.state.validation.OrderValidationChains;

public class DraftState extends AbstractPurchaseOrderState {
    @Override
    public void edit(PurchaseOrder order, String productName, int quantity, String supplier) {
        OrderValidationChains.validateEdit(productName, quantity, supplier);
        String beforeState = order.getStateName();
        order.updateBasicInfo(productName, quantity, supplier);
        order.recordOperation("编辑采购单", beforeState, order.getStateName(),
                "已更新商品、数量和供应商信息。");
    }

    @Override
    public void approve(PurchaseOrder order) {
        order.changeState(new ApprovedState(), "审批采购单", "审批通过，采购单进入已审批状态。");
    }

    @Override
    public void cancel(PurchaseOrder order) {
        order.changeState(new CancelledState(), "取消采购单", "采购单尚未入库，取消成功。");
    }

    @Override
    public String getStateName() {
        return "草稿";
    }
}
