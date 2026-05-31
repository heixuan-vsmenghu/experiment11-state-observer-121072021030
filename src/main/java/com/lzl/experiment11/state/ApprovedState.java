package com.lzl.experiment11.state;

import com.lzl.experiment11.state.validation.OrderValidationChains;

public class ApprovedState extends AbstractPurchaseOrderState {
    @Override
    public void cancel(PurchaseOrder order) {
        order.changeState(new CancelledState(), "取消采购单", "采购单尚未入库，取消成功。");
    }

    @Override
    public void revokeApproval(PurchaseOrder order, UserRole role) {
        OrderValidationChains.validateAdmin("撤销审批", role);
        order.changeState(new DraftState(), "撤销审批",
                role.getDisplayName() + "撤销已审批采购单，单据退回草稿状态。");
    }

    @Override
    public void generateInboundOrder(PurchaseOrder order, int inboundQuantity) {
        OrderValidationChains.validateBusinessQuantity("生成入库单", inboundQuantity, order.getRemainingInboundQuantity());
        BusinessDocument document = order.recordInboundDocument(inboundQuantity);
        if (order.isInboundComplete()) {
            order.changeState(new InStockState(), "生成入库单",
                    document.getDocumentNo() + " 已生成，采购单全部入库。");
            return;
        }
        order.changeState(new PartiallyInStockState(), "部分入库",
                document.getDocumentNo() + " 已生成，当前已入库 " + order.getInboundQuantity() + "/" + order.getQuantity() + "。");
    }

    @Override
    public String getStateName() {
        return "已审批";
    }
}
