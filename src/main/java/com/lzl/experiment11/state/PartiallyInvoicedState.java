package com.lzl.experiment11.state;

import com.lzl.experiment11.state.validation.OrderValidationChains;

public class PartiallyInvoicedState extends AbstractPurchaseOrderState {
    @Override
    public void cancel(PurchaseOrder order) {
        reject("取消采购单", "采购单已经开始开票，不能直接取消，应按退货退款或财务冲销流程处理。");
    }

    @Override
    public void generateInvoice(PurchaseOrder order, int invoiceQuantity) {
        OrderValidationChains.validateBusinessQuantity("继续生成发票", invoiceQuantity, order.getRemainingInvoiceQuantity());
        BusinessDocument document = order.recordInvoiceDocument(invoiceQuantity);
        if (order.isInvoiceComplete()) {
            order.changeState(new InvoicedState(), "补齐发票",
                    document.getDocumentNo() + " 已生成，采购单已完成全部开票。");
            return;
        }
        order.recordOperation("继续部分开票", order.getStateName(), order.getStateName(),
                document.getDocumentNo() + " 已生成，当前已开票 " + order.getInvoiceQuantity() + "/" + order.getQuantity() + "。");
    }

    @Override
    public void pay(PurchaseOrder order) {
        reject("付款", "采购单还有部分商品未开票，必须全部开票后才能付款。");
    }

    @Override
    public void returnAndRefund(PurchaseOrder order) {
        BusinessDocument document = order.recordReturnDocument(order.getInboundQuantity());
        order.changeState(new ReturnedState(), "退货退款",
                document.getDocumentNo() + " 已生成，部分开票采购单退货退款成功，流程完结。");
    }

    @Override
    public String getStateName() {
        return "部分开票";
    }
}
