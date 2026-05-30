package com.lzl.experiment11.state;

public class ApprovedState extends AbstractPurchaseOrderState {
    @Override
    public void cancel(PurchaseOrder order) {
        order.changeState(new CancelledState(), "取消采购单", "采购单尚未入库，取消成功。");
    }

    @Override
    public void generateInboundOrder(PurchaseOrder order) {
        order.changeState(new InStockState(), "生成入库单", "入库单生成成功，采购单进入已入库状态。");
    }

    @Override
    public String getStateName() {
        return "已审批";
    }
}
