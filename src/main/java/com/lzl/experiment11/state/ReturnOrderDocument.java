package com.lzl.experiment11.state;

public class ReturnOrderDocument extends AbstractBusinessDocument {
    public ReturnOrderDocument(PurchaseOrder order, int quantity) {
        super(order, quantity);
    }

    @Override
    public DocumentType getDocumentType() {
        return DocumentType.RETURN_ORDER;
    }
}
