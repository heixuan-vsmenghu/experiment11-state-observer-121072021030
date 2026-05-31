package com.lzl.experiment11.state;

public class InboundOrderDocument extends AbstractBusinessDocument {
    public InboundOrderDocument(PurchaseOrder order, int quantity) {
        super(order, quantity);
    }

    @Override
    public DocumentType getDocumentType() {
        return DocumentType.INBOUND_ORDER;
    }
}
