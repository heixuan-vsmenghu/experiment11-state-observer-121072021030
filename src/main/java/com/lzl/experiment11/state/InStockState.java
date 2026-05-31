package com.lzl.experiment11.state;

import com.lzl.experiment11.state.validation.OrderValidationChains;

public class InStockState extends AbstractPurchaseOrderState {
    @Override
    public void cancel(PurchaseOrder order) {
        reject("取消采购单", "采购单已完成入库，不能直接取消，应按退货退款流程处理。");
    }

    @Override
    public void generateInvoice(PurchaseOrder order, int invoiceQuantity) {
        OrderValidationChains.validateBusinessQuantity("生成发票", invoiceQuantity, order.getRemainingInvoiceQuantity());
        BusinessDocument document = order.recordInvoiceDocument(invoiceQuantity);
        if (order.isInvoiceComplete()) {
            order.changeState(new InvoicedState(), "生成发票",
                    document.getDocumentNo() + " 已生成，采购单全部开票。");
            return;
        }
        order.changeState(new PartiallyInvoicedState(), "部分开票",
                document.getDocumentNo() + " 已生成，当前已开票 " + order.getInvoiceQuantity() + "/" + order.getQuantity() + "。");
    }

    @Override
    public void returnAndRefund(PurchaseOrder order) {
        BusinessDocument document = order.recordReturnDocument(order.getInboundQuantity());
        order.changeState(new ReturnedState(), "退货退款",
                document.getDocumentNo() + " 已生成，已入库采购单退货退款成功，流程完结。");
    }

    @Override
    public String getStateName() {
        return "已入库";
    }
}
