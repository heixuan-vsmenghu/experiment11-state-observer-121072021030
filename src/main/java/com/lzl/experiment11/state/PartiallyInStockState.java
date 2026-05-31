package com.lzl.experiment11.state;

import com.lzl.experiment11.state.validation.OrderValidationChains;

public class PartiallyInStockState extends AbstractPurchaseOrderState {
    @Override
    public void cancel(PurchaseOrder order) {
        reject("取消采购单", "采购单已经开始入库，不能直接取消，应继续入库或走退货退款流程。");
    }

    @Override
    public void generateInboundOrder(PurchaseOrder order, int inboundQuantity) {
        OrderValidationChains.validateBusinessQuantity("继续生成入库单", inboundQuantity, order.getRemainingInboundQuantity());
        BusinessDocument document = order.recordInboundDocument(inboundQuantity);
        if (order.isInboundComplete()) {
            order.changeState(new InStockState(), "补齐入库",
                    document.getDocumentNo() + " 已生成，采购单已完成全部入库。");
            return;
        }
        order.recordOperation("继续部分入库", order.getStateName(), order.getStateName(),
                document.getDocumentNo() + " 已生成，当前已入库 " + order.getInboundQuantity() + "/" + order.getQuantity() + "。");
    }

    @Override
    public String getStateName() {
        return "部分入库";
    }
}
