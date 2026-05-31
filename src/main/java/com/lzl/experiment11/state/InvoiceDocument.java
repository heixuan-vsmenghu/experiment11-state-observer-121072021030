package com.lzl.experiment11.state;

public class InvoiceDocument extends AbstractBusinessDocument {
    public InvoiceDocument(PurchaseOrder order, int quantity) {
        super(order, quantity);
    }

    @Override
    public DocumentType getDocumentType() {
        return DocumentType.INVOICE;
    }
}
