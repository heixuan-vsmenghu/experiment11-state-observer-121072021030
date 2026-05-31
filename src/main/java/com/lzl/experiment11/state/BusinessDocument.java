package com.lzl.experiment11.state;

public interface BusinessDocument {
    String getDocumentNo();

    DocumentType getDocumentType();

    int getQuantity();

    String getSummary();
}
