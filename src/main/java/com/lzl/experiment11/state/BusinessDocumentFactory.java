package com.lzl.experiment11.state;

import java.util.EnumMap;
import java.util.Map;

public final class BusinessDocumentFactory {
    private static final Map<DocumentType, DocumentCreator> CREATORS = new EnumMap<>(DocumentType.class);

    static {
        CREATORS.put(DocumentType.INBOUND_ORDER, InboundOrderDocument::new);
        CREATORS.put(DocumentType.INVOICE, InvoiceDocument::new);
        CREATORS.put(DocumentType.RETURN_ORDER, ReturnOrderDocument::new);
    }

    private BusinessDocumentFactory() {
    }

    public static BusinessDocument create(DocumentType type, PurchaseOrder order, int quantity) {
        DocumentCreator creator = CREATORS.get(type);
        if (creator == null) {
            throw new BusinessException("业务单据创建失败：未知单据类型。");
        }
        return creator.create(order, quantity);
    }

    private interface DocumentCreator {
        BusinessDocument create(PurchaseOrder order, int quantity);
    }
}
