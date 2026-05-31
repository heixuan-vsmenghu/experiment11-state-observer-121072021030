package com.lzl.experiment11.state;

public enum DocumentType {
    INBOUND_ORDER("IN", "入库单"),
    INVOICE("INV", "发票"),
    RETURN_ORDER("RTN", "退货单");

    private final String code;
    private final String displayName;

    DocumentType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }
}
