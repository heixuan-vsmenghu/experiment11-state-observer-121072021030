package com.lzl.experiment11.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PurchaseOrder {
    private final String orderNo;
    private String supplier;
    private String productName;
    private int quantity;
    private double amount;
    private int inboundQuantity;
    private int invoiceQuantity;
    private PurchaseOrderState state;
    private final List<OperationLog> operationLogs = new ArrayList<>();
    private final List<BusinessDocument> businessDocuments = new ArrayList<>();

    public PurchaseOrder(String orderNo) {
        this.orderNo = orderNo;
        this.state = new DraftState();
        addLog("创建采购单", "无", getStateName(), "采购单创建成功，进入草稿状态。");
    }

    public void edit(String productName, int quantity, String supplier) {
        state.edit(this, productName, quantity, supplier);
    }

    public void approve() {
        state.approve(this);
    }

    public void cancel() {
        state.cancel(this);
    }

    public void revokeApproval(UserRole role) {
        state.revokeApproval(this, role);
    }

    public void generateInboundOrder() {
        generateInboundOrder(getRemainingInboundQuantity());
    }

    public void generateInboundOrder(int inboundQuantity) {
        state.generateInboundOrder(this, inboundQuantity);
    }

    public void generateInvoice() {
        generateInvoice(getRemainingInvoiceQuantity());
    }

    public void generateInvoice(int invoiceQuantity) {
        state.generateInvoice(this, invoiceQuantity);
    }

    public void pay() {
        state.pay(this);
    }

    public void returnAndRefund() {
        state.returnAndRefund(this);
    }

    public void setState(PurchaseOrderState state) {
        this.state = state;
    }

    public String getStateName() {
        return state.getStateName();
    }

    public void addLog(String operation, String beforeState, String afterState, String message) {
        operationLogs.add(new OperationLog(operation, beforeState, afterState, message));
    }

    public void showInfo() {
        System.out.println();
        System.out.println("采购单信息：" + orderNo);
        System.out.println("商品名称：" + display(productName));
        System.out.println("采购数量：" + quantity);
        System.out.println("已入库数量：" + inboundQuantity);
        System.out.println("已开票数量：" + invoiceQuantity);
        System.out.println("供应商：" + display(supplier));
        System.out.printf("采购金额：%.2f 元%n", amount);
        System.out.println("当前状态：" + getStateName());
        System.out.println("业务单据：");
        if (businessDocuments.isEmpty()) {
            System.out.println("  暂无业务单据");
        } else {
            for (BusinessDocument document : businessDocuments) {
                System.out.println("  " + document.getSummary());
            }
        }
        System.out.println("操作日志：");
        for (OperationLog log : operationLogs) {
            System.out.println("  " + log);
        }
    }

    public List<OperationLog> getOperationLogs() {
        return Collections.unmodifiableList(operationLogs);
    }

    public List<BusinessDocument> getBusinessDocuments() {
        return Collections.unmodifiableList(businessDocuments);
    }

    void updateBasicInfo(String productName, int quantity, String supplier) {
        this.productName = productName;
        this.quantity = quantity;
        this.supplier = supplier;
        this.amount = quantity * 100.0;
        this.inboundQuantity = 0;
        this.invoiceQuantity = 0;
        this.businessDocuments.clear();
    }

    void recordOperation(String operation, String beforeState, String afterState, String message) {
        addLog(operation, beforeState, afterState, message);
        System.out.printf("[%s] %s：%s -> %s，%s%n", orderNo, operation, beforeState, afterState, message);
    }

    void changeState(PurchaseOrderState nextState, String operation, String message) {
        String beforeState = getStateName();
        setState(nextState);
        recordOperation(operation, beforeState, getStateName(), message);
    }

    BusinessDocument recordInboundDocument(int inboundQuantity) {
        this.inboundQuantity += inboundQuantity;
        return addDocument(DocumentType.INBOUND_ORDER, inboundQuantity);
    }

    BusinessDocument recordInvoiceDocument(int invoiceQuantity) {
        this.invoiceQuantity += invoiceQuantity;
        return addDocument(DocumentType.INVOICE, invoiceQuantity);
    }

    BusinessDocument recordReturnDocument(int returnQuantity) {
        return addDocument(DocumentType.RETURN_ORDER, returnQuantity);
    }

    int getRemainingInboundQuantity() {
        return quantity - inboundQuantity;
    }

    int getRemainingInvoiceQuantity() {
        return quantity - invoiceQuantity;
    }

    boolean isInboundComplete() {
        return quantity > 0 && inboundQuantity == quantity;
    }

    boolean isInvoiceComplete() {
        return quantity > 0 && invoiceQuantity == quantity;
    }

    int getQuantity() {
        return quantity;
    }

    int getInboundQuantity() {
        return inboundQuantity;
    }

    int getInvoiceQuantity() {
        return invoiceQuantity;
    }

    String getOrderNo() {
        return orderNo;
    }

    String nextDocumentNo(DocumentType documentType) {
        return documentType.getCode() + "-" + orderNo + "-" + String.format("%02d", businessDocuments.size() + 1);
    }

    private BusinessDocument addDocument(DocumentType type, int documentQuantity) {
        BusinessDocument document = BusinessDocumentFactory.create(type, this, documentQuantity);
        businessDocuments.add(document);
        return document;
    }

    private String display(String value) {
        return value == null || value.isBlank() ? "未填写" : value;
    }
}
