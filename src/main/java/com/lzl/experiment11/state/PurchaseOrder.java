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
    private PurchaseOrderState state;
    private final List<OperationLog> operationLogs = new ArrayList<>();

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

    public void generateInboundOrder() {
        state.generateInboundOrder(this);
    }

    public void generateInvoice() {
        state.generateInvoice(this);
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
        System.out.println("供应商：" + display(supplier));
        System.out.printf("采购金额：%.2f 元%n", amount);
        System.out.println("当前状态：" + getStateName());
        System.out.println("操作日志：");
        for (OperationLog log : operationLogs) {
            System.out.println("  " + log);
        }
    }

    public List<OperationLog> getOperationLogs() {
        return Collections.unmodifiableList(operationLogs);
    }

    void updateBasicInfo(String productName, int quantity, String supplier) {
        if (quantity <= 0) {
            throw new BusinessException("编辑采购单失败：采购数量必须大于 0。");
        }
        this.productName = productName;
        this.quantity = quantity;
        this.supplier = supplier;
        this.amount = quantity * 100.0;
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

    String getOrderNo() {
        return orderNo;
    }

    private String display(String value) {
        return value == null || value.isBlank() ? "未填写" : value;
    }
}
