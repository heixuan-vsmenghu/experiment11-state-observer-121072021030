# 实验11 状态模式与课堂观察者模式任务

## 一、基本信息

- 课程名称：软件体系结构与设计模式
- 年级专业：2023级软件工程
- 班级：软工2班
- 学号：121072021030
- 姓名：林立洲
- 建模工具：Enterprise Architect 12
- 开发工具：IntelliJ IDEA
- 开发语言：Java
- 项目管理：Maven

## 二、项目简介

本项目完成两部分内容：

1. 课堂任务：使用观察者模式设计机房温度监控响应系统。温度传感器作为目标对象，警示灯、报警器、安全逃生门、隔热门作为观察者对象；当温度超过阈值时，传感器统一通知所有响应设备。
2. 实验11：使用状态模式实现 WMS 仓储系统采购单全生命周期业务。采购单从草稿、已审批、已入库、已开票、已付款、已取消、已退货完结等状态之间流转，各状态类分别封装对应状态下允许和禁止的业务操作。

## 三、设计模式说明

### 观察者模式

观察者模式用于机房温度报警响应。`TemperatureSensor` 维护多个 `AlarmObserver`，当温度达到报警阈值时调用 `notifyObservers()`，分别通知 `WarningLight`、`AlarmBell`、`EscapeDoor`、`InsulationDoor` 执行闪烁、报警、开门、关门等响应动作。

### 状态模式

状态模式用于采购单生命周期业务控制。`PurchaseOrder` 是环境类，持有当前 `PurchaseOrderState`。业务方法不在 `PurchaseOrder` 中使用大量 `if-else` 或 `switch` 判断状态，而是委托给当前状态对象处理。`DraftState`、`ApprovedState`、`InStockState`、`InvoicedState`、`PaidState`、`CancelledState`、`ReturnedState` 分别封装对应状态下的操作权限、状态流转和非法操作拦截逻辑。

## 四、项目结构

```text
experiment11-state-observer
├── pom.xml
├── README.md
├── src/main/java/com/lzl/experiment11
│   ├── MainApp.java
│   ├── observer
│   │   ├── AlarmObserver.java
│   │   ├── Subject.java
│   │   ├── TemperatureSensor.java
│   │   ├── WarningLight.java
│   │   ├── AlarmBell.java
│   │   ├── EscapeDoor.java
│   │   ├── InsulationDoor.java
│   │   └── ObserverTaskDemo.java
│   └── state
│       ├── PurchaseOrder.java
│       ├── PurchaseOrderState.java
│       ├── AbstractPurchaseOrderState.java
│       ├── DraftState.java
│       ├── ApprovedState.java
│       ├── InStockState.java
│       ├── InvoicedState.java
│       ├── PaidState.java
│       ├── CancelledState.java
│       ├── ReturnedState.java
│       ├── OperationLog.java
│       ├── BusinessException.java
│       └── Experiment11Demo.java
├── report-images
└── 实验11_121072021030_林立洲.eap
```

## 五、UML 图说明

UML 图已在 Enterprise Architect 12 中建立并导出到 `report-images/`：

- `01_observer_class_diagram.png`：课堂任务机房监控观察者模式类图
- `02_observer_sequence_diagram.png`：课堂任务机房监控观察者模式顺序图
- `03_state_class_diagram.png`：实验11 WMS 采购单状态模式类图
- `04_state_state_diagram.png`：实验11 WMS 采购单状态流转图
- `05_state_normal_sequence.png`：实验11 WMS 采购单正常流程顺序图
- `06_state_invalid_sequence.png`：实验11 WMS 采购单非法操作拦截顺序图
- `07_state_return_sequence.png`：实验11 WMS 采购单退货退款流程顺序图

EA 项目文件：`实验11_121072021030_林立洲.eap`。

## 六、运行方式

Maven 构建：

```bash
mvn clean package
```

运行主程序：

```bash
java -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dstdout.encoding=UTF-8 -jar target/experiment11-state-observer-1.0.0.jar
```

也可以在 IntelliJ IDEA 中直接运行：

```text
com.lzl.experiment11.MainApp
```

## 七、测试结果说明

程序入口 `MainApp.main()` 依次运行课堂观察者模式任务和实验11状态模式任务。测试覆盖：

- 观察者模式：温度达到 80.0℃ 后，传感器通知警示灯、报警器、安全逃生门、隔热门。
- 正常付款流程：`PO-001` 从草稿、已审批、已入库、已开票流转到已付款。
- 取消流程：`PO-002` 审批后取消，最终状态为已取消。
- 退货退款流程：`PO-003` 审批、入库后退货退款，最终状态为已退货完结。
- 非法操作拦截：草稿直接入库、草稿直接付款、已入库取消、已开票后编辑、已取消后审批均被 `BusinessException` 拦截，并输出明确中文原因。
- 操作日志：每个采购单在最终输出时展示状态变更历史。

运行结果保存在：

- `run-result.txt`
- `maven-package-result.txt`
- `git-result.txt`

## 八、GitHub/Gitee 仓库地址

仓库地址：[https://github.com/heixuan-vsmenghu/experiment11-state-observer-121072021030](https://github.com/heixuan-vsmenghu/experiment11-state-observer-121072021030)

本地 Git 仓库已创建并提交，项目已推送到 GitHub 公开仓库。后续手动推送命令如下：

```bash
git remote add origin <仓库地址>
git branch -M main
git push -u origin main
```
