package com.lzl.experiment11.state;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AbstractBusinessDocument implements BusinessDocument {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String documentNo;
    private final String orderNo;
    private final int quantity;
    private final LocalDateTime createdTime;

    protected AbstractBusinessDocument(PurchaseOrder order, int quantity) {
        this.documentNo = order.nextDocumentNo(getDocumentType());
        this.orderNo = order.getOrderNo();
        this.quantity = quantity;
        this.createdTime = LocalDateTime.now();
    }

    @Override
    public String getDocumentNo() {
        return documentNo;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public String getSummary() {
        return String.format("%s：%s，关联采购单：%s，数量：%d，生成时间：%s",
                getDocumentType().getDisplayName(), documentNo, orderNo, quantity, createdTime.format(FORMATTER));
    }
}
