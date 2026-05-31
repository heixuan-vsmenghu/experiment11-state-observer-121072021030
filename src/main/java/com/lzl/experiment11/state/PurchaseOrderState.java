package com.lzl.experiment11.state;

public interface PurchaseOrderState {
    void edit(PurchaseOrder order, String productName, int quantity, String supplier);

    void approve(PurchaseOrder order);

    void cancel(PurchaseOrder order);

    void revokeApproval(PurchaseOrder order, UserRole role);

    void generateInboundOrder(PurchaseOrder order, int inboundQuantity);

    void generateInvoice(PurchaseOrder order, int invoiceQuantity);

    void pay(PurchaseOrder order);

    void returnAndRefund(PurchaseOrder order);

    String getStateName();
}
