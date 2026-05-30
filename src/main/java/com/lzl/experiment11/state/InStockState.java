package com.lzl.experiment11.state;

public class InStockState extends AbstractPurchaseOrderState {
    @Override
    public void cancel(PurchaseOrder order) {
        reject("取消采购单", "采购单已完成入库，不能直接取消，应按退货退款流程处理。");
    }

    @Override
    public void generateInvoice(PurchaseOrder order) {
        order.changeState(new InvoicedState(), "生成发票", "采购单已完成入库，发票生成成功。");
    }

    @Override
    public void returnAndRefund(PurchaseOrder order) {
        order.changeState(new ReturnedState(), "退货退款", "已入库采购单退货退款成功，流程完结。");
    }

    @Override
    public String getStateName() {
        return "已入库";
    }
}
