package com.lzl.experiment11.state;

public class InvoicedState extends AbstractPurchaseOrderState {
    @Override
    public void edit(PurchaseOrder order, String productName, int quantity, String supplier) {
        reject("编辑采购单", "采购单已开票，商品、数量和供应商信息已经固化。");
    }

    @Override
    public void cancel(PurchaseOrder order) {
        reject("取消采购单", "采购单已开票，不能直接取消，应按退货退款或财务处理流程执行。");
    }

    @Override
    public void pay(PurchaseOrder order) {
        order.changeState(new PaidState(), "付款", "发票已全部生成，付款成功，采购单进入已付款状态。");
    }

    @Override
    public void returnAndRefund(PurchaseOrder order) {
        BusinessDocument document = order.recordReturnDocument(order.getInboundQuantity());
        order.changeState(new ReturnedState(), "退货退款",
                document.getDocumentNo() + " 已生成，已开票采购单退货退款成功，流程完结。");
    }

    @Override
    public String getStateName() {
        return "已开票";
    }
}
