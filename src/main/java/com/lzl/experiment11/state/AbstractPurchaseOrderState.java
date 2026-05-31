package com.lzl.experiment11.state;

public abstract class AbstractPurchaseOrderState implements PurchaseOrderState {
    @Override
    public void edit(PurchaseOrder order, String productName, int quantity, String supplier) {
        reject("编辑采购单", "该状态下采购单信息已经不能修改。");
    }

    @Override
    public void approve(PurchaseOrder order) {
        reject("审批采购单", "只有草稿状态的采购单允许审批。");
    }

    @Override
    public void cancel(PurchaseOrder order) {
        reject("取消采购单", "当前状态不允许取消采购单。");
    }

    @Override
    public void revokeApproval(PurchaseOrder order, UserRole role) {
        reject("撤销审批", "只有已审批状态允许执行审批撤销。");
    }

    @Override
    public void generateInboundOrder(PurchaseOrder order, int inboundQuantity) {
        reject("生成入库单", "只有已审批状态的采购单允许生成入库单。");
    }

    @Override
    public void generateInvoice(PurchaseOrder order, int invoiceQuantity) {
        reject("生成发票", "只有已入库状态的采购单允许生成发票。");
    }

    @Override
    public void pay(PurchaseOrder order) {
        reject("付款", "只有已开票状态的采购单允许付款。");
    }

    @Override
    public void returnAndRefund(PurchaseOrder order) {
        reject("退货退款", "只有已入库或已开票状态的采购单允许退货退款。");
    }

    protected void reject(String operation, String reason) {
        throw new BusinessException(operation + "失败：当前状态【" + getStateName() + "】不满足业务规则。" + reason);
    }
}
