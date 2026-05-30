package com.lzl.experiment11.state;

public class Experiment11Demo {
    private Experiment11Demo() {
    }

    public static void run() {
        runNormalPaymentFlow();
        runCancelFlow();
        runInvalidFlow();
        runReturnFlow();
    }

    private static void runNormalPaymentFlow() {
        System.out.println("========== 正常流程测试 ==========");
        PurchaseOrder order = new PurchaseOrder("PO-001");
        order.edit("服务器硬盘", 20, "福建数仓科技有限公司");
        order.approve();
        order.generateInboundOrder();
        order.generateInvoice();
        order.pay();
        System.out.println("最终状态：" + order.getStateName());
        order.showInfo();
        System.out.println();
    }

    private static void runCancelFlow() {
        System.out.println("========== 取消流程测试 ==========");
        PurchaseOrder order = new PurchaseOrder("PO-002");
        order.edit("机柜温控模块", 5, "福州恒温设备有限公司");
        order.approve();
        order.cancel();
        System.out.println("最终状态：" + order.getStateName());
        order.showInfo();
        System.out.println();
    }

    private static void runInvalidFlow() {
        System.out.println("========== 非法流程测试 ==========");

        PurchaseOrder draftInboundOrder = new PurchaseOrder("PO-ERR-001");
        attempt("草稿状态直接生成入库单", draftInboundOrder::generateInboundOrder);

        PurchaseOrder draftPayOrder = new PurchaseOrder("PO-ERR-002");
        attempt("草稿状态直接付款", draftPayOrder::pay);

        PurchaseOrder inStockCancelOrder = new PurchaseOrder("PO-ERR-003");
        inStockCancelOrder.edit("扫码枪", 10, "仓储设备供应商A");
        inStockCancelOrder.approve();
        inStockCancelOrder.generateInboundOrder();
        attempt("已入库状态取消采购单", inStockCancelOrder::cancel);

        PurchaseOrder invoicedEditOrder = new PurchaseOrder("PO-ERR-004");
        invoicedEditOrder.edit("PDA手持终端", 8, "仓储设备供应商B");
        invoicedEditOrder.approve();
        invoicedEditOrder.generateInboundOrder();
        invoicedEditOrder.generateInvoice();
        attempt("已开票后再次编辑采购单", () -> invoicedEditOrder.edit("PDA手持终端", 9, "仓储设备供应商B"));

        PurchaseOrder cancelledApproveOrder = new PurchaseOrder("PO-ERR-005");
        cancelledApproveOrder.edit("标签打印机", 3, "仓储设备供应商C");
        cancelledApproveOrder.cancel();
        attempt("已取消后再次审批", cancelledApproveOrder::approve);
        System.out.println();
    }

    private static void runReturnFlow() {
        System.out.println("========== 退货退款流程测试 ==========");
        PurchaseOrder order = new PurchaseOrder("PO-003");
        order.edit("冷链传感器", 12, "福州冷链物联有限公司");
        order.approve();
        order.generateInboundOrder();
        order.returnAndRefund();
        System.out.println("最终状态：" + order.getStateName());
        order.showInfo();
        System.out.println();
    }

    private static void attempt(String description, Runnable operation) {
        try {
            operation.run();
            System.out.println("未预期成功：" + description);
        } catch (BusinessException exception) {
            System.out.println("拦截成功：" + description);
            System.out.println("失败原因：" + exception.getMessage());
        }
    }
}
