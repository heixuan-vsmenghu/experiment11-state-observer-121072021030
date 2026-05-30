package com.lzl.experiment11.state;

public interface PurchaseOrderState {
    void edit(PurchaseOrder order, String productName, int quantity, String supplier);

    void approve(PurchaseOrder order);

    void cancel(PurchaseOrder order);

    void generateInboundOrder(PurchaseOrder order);

    void generateInvoice(PurchaseOrder order);

    void pay(PurchaseOrder order);

    void returnAndRefund(PurchaseOrder order);

    String getStateName();
}
