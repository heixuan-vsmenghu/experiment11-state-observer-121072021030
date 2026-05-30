from pathlib import Path
from docx import Document
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Inches, Pt

ROOT = Path(__file__).resolve().parents[1]
IMG = ROOT / "report-images"
REPORT = ROOT / "实验11_121072021030_林立洲.docx"


def set_font(run, font_name="宋体", size=11):
    run.font.name = font_name
    run._element.rPr.rFonts.set(qn("w:eastAsia"), font_name)
    run.font.size = Pt(size)


def add_heading(document, text, level=1):
    paragraph = document.add_heading("", level=level)
    run = paragraph.add_run(text)
    set_font(run, "黑体", 16 if level == 1 else 14)
    run.bold = True
    return paragraph


def add_paragraph(document, text):
    paragraph = document.add_paragraph()
    paragraph.paragraph_format.first_line_indent = Pt(22)
    paragraph.paragraph_format.line_spacing = 1.25
    run = paragraph.add_run(text)
    set_font(run)
    return paragraph


def add_plain_paragraph(document, text, align=None):
    paragraph = document.add_paragraph()
    if align:
        paragraph.alignment = align
    run = paragraph.add_run(text)
    set_font(run)
    return paragraph


def add_code(document, code):
    paragraph = document.add_paragraph()
    paragraph.paragraph_format.left_indent = Pt(18)
    paragraph.paragraph_format.space_before = Pt(4)
    paragraph.paragraph_format.space_after = Pt(4)
    run = paragraph.add_run(code.strip())
    set_font(run, "Consolas", 9)
    shading = OxmlElement("w:shd")
    shading.set(qn("w:fill"), "F2F2F2")
    paragraph._p.get_or_add_pPr().append(shading)
    return paragraph


def add_picture(document, filename, caption, width=6.3):
    path = IMG / filename
    if not path.exists():
        add_plain_paragraph(document, f"图缺失：{filename}")
        return
    paragraph = document.add_paragraph()
    paragraph.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = paragraph.add_run()
    run.add_picture(str(path), width=Inches(width))
    caption_paragraph = document.add_paragraph()
    caption_paragraph.alignment = WD_ALIGN_PARAGRAPH.CENTER
    caption_run = caption_paragraph.add_run(caption)
    set_font(caption_run, "宋体", 10)


def add_info_table(document):
    table = document.add_table(rows=8, cols=2)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.style = "Table Grid"
    rows = [
        ("实验名称", "实验11 状态模式"),
        ("课程名称", "软件体系结构与设计模式"),
        ("年级专业", "2023级软件工程"),
        ("班级", "软工2班"),
        ("学号", "121072021030"),
        ("姓名", "林立洲"),
        ("实验环境", "Windows、Enterprise Architect 12、IntelliJ IDEA、Java、Maven"),
        ("项目名称", "experiment11-state-observer"),
    ]
    for row, (key, value) in zip(table.rows, rows):
        row.cells[0].text = key
        row.cells[1].text = value
        for cell in row.cells:
            for paragraph in cell.paragraphs:
                for run in paragraph.runs:
                    set_font(run)


def main():
    document = Document()
    section = document.sections[0]
    section.top_margin = Inches(0.8)
    section.bottom_margin = Inches(0.8)
    section.left_margin = Inches(0.9)
    section.right_margin = Inches(0.9)

    title = document.add_paragraph()
    title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = title.add_run("《软件体系结构与设计模式》实验报告")
    set_font(run, "黑体", 20)
    run.bold = True

    subtitle = document.add_paragraph()
    subtitle.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = subtitle.add_run("实验11 状态模式与课堂观察者模式任务")
    set_font(run, "黑体", 16)
    run.bold = True

    add_info_table(document)

    add_heading(document, "一、实验目的")
    add_paragraph(document, "本次实验通过观察者模式和状态模式两个任务，训练先分析业务需求、再进行 UML 建模、最后使用 Java 编码实现并验证的完整开发过程。观察者模式部分主要掌握目标对象与观察者对象之间的一对多通知机制；状态模式部分主要掌握对象状态变化导致行为变化的设计思想。")
    add_paragraph(document, "通过机房温度监控场景，可以理解当一个目标对象发生变化时，多个依赖对象如何自动收到通知并执行响应动作。通过 WMS 仓储系统采购单场景，可以理解在业务流程较长、状态规则较多时，如何把不同状态下的操作权限封装到独立状态类中，从而避免在环境类中堆积大量条件判断。")
    add_paragraph(document, "实验还要求使用 Enterprise Architect 绘制 UML 类图、状态图和顺序图，并将设计结果与 Java 代码实现保持一致。通过 Maven 构建、主程序运行、非法操作拦截和 Git 仓库整理，验证系统设计是否符合课堂任务和实验要求。")

    add_heading(document, "二、实验任务与需求分析")
    add_heading(document, "2.1 课堂任务：观察者模式", 2)
    add_paragraph(document, "课堂任务要求设计一个机房监控系统。当温度传感器检测到机房温度达到指定阈值时，需要把高温信号传递给多个响应设备。响应设备包括警示灯、报警器、安全逃生门和隔热门。警示灯负责闪烁，报警器负责报警，安全逃生门自动开启，隔热门自动关闭。")
    add_paragraph(document, "该场景中温度传感器与响应设备之间是一对多关系，传感器不应该直接绑定某一个具体设备，而是应当维护观察者集合。这样当后续增加新的响应设备时，只需要实现统一的观察者接口并注册到传感器中，不需要修改传感器核心逻辑。因此本任务适合采用观察者模式。")

    add_heading(document, "2.2 实验11：状态模式", 2)
    add_paragraph(document, "实验11要求基于状态模式实现 WMS 仓储系统采购单的完整生命周期。采购单状态包括草稿、已审批、已入库、已开票、已付款、已取消和已退货完结。系统需要支持编辑采购单、审批采购单、取消采购单、生成入库单、生成发票、退货退款和付款七类业务操作。")
    add_paragraph(document, "采购单业务规则的核心特点是状态决定行为。例如草稿状态允许编辑、审批和取消，但不允许直接入库、开票或付款；已审批状态允许生成入库单和取消，但不允许重复审批；已入库状态允许生成发票或退货退款，但禁止直接取消；已开票状态允许付款或退货退款；已取消和已退货完结属于终止状态，后续业务操作均应被拦截。")
    add_paragraph(document, "如果把所有状态规则都写在 PurchaseOrder 中，代码会逐渐变成大量 if-else 或 switch 判断，既不利于阅读，也不利于扩展。本实验采用状态模式，将每种状态封装为独立状态类，由当前状态对象负责处理业务操作和状态流转。")

    add_heading(document, "三、系统设计")
    add_heading(document, "3.1 观察者模式设计", 2)
    add_paragraph(document, "观察者模式设计中，Subject 表示抽象目标，定义 attach、detach 和 notifyObservers 方法；AlarmObserver 表示抽象观察者，定义 update 方法；TemperatureSensor 是具体目标对象，保存当前温度、报警阈值和观察者集合；WarningLight、AlarmBell、EscapeDoor、InsulationDoor 是具体观察者，分别完成警示灯闪烁、报警器报警、逃生门开启和隔热门关闭。")
    add_paragraph(document, "ObserverTaskDemo 作为客户端测试类，负责创建温度传感器和四类响应设备，将响应设备注册到传感器中，并模拟温度达到 80.0℃ 的高温场景。传感器在检测到温度超过阈值后，统一调用 notifyObservers 通知所有观察者。")
    add_picture(document, "01_observer_class_diagram.png", "图1 课堂任务机房监控观察者模式类图")
    add_picture(document, "02_observer_sequence_diagram.png", "图2 课堂任务机房监控观察者模式顺序图", width=6.7)

    add_heading(document, "3.2 状态模式设计", 2)
    add_paragraph(document, "状态模式设计中，PurchaseOrder 是环境类 Context，内部持有当前状态对象 state 和操作日志集合 operationLogs。PurchaseOrderState 是抽象状态接口，规定采购单所有业务操作的方法。AbstractPurchaseOrderState 是抽象状态基类，为非法操作提供统一默认处理，默认抛出 BusinessException 并说明失败原因。")
    add_paragraph(document, "DraftState、ApprovedState、InStockState、InvoicedState、PaidState、CancelledState、ReturnedState 分别表示七种业务状态。每个状态类只关心自己状态下允许的操作和禁止的操作，例如 DraftState 允许 edit、approve、cancel；ApprovedState 允许 generateInboundOrder 和 cancel；InStockState 允许 generateInvoice 和 returnAndRefund；InvoicedState 允许 pay 和 returnAndRefund。")
    add_paragraph(document, "本实验将已付款状态视为付款完成状态，不再从已付款状态触发退货退款。退货退款主要在已入库和已开票状态触发，这样可以覆盖退货退款功能，同时避免与付款完结逻辑冲突。OperationLog 用于记录每次合法操作的操作名称、原状态、新状态、操作结果和时间，便于测试时查看状态流转历史。")
    add_picture(document, "03_state_class_diagram.png", "图3 实验11 WMS采购单状态模式类图", width=6.7)
    add_picture(document, "04_state_state_diagram.png", "图4 实验11 WMS采购单状态流转图", width=6.7)
    add_picture(document, "05_state_normal_sequence.png", "图5 实验11 WMS采购单正常流程顺序图", width=6.7)
    add_picture(document, "06_state_invalid_sequence.png", "图6 实验11 WMS采购单非法操作拦截顺序图", width=6.7)
    add_picture(document, "07_state_return_sequence.png", "图7 实验11 WMS采购单退货退款流程顺序图", width=6.7)

    add_heading(document, "四、系统实现")
    add_heading(document, "4.1 项目结构", 2)
    add_paragraph(document, "项目使用 Maven 管理，主入口为 com.lzl.experiment11.MainApp。observer 包存放课堂观察者模式任务代码，state 包存放实验11状态模式代码。报告图片、运行结果和 EA 项目文件保存在项目根目录下，便于统一提交和检查。")
    add_picture(document, "08_project_structure.png", "图8 项目结构截图", width=5.8)

    add_heading(document, "4.2 观察者模式核心代码", 2)
    add_paragraph(document, "AlarmObserver 是所有响应设备共同实现的接口，TemperatureSensor 通过 Subject 接口维护观察者集合。传感器只依赖 AlarmObserver 抽象接口，不直接依赖具体设备类。")
    add_code(document, """
public interface AlarmObserver {
    void update(double temperature);
}

public class TemperatureSensor implements Subject {
    private double temperature;
    private final double threshold;
    private final List<AlarmObserver> observers = new ArrayList<>();

    public void attach(AlarmObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        for (AlarmObserver observer : observers) {
            observer.update(temperature);
        }
    }
}
""")
    add_paragraph(document, "当 setTemperature 方法接收到新的温度值后，调用 checkTemperature 判断是否超过阈值。若超过阈值，传感器不需要知道具体响应设备是什么，只需要统一调用观察者的 update 方法。")
    add_code(document, """
public void setTemperature(double temperature) {
    this.temperature = temperature;
    System.out.printf("温度传感器检测到当前温度：%.1f℃%n", temperature);
    checkTemperature();
}

public void checkTemperature() {
    if (temperature >= threshold) {
        notifyObservers();
        return;
    }
    System.out.printf("当前温度未达到报警阈值 %.1f℃，系统保持正常监控。%n", threshold);
}
""")
    add_paragraph(document, "具体观察者只负责自己的响应动作。例如 WarningLight 在收到高温通知后调用 flash 方法，实现警示灯闪烁。")
    add_code(document, """
public class WarningLight implements AlarmObserver {
    public void update(double temperature) {
        System.out.printf("警示灯接收到 %.1f℃ 高温信号。%n", temperature);
        flash();
    }

    public void flash() {
        System.out.println("警示灯开始闪烁");
    }
}
""")

    add_heading(document, "4.3 状态模式核心代码", 2)
    add_paragraph(document, "PurchaseOrder 的业务方法只负责把请求委托给当前状态对象，不在环境类中集中判断状态。这样新增状态或调整状态规则时，修改范围主要集中在具体状态类。")
    add_code(document, """
public void approve() {
    state.approve(this);
}

public void generateInboundOrder() {
    state.generateInboundOrder(this);
}

public void pay() {
    state.pay(this);
}
""")
    add_paragraph(document, "PurchaseOrderState 接口统一规定采购单支持的业务操作，所有具体状态类都通过该接口对外提供一致的处理方式。")
    add_code(document, """
public interface PurchaseOrderState {
    void edit(PurchaseOrder order, String productName, int quantity, String supplier);
    void approve(PurchaseOrder order);
    void cancel(PurchaseOrder order);
    void generateInboundOrder(PurchaseOrder order);
    void generateInvoice(PurchaseOrder order);
    void pay(PurchaseOrder order);
    void returnAndRefund(PurchaseOrder order);
    String getStateName();
}
""")
    add_paragraph(document, "AbstractPurchaseOrderState 为非法操作提供默认实现。具体状态类只需要覆盖自己允许的操作，未覆盖的操作自动进入统一拦截逻辑。")
    add_code(document, """
protected void reject(String operation, String reason) {
    throw new BusinessException(
        operation + "失败：当前状态【" + getStateName() + "】不满足业务规则。" + reason
    );
}
""")
    add_paragraph(document, "DraftState 允许编辑、审批和取消。审批成功后切换到 ApprovedState，取消成功后切换到 CancelledState。")
    add_code(document, """
public class DraftState extends AbstractPurchaseOrderState {
    public void approve(PurchaseOrder order) {
        order.changeState(new ApprovedState(), "审批采购单",
                "审批通过，采购单进入已审批状态。");
    }

    public void cancel(PurchaseOrder order) {
        order.changeState(new CancelledState(), "取消采购单",
                "采购单尚未入库，取消成功。");
    }
}
""")
    add_paragraph(document, "ApprovedState 允许生成入库单和取消采购单；InStockState 允许生成发票和退货退款；InvoicedState 允许付款和退货退款。这些规则分散在对应状态类中，使业务含义更直观。")
    add_code(document, """
public class InStockState extends AbstractPurchaseOrderState {
    public void generateInvoice(PurchaseOrder order) {
        order.changeState(new InvoicedState(), "生成发票",
                "采购单已完成入库，发票生成成功。");
    }

    public void returnAndRefund(PurchaseOrder order) {
        order.changeState(new ReturnedState(), "退货退款",
                "已入库采购单退货退款成功，流程完结。");
    }
}
""")
    add_paragraph(document, "每次合法操作都会通过 recordOperation 或 changeState 写入 OperationLog，并在控制台输出操作名称、原状态、新状态和操作结果，便于测试时追踪完整生命周期。")

    add_heading(document, "五、测试与运行结果")
    add_heading(document, "5.1 Maven 构建结果", 2)
    add_paragraph(document, "项目使用 Maven 执行 clean package，编译 22 个 Java 源文件并生成 target/experiment11-state-observer-1.0.0.jar。构建结果显示 BUILD SUCCESS。")
    add_picture(document, "14_maven_package_success.png", "图9 Maven package 成功结果", width=6.5)

    add_heading(document, "5.2 观察者模式运行结果", 2)
    add_paragraph(document, "运行课堂任务时，程序先注册 WarningLight、AlarmBell、EscapeDoor 和 InsulationDoor 四个响应设备。随后传感器检测到当前温度为 80.0℃，超过 60.0℃ 阈值，因此通知所有观察者。运行结果表明四个设备都按设计完成了响应。")
    add_picture(document, "10_observer_run_result.png", "图10 观察者模式运行结果", width=6.2)

    add_heading(document, "5.3 状态模式正常流程测试", 2)
    add_paragraph(document, "正常付款流程使用采购单 PO-001。该采购单依次执行编辑、审批、生成入库单、生成发票和付款操作，状态从草稿流转到已审批、已入库、已开票，最终到达已付款。每一步都记录操作日志，最终输出状态为已付款。")
    add_picture(document, "11_state_normal_result.png", "图11 状态模式正常流程运行结果", width=6.5)

    add_heading(document, "5.4 状态模式取消流程测试", 2)
    add_paragraph(document, "取消流程使用采购单 PO-002。采购单先在草稿状态完成编辑，再审批为已审批状态。由于此时还没有入库，系统允许取消采购单，最终状态为已取消。该流程验证了入库前允许取消、入库后不允许取消的业务边界。")

    add_heading(document, "5.5 状态模式退货退款流程测试", 2)
    add_paragraph(document, "退货退款流程使用采购单 PO-003。该采购单完成编辑、审批和入库后，处于已入库状态。在该状态下调用 returnAndRefund 方法，由 InStockState 将状态切换为 ReturnedState，最终状态为已退货完结。")
    add_picture(document, "13_state_return_result.png", "图12 退货退款流程运行结果", width=6.5)

    add_heading(document, "5.6 非法操作拦截测试", 2)
    add_paragraph(document, "非法流程测试覆盖草稿状态直接生成入库单、草稿状态直接付款、已入库状态取消采购单、已开票后再次编辑采购单、已取消后再次审批等场景。所有非法操作均由当前状态类或抽象状态基类抛出 BusinessException，并输出清晰的中文失败原因。")
    add_picture(document, "12_state_invalid_result.png", "图13 非法操作拦截运行结果", width=6.5)
    add_paragraph(document, "完整运行结果已保存到 run-result.txt，能够看到 MainApp 按顺序执行观察者模式和状态模式的全部测试。")
    add_picture(document, "09_mainapp_run_result.png", "图14 MainApp 完整运行结果截图", width=6.5)

    add_heading(document, "六、Git 仓库说明")
    add_paragraph(document, "本项目已在本地建立 Git 仓库，并按要求提交本次实验代码、EA 项目文件、UML 图片、运行结果文件、README 和实验报告。项目已推送到 GitHub 公开仓库，仓库地址为：https://github.com/heixuan-vsmenghu/experiment11-state-observer-121072021030。")
    add_paragraph(document, "后续如果继续修改项目，可使用以下命令将本地提交推送到远程仓库：")
    add_code(document, """
git remote add origin <仓库地址>
git branch -M main
git push -u origin main
""")
    if (IMG / "15_git_commit_success.png").exists():
        add_picture(document, "15_git_commit_success.png", "图15 Git commit 成功结果", width=6.5)

    add_heading(document, "七、实验总结")
    add_paragraph(document, "通过本次实验，我进一步理解了观察者模式中一对多通知机制的设计思想。温度传感器只负责维护观察者集合并发布通知，具体响应设备只负责各自的响应动作。这种方式降低了传感器与响应设备之间的耦合，也使后续扩展新的响应设备更加方便。")
    add_paragraph(document, "通过状态模式实现 WMS 采购单生命周期，我体会到“状态决定行为”的业务建模方式。采购单在不同状态下允许执行的操作不同，如果把所有判断都写在 PurchaseOrder 中，代码会很快变得复杂。本实验将状态规则分散到不同状态类中后，正常流程、取消流程、退货退款流程和非法操作拦截都更清晰。")
    add_paragraph(document, "本次实验中较难的部分是把业务规则转换成状态流转关系，尤其是已入库、已开票、已付款、已取消和已退货完结之间的边界。通过先绘制 UML 状态图和顺序图，再编写代码，可以在实现前明确每一步状态变化，减少编码时的混乱。")
    add_paragraph(document, "总体来看，观察者模式适合处理对象之间的通知关系，状态模式适合处理状态较多、行为随状态变化的业务流程。通过本次实验，我不仅完成了 Java 编码，也完成了 UML 建模、运行验证、日志输出和 Git 仓库整理，形成了比较完整的实验交付过程。")

    add_heading(document, "八、附录")
    add_paragraph(document, "运行命令：mvn clean package；java -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dstdout.encoding=UTF-8 -jar target/experiment11-state-observer-1.0.0.jar。")
    add_paragraph(document, "重要文件包括 pom.xml、README.md、src/main/java/com/lzl/experiment11、实验11_121072021030_林立洲.eap、report-images、run-result.txt、maven-package-result.txt、git-result.txt 和本 Word 报告。")
    add_paragraph(document, "重要截图清单包括 7 张 EA UML 图、项目结构截图、MainApp 完整运行结果截图、观察者模式运行结果截图、状态模式正常流程截图、非法操作拦截图、退货退款流程截图、Maven 构建截图和 Git 提交截图。")

    document.save(REPORT)
    print(REPORT)


if __name__ == "__main__":
    main()
