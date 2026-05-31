from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
IMAGE_DIR = ROOT / "report-images"
IMAGE_DIR.mkdir(exist_ok=True)

FONT_PATHS = [
    Path("C:/Windows/Fonts/msyh.ttc"),
    Path("C:/Windows/Fonts/simhei.ttf"),
    Path("C:/Windows/Fonts/simsun.ttc"),
]


def load_font(size):
    for path in FONT_PATHS:
        if path.exists():
            return ImageFont.truetype(str(path), size)
    return ImageFont.load_default()


FONT = load_font(18)
SMALL_FONT = load_font(16)


def wrap_line(draw, line, font, max_width):
    if not line:
        return [""]
    pieces = []
    current = ""
    for char in line:
        candidate = current + char
        if draw.textlength(candidate, font=font) <= max_width:
            current = candidate
        else:
            pieces.append(current)
            current = char
    if current:
        pieces.append(current)
    return pieces


def render_text_image(title, text, output_name, width=1500, max_lines=None):
    probe = Image.new("RGB", (width, 100), "white")
    draw = ImageDraw.Draw(probe)
    content_width = width - 64
    raw_lines = text.splitlines()
    if max_lines and len(raw_lines) > max_lines:
        raw_lines = raw_lines[:max_lines] + ["……后续内容详见对应 txt 文件。"]

    lines = []
    for raw_line in raw_lines:
        lines.extend(wrap_line(draw, raw_line, FONT, content_width))

    line_height = 28
    title_height = 48
    height = max(180, title_height + 42 + line_height * len(lines))
    image = Image.new("RGB", (width, height), (30, 32, 36))
    draw = ImageDraw.Draw(image)
    draw.rectangle((0, 0, width, 48), fill=(46, 50, 56))
    draw.ellipse((18, 16, 30, 28), fill=(255, 95, 87))
    draw.ellipse((38, 16, 50, 28), fill=(255, 189, 46))
    draw.ellipse((58, 16, 70, 28), fill=(39, 201, 63))
    draw.text((92, 12), title, fill=(238, 238, 238), font=SMALL_FONT)

    y = 66
    for line in lines:
        draw.text((32, y), line, fill=(236, 239, 244), font=FONT)
        y += line_height

    image.save(IMAGE_DIR / output_name)


def read_text(name):
    path = ROOT / name
    if not path.exists():
        return ""
    data = path.read_bytes()
    if data.startswith(b"\xff\xfe") or data.startswith(b"\xfe\xff"):
        return data.decode("utf-16", errors="replace")
    if data.startswith(b"\xef\xbb\xbf"):
        return data.decode("utf-8-sig", errors="replace")
    return data.decode("utf-8", errors="replace")


def between(text, start, end=None):
    start_index = text.find(start)
    if start_index < 0:
        return ""
    if end is None:
        return text[start_index:]
    end_index = text.find(end, start_index + len(start))
    if end_index < 0:
        return text[start_index:]
    return text[start_index:end_index]


def project_structure_text():
    return """experiment11-state-observer
├── pom.xml
├── README.md
├── 实验11_121072021030_林立洲.eap
├── 实验11_121072021030_林立洲.docx
├── run-result.txt
├── maven-package-result.txt
├── git-result.txt
├── report-images
│   ├── 01_observer_class_diagram.png
│   ├── 02_observer_sequence_diagram.png
│   ├── 03_state_class_diagram.png
│   ├── 04_state_state_diagram.png
│   ├── 05_state_normal_sequence.png
│   ├── 06_state_invalid_sequence.png
│   ├── 07_state_return_sequence.png
│   └── 16_advanced_bonus_result.png
└── src/main/java/com/lzl/experiment11
    ├── MainApp.java
    ├── observer
    │   ├── AlarmObserver.java
    │   ├── Subject.java
    │   ├── TemperatureSensor.java
    │   ├── WarningLight.java
    │   ├── AlarmBell.java
    │   ├── EscapeDoor.java
    │   ├── InsulationDoor.java
    │   └── ObserverTaskDemo.java
    └── state
        ├── PurchaseOrder.java
        ├── PurchaseOrderState.java
        ├── AbstractPurchaseOrderState.java
        ├── DraftState.java
        ├── ApprovedState.java
        ├── PartiallyInStockState.java
        ├── InStockState.java
        ├── PartiallyInvoicedState.java
        ├── InvoicedState.java
        ├── PaidState.java
        ├── CancelledState.java
        ├── ReturnedState.java
        ├── OperationLog.java
        ├── UserRole.java
        ├── DocumentType.java
        ├── BusinessDocument.java
        ├── BusinessDocumentFactory.java
        ├── InboundOrderDocument.java
        ├── InvoiceDocument.java
        ├── ReturnOrderDocument.java
        ├── validation
        │   ├── OrderValidationRule.java
        │   └── OrderValidationChains.java
        ├── BusinessException.java
        └── Experiment11Demo.java"""


def main():
    run_result = read_text("run-result.txt")
    maven_result = read_text("maven-package-result.txt")
    git_result = read_text("git-result.txt")

    render_text_image("IntelliJ 项目结构", project_structure_text(), "08_project_structure.png", width=1200)
    render_text_image("MainApp 完整运行结果", run_result, "09_mainapp_run_result.png", width=1500)
    render_text_image(
        "观察者模式运行结果",
        between(run_result, "========== 课堂任务：观察者模式 ==========", "========== 实验11：状态模式 =========="),
        "10_observer_run_result.png",
        width=1300,
    )
    render_text_image(
        "状态模式正常流程运行结果",
        between(run_result, "========== 正常流程测试 ==========", "========== 取消流程测试 =========="),
        "11_state_normal_result.png",
        width=1500,
    )
    render_text_image(
        "非法操作拦截运行结果",
        between(run_result, "========== 非法流程测试 ==========", "========== 退货退款流程测试 =========="),
        "12_state_invalid_result.png",
        width=1500,
    )
    render_text_image(
        "退货退款流程运行结果",
        between(run_result, "========== 退货退款流程测试 ==========", "========== 进阶加分流程测试 =========="),
        "13_state_return_result.png",
        width=1500,
    )
    render_text_image(
        "进阶加分流程运行结果",
        between(run_result, "========== 进阶加分流程测试 =========="),
        "16_advanced_bonus_result.png",
        width=1500,
    )
    render_text_image(
        "Maven package 成功结果",
        maven_result,
        "14_maven_package_success.png",
        width=1500,
    )

    if git_result:
        render_text_image("Git commit 成功结果", git_result, "15_git_commit_success.png", width=1500)


if __name__ == "__main__":
    main()
