$ErrorActionPreference = "Stop"

$workspace = Split-Path -Parent $PSScriptRoot
$target = Join-Path $workspace "实验11_121072021030_林立洲.eap"
$imageDir = Join-Path $workspace "report-images"
$base = "C:\Program Files (x86)\Sparx Systems\EA\EABase.eap"

if (-not (Test-Path $base)) {
    throw "Cannot find EA base model: $base"
}

New-Item -ItemType Directory -Force -Path $imageDir | Out-Null
Copy-Item -LiteralPath $base -Destination $target -Force

$repo = New-Object -ComObject "EA.Repository"
if (-not $repo.OpenFile($target)) {
    throw "Enterprise Architect failed to open $target"
}

function Add-Package($parent, [string]$name) {
    $pkg = $parent.Packages.AddNew($name, "Package")
    [void]$pkg.Update()
    [void]$parent.Packages.Refresh()
    return $pkg
}

function Add-Element($pkg, [string]$name, [string]$type) {
    $element = $pkg.Elements.AddNew($name, $type)
    [void]$element.Update()
    [void]$pkg.Elements.Refresh()
    return $element
}

function Add-Attribute($element, [string]$name, [string]$type) {
    $attr = $element.Attributes.AddNew($name, $type)
    $attr.Visibility = "Private"
    [void]$attr.Update()
    [void]$element.Attributes.Refresh()
}

function Add-Method($element, [string]$name, [string]$returnType = "void") {
    $methodName = $name
    $parameterText = ""
    if ($name -match "^(.+?)\((.*)\)$") {
        $methodName = $Matches[1].Trim()
        $parameterText = $Matches[2].Trim()
    }

    $method = $element.Methods.AddNew($methodName, "")
    $method.Visibility = "Public"
    $method.ReturnType = $returnType
    [void]$method.Update()
    if ($parameterText.Length -gt 0) {
        foreach ($rawParameter in $parameterText.Split(",")) {
            $pieces = $rawParameter.Trim().Split(":")
            $parameterName = $pieces[0].Trim()
            $parameterType = "Object"
            if ($pieces.Count -gt 1) {
                $parameterType = $pieces[1].Trim()
            }
            $parameter = $method.Parameters.AddNew($parameterName, $parameterType)
            [void]$parameter.Update()
        }
        [void]$method.Parameters.Refresh()
        [void]$method.Update()
    }
    [void]$element.Methods.Refresh()
}

function Add-Connector($client, $supplier, [string]$type, [string]$name = "") {
    $connector = $client.Connectors.AddNew($name, $type)
    $connector.SupplierID = $supplier.ElementID
    [void]$connector.Update()
    [void]$client.Connectors.Refresh()
    return $connector
}

function Set-ConnectorType($repo, $connector, [string]$type) {
    $connectorId = $connector.ConnectorID
    $repo.Execute("update t_connector set Connector_Type='$type' where Connector_ID=$connectorId")
}

function Add-Realisation($repo, $client, $supplier) {
    $connector = Add-Connector $client $supplier "Dependency"
    Set-ConnectorType $repo $connector "Realisation"
}

function Add-StateFlow($repo, $client, $supplier, [string]$name) {
    $connector = Add-Connector $client $supplier "Dependency" $name
    Set-ConnectorType $repo $connector "StateFlow"
}

function Add-ToDiagram($diagram, $element, [int]$left, [int]$top, [int]$width = 180, [int]$height = 95) {
    $right = $left + $width
    $bottom = $top + $height
    $obj = $diagram.DiagramObjects.AddNew("l=$left;r=$right;t=$top;b=$bottom;", "")
    $obj.ElementID = $element.ElementID
    [void]$obj.Update()
    [void]$diagram.DiagramObjects.Refresh()
}

function Add-Diagram($pkg, [string]$name, [string]$type) {
    $diagram = $pkg.Diagrams.AddNew($name, $type)
    [void]$diagram.Update()
    [void]$pkg.Diagrams.Refresh()
    return $diagram
}

function Add-Participant($pkg, $diagram, [string]$name, [int]$left) {
    $element = Add-Element $pkg $name "Object"
    Add-ToDiagram $diagram $element $left 45 160 720
    return $element
}

function Add-Message($from, $to, [string]$name, [int]$sequenceNo) {
    $message = $from.Connectors.AddNew($name, "Sequence")
    $message.SupplierID = $to.ElementID
    $message.SequenceNo = [string]$sequenceNo
    [void]$message.Update()
    [void]$from.Connectors.Refresh()
}

function Export-Diagram($diagram, [string]$fileName) {
    $path = Join-Path $imageDir $fileName
    Remove-Item -LiteralPath $path -Force -ErrorAction SilentlyContinue
    [void]$repo.OpenDiagram($diagram.DiagramID)
    Start-Sleep -Milliseconds 700
    $project = $repo.GetProjectInterface()
    $project.SaveDiagramImageToFile($path)
    [void]$repo.CloseDiagram($diagram.DiagramID)
}

try {
    $model = $repo.Models.GetAt(0)
    $model.Name = "实验11_状态模式与观察者模式"
    [void]$model.Update()

    $observerPkg = Add-Package $model "课堂任务_观察者模式"
    $statePkg = Add-Package $model "实验11_状态模式_WMS采购单"

    $subject = Add-Element $observerPkg "Subject" "Interface"
    Add-Method $subject "attach(observer : AlarmObserver)" "void"
    Add-Method $subject "detach(observer : AlarmObserver)" "void"
    Add-Method $subject "notifyObservers()" "void"

    $alarmObserver = Add-Element $observerPkg "AlarmObserver" "Interface"
    Add-Method $alarmObserver "update(temperature : double)" "void"

    $sensor = Add-Element $observerPkg "TemperatureSensor" "Class"
    Add-Attribute $sensor "temperature" "double"
    Add-Attribute $sensor "threshold" "double"
    Add-Attribute $sensor "observers" "List<AlarmObserver>"
    "setTemperature(temperature : double)","checkTemperature()","notifyObservers()","attach(observer : AlarmObserver)","detach(observer : AlarmObserver)" |
        ForEach-Object { Add-Method $sensor $_ "void" }

    $warningLight = Add-Element $observerPkg "WarningLight" "Class"
    Add-Method $warningLight "update(temperature : double)" "void"
    Add-Method $warningLight "flash()" "void"
    $alarmBell = Add-Element $observerPkg "AlarmBell" "Class"
    Add-Method $alarmBell "update(temperature : double)" "void"
    Add-Method $alarmBell "alarm()" "void"
    $escapeDoor = Add-Element $observerPkg "EscapeDoor" "Class"
    Add-Method $escapeDoor "update(temperature : double)" "void"
    Add-Method $escapeDoor "open()" "void"
    $insulationDoor = Add-Element $observerPkg "InsulationDoor" "Class"
    Add-Method $insulationDoor "update(temperature : double)" "void"
    Add-Method $insulationDoor "close()" "void"
    $observerDemo = Add-Element $observerPkg "ObserverTaskDemo" "Class"
    Add-Method $observerDemo "run()" "void"

    Add-Realisation $repo $sensor $subject
    Add-Realisation $repo $warningLight $alarmObserver
    Add-Realisation $repo $alarmBell $alarmObserver
    Add-Realisation $repo $escapeDoor $alarmObserver
    Add-Realisation $repo $insulationDoor $alarmObserver
    [void](Add-Connector $sensor $alarmObserver "Aggregation" "observers")
    [void](Add-Connector $observerDemo $sensor "Dependency")
    [void](Add-Connector $observerDemo $warningLight "Dependency")
    [void](Add-Connector $observerDemo $alarmBell "Dependency")
    [void](Add-Connector $observerDemo $escapeDoor "Dependency")
    [void](Add-Connector $observerDemo $insulationDoor "Dependency")

    $observerClassDiagram = Add-Diagram $observerPkg "课堂任务_机房监控观察者模式类图" "Class"
    Add-ToDiagram $observerClassDiagram $subject 70 80 230 110
    Add-ToDiagram $observerClassDiagram $alarmObserver 455 70 245 95
    Add-ToDiagram $observerClassDiagram $sensor 70 270 285 165
    Add-ToDiagram $observerClassDiagram $warningLight 430 255 205 115
    Add-ToDiagram $observerClassDiagram $alarmBell 705 255 205 115
    Add-ToDiagram $observerClassDiagram $escapeDoor 430 435 205 115
    Add-ToDiagram $observerClassDiagram $insulationDoor 705 435 205 115
    Add-ToDiagram $observerClassDiagram $observerDemo 90 520 220 95
    [void]$observerClassDiagram.Update()

    $observerSeq = Add-Diagram $observerPkg "课堂任务_机房监控观察者模式顺序图" "Sequence"
    $oClient = Add-Participant $observerPkg $observerSeq "client : Client" 30
    $oSensor = Add-Participant $observerPkg $observerSeq "sensor : TemperatureSensor" 250
    $oLight = Add-Participant $observerPkg $observerSeq "light : WarningLight" 505
    $oBell = Add-Participant $observerPkg $observerSeq "bell : AlarmBell" 735
    $oEscape = Add-Participant $observerPkg $observerSeq "escapeDoor : EscapeDoor" 965
    $oInsulation = Add-Participant $observerPkg $observerSeq "insulationDoor : InsulationDoor" 1220
    $seq = 1
    foreach ($msg in @(
        @($oClient,$oSensor,"new TemperatureSensor(60.0)"),
        @($oClient,$oLight,"new WarningLight()"),
        @($oClient,$oBell,"new AlarmBell()"),
        @($oClient,$oEscape,"new EscapeDoor()"),
        @($oClient,$oInsulation,"new InsulationDoor()"),
        @($oClient,$oSensor,"attach(light)"),
        @($oClient,$oSensor,"attach(bell)"),
        @($oClient,$oSensor,"attach(escapeDoor)"),
        @($oClient,$oSensor,"attach(insulationDoor)"),
        @($oClient,$oSensor,"setTemperature(80.0)"),
        @($oSensor,$oSensor,"checkTemperature()"),
        @($oSensor,$oSensor,"notifyObservers()"),
        @($oSensor,$oLight,"update(80.0)"),
        @($oLight,$oLight,"flash()"),
        @($oSensor,$oBell,"update(80.0)"),
        @($oBell,$oBell,"alarm()"),
        @($oSensor,$oEscape,"update(80.0)"),
        @($oEscape,$oEscape,"open()"),
        @($oSensor,$oInsulation,"update(80.0)"),
        @($oInsulation,$oInsulation,"close()")
    )) {
        Add-Message $msg[0] $msg[1] $msg[2] $seq
        $seq++
    }
    [void]$observerSeq.Update()

    $purchaseOrder = Add-Element $statePkg "PurchaseOrder" "Class"
    "orderNo:String","supplier:String","productName:String","quantity:int","amount:double","state:PurchaseOrderState","operationLogs:List<OperationLog>" |
        ForEach-Object {
            $parts = $_.Split(":")
            Add-Attribute $purchaseOrder $parts[0] $parts[1]
        }
    "edit(productName : String, quantity : int, supplier : String)","approve()","cancel()","generateInboundOrder()","generateInvoice()","pay()","returnAndRefund()","setState(state : PurchaseOrderState)","addLog(operation : String, beforeState : String, afterState : String, message : String)","showInfo()" |
        ForEach-Object { Add-Method $purchaseOrder $_ "void" }
    Add-Method $purchaseOrder "getStateName()" "String"

    $state = Add-Element $statePkg "PurchaseOrderState" "Interface"
    "edit(order : PurchaseOrder, productName : String, quantity : int, supplier : String)","approve(order : PurchaseOrder)","cancel(order : PurchaseOrder)","generateInboundOrder(order : PurchaseOrder)","generateInvoice(order : PurchaseOrder)","pay(order : PurchaseOrder)","returnAndRefund(order : PurchaseOrder)" |
        ForEach-Object { Add-Method $state $_ "void" }
    Add-Method $state "getStateName()" "String"

    $abstractState = Add-Element $statePkg "AbstractPurchaseOrderState" "Class"
    $abstractState.Abstract = "1"
    [void]$abstractState.Update()
    Add-Method $abstractState "reject(operation : String, reason : String)" "void"

    $draft = Add-Element $statePkg "DraftState" "Class"
    "edit(order : PurchaseOrder, productName : String, quantity : int, supplier : String)","approve(order : PurchaseOrder)","cancel(order : PurchaseOrder)" |
        ForEach-Object { Add-Method $draft $_ "void" }
    Add-Method $draft "getStateName()" "String"
    $approved = Add-Element $statePkg "ApprovedState" "Class"
    "cancel(order : PurchaseOrder)","generateInboundOrder(order : PurchaseOrder)" |
        ForEach-Object { Add-Method $approved $_ "void" }
    Add-Method $approved "getStateName()" "String"
    $inStock = Add-Element $statePkg "InStockState" "Class"
    "cancel(order : PurchaseOrder)","generateInvoice(order : PurchaseOrder)","returnAndRefund(order : PurchaseOrder)" |
        ForEach-Object { Add-Method $inStock $_ "void" }
    Add-Method $inStock "getStateName()" "String"
    $invoiced = Add-Element $statePkg "InvoicedState" "Class"
    "edit(order : PurchaseOrder, productName : String, quantity : int, supplier : String)","cancel(order : PurchaseOrder)","pay(order : PurchaseOrder)","returnAndRefund(order : PurchaseOrder)" |
        ForEach-Object { Add-Method $invoiced $_ "void" }
    Add-Method $invoiced "getStateName()" "String"
    $paid = Add-Element $statePkg "PaidState" "Class"
    Add-Method $paid "returnAndRefund(order : PurchaseOrder)" "void"
    Add-Method $paid "getStateName()" "String"
    $cancelled = Add-Element $statePkg "CancelledState" "Class"
    "approve(order : PurchaseOrder)","cancel(order : PurchaseOrder)" |
        ForEach-Object { Add-Method $cancelled $_ "void" }
    Add-Method $cancelled "getStateName()" "String"
    $returned = Add-Element $statePkg "ReturnedState" "Class"
    Add-Method $returned "returnAndRefund(order : PurchaseOrder)" "void"
    Add-Method $returned "getStateName()" "String"

    $operationLog = Add-Element $statePkg "OperationLog" "Class"
    "operation:String","beforeState:String","afterState:String","message:String","time:LocalDateTime" |
        ForEach-Object {
            $parts = $_.Split(":")
            Add-Attribute $operationLog $parts[0] $parts[1]
        }
    Add-Method $operationLog "toString()" "String"
    $businessException = Add-Element $statePkg "BusinessException" "Class"
    Add-Method $businessException "BusinessException(message : String)" ""
    $experimentDemo = Add-Element $statePkg "Experiment11Demo" "Class"
    Add-Method $experimentDemo "run()" "void"

    [void](Add-Connector $purchaseOrder $state "Association" "state")
    [void](Add-Connector $purchaseOrder $operationLog "Aggregation" "operationLogs")
    Add-Realisation $repo $abstractState $state
    foreach ($concrete in @($draft, $approved, $inStock, $invoiced, $paid, $cancelled, $returned)) {
        [void](Add-Connector $concrete $abstractState "Generalization")
    }
    [void](Add-Connector $experimentDemo $purchaseOrder "Dependency")

    $stateClassDiagram = Add-Diagram $statePkg "实验11_WMS采购单状态模式类图" "Class"
    Add-ToDiagram $stateClassDiagram $purchaseOrder 45 75 420 230
    Add-ToDiagram $stateClassDiagram $state 610 65 455 215
    Add-ToDiagram $stateClassDiagram $abstractState 675 345 340 105
    Add-ToDiagram $stateClassDiagram $operationLog 70 395 300 145
    Add-ToDiagram $stateClassDiagram $businessException 1100 345 230 105
    Add-ToDiagram $stateClassDiagram $draft 35 635 220 145
    Add-ToDiagram $stateClassDiagram $approved 285 635 220 130
    Add-ToDiagram $stateClassDiagram $inStock 535 635 230 150
    Add-ToDiagram $stateClassDiagram $invoiced 795 635 245 170
    Add-ToDiagram $stateClassDiagram $paid 115 850 220 115
    Add-ToDiagram $stateClassDiagram $cancelled 400 850 220 125
    Add-ToDiagram $stateClassDiagram $returned 685 850 220 115
    Add-ToDiagram $stateClassDiagram $experimentDemo 1040 850 235 95
    [void]$stateClassDiagram.Update()

    $stateDiagram = Add-Diagram $statePkg "实验11_WMS采购单状态流转图" "Statechart"
    $sDraft = Add-Element $statePkg "草稿" "State"
    $sApproved = Add-Element $statePkg "已审批" "State"
    $sInStock = Add-Element $statePkg "已入库" "State"
    $sInvoiced = Add-Element $statePkg "已开票" "State"
    $sPaid = Add-Element $statePkg "已付款" "State"
    $sCancelled = Add-Element $statePkg "已取消" "State"
    $sReturned = Add-Element $statePkg "已退货完结" "State"
    Add-ToDiagram $stateDiagram $sDraft 65 120 150 80
    Add-ToDiagram $stateDiagram $sApproved 300 120 150 80
    Add-ToDiagram $stateDiagram $sInStock 545 120 150 80
    Add-ToDiagram $stateDiagram $sInvoiced 790 120 150 80
    Add-ToDiagram $stateDiagram $sPaid 1035 120 150 80
    Add-ToDiagram $stateDiagram $sCancelled 295 340 150 80
    Add-ToDiagram $stateDiagram $sReturned 690 340 170 80
    foreach ($transition in @(
        @($sDraft,$sApproved,"审批"),
        @($sDraft,$sCancelled,"取消"),
        @($sApproved,$sCancelled,"取消"),
        @($sApproved,$sInStock,"生成入库单"),
        @($sInStock,$sInvoiced,"生成发票"),
        @($sInStock,$sReturned,"退货退款"),
        @($sInvoiced,$sPaid,"付款"),
        @($sInvoiced,$sReturned,"退货退款")
    )) {
        Add-StateFlow $repo $transition[0] $transition[1] $transition[2]
    }
    [void]$stateDiagram.Update()

    $normalSeq = Add-Diagram $statePkg "实验11_WMS采购单正常流程顺序图" "Sequence"
    $nClient = Add-Participant $statePkg $normalSeq "client : Experiment11Demo" 30
    $nOrder = Add-Participant $statePkg $normalSeq "order : PurchaseOrder" 285
    $nDraft = Add-Participant $statePkg $normalSeq "state : DraftState" 540
    $nApproved = Add-Participant $statePkg $normalSeq "state : ApprovedState" 785
    $nInStock = Add-Participant $statePkg $normalSeq "state : InStockState" 1040
    $nInvoiced = Add-Participant $statePkg $normalSeq "state : InvoicedState" 1290
    $seq = 1
    foreach ($msg in @(
        @($nClient,$nOrder,"new PurchaseOrder(PO-001)"),
        @($nClient,$nOrder,"edit(product, quantity, supplier)"),
        @($nOrder,$nDraft,"edit(order, product, quantity, supplier)"),
        @($nClient,$nOrder,"approve()"),
        @($nOrder,$nDraft,"approve(order)"),
        @($nDraft,$nOrder,"setState(ApprovedState)"),
        @($nClient,$nOrder,"generateInboundOrder()"),
        @($nOrder,$nApproved,"generateInboundOrder(order)"),
        @($nApproved,$nOrder,"setState(InStockState)"),
        @($nClient,$nOrder,"generateInvoice()"),
        @($nOrder,$nInStock,"generateInvoice(order)"),
        @($nInStock,$nOrder,"setState(InvoicedState)"),
        @($nClient,$nOrder,"pay()"),
        @($nOrder,$nInvoiced,"pay(order)"),
        @($nInvoiced,$nOrder,"setState(PaidState)"),
        @($nClient,$nOrder,"getStateName() = 已付款")
    )) {
        Add-Message $msg[0] $msg[1] $msg[2] $seq
        $seq++
    }
    [void]$normalSeq.Update()

    $invalidSeq = Add-Diagram $statePkg "实验11_WMS采购单非法操作拦截顺序图" "Sequence"
    $iClient = Add-Participant $statePkg $invalidSeq "client : Experiment11Demo" 30
    $iDraftOrder = Add-Participant $statePkg $invalidSeq "draftOrder : PurchaseOrder" 310
    $iDraft = Add-Participant $statePkg $invalidSeq "state : DraftState" 610
    $iStockOrder = Add-Participant $statePkg $invalidSeq "stockOrder : PurchaseOrder" 890
    $iStock = Add-Participant $statePkg $invalidSeq "state : InStockState" 1200
    $iException = Add-Participant $statePkg $invalidSeq "BusinessException" 1490
    $seq = 1
    foreach ($msg in @(
        @($iClient,$iDraftOrder,"new PurchaseOrder(PO-ERR-001)"),
        @($iClient,$iDraftOrder,"generateInboundOrder()"),
        @($iDraftOrder,$iDraft,"generateInboundOrder(order)"),
        @($iDraft,$iException,"throw 只有已审批状态允许生成入库单"),
        @($iException,$iClient,"拦截成功"),
        @($iClient,$iStockOrder,"new PurchaseOrder(PO-ERR-003)"),
        @($iClient,$iStockOrder,"edit() / approve() / generateInboundOrder()"),
        @($iClient,$iStockOrder,"cancel()"),
        @($iStockOrder,$iStock,"cancel(order)"),
        @($iStock,$iException,"throw 已入库后不能直接取消"),
        @($iException,$iClient,"拦截成功")
    )) {
        Add-Message $msg[0] $msg[1] $msg[2] $seq
        $seq++
    }
    [void]$invalidSeq.Update()

    $returnSeq = Add-Diagram $statePkg "实验11_WMS采购单退货退款流程顺序图" "Sequence"
    $rClient = Add-Participant $statePkg $returnSeq "client : Experiment11Demo" 30
    $rOrder = Add-Participant $statePkg $returnSeq "order : PurchaseOrder" 300
    $rDraft = Add-Participant $statePkg $returnSeq "state : DraftState" 570
    $rApproved = Add-Participant $statePkg $returnSeq "state : ApprovedState" 840
    $rInStock = Add-Participant $statePkg $returnSeq "state : InStockState" 1130
    $seq = 1
    foreach ($msg in @(
        @($rClient,$rOrder,"new PurchaseOrder(PO-003)"),
        @($rClient,$rOrder,"edit(product, quantity, supplier)"),
        @($rOrder,$rDraft,"edit(order, product, quantity, supplier)"),
        @($rClient,$rOrder,"approve()"),
        @($rOrder,$rDraft,"approve(order)"),
        @($rDraft,$rOrder,"setState(ApprovedState)"),
        @($rClient,$rOrder,"generateInboundOrder()"),
        @($rOrder,$rApproved,"generateInboundOrder(order)"),
        @($rApproved,$rOrder,"setState(InStockState)"),
        @($rClient,$rOrder,"returnAndRefund()"),
        @($rOrder,$rInStock,"returnAndRefund(order)"),
        @($rInStock,$rOrder,"setState(ReturnedState)"),
        @($rClient,$rOrder,"getStateName() = 已退货完结")
    )) {
        Add-Message $msg[0] $msg[1] $msg[2] $seq
        $seq++
    }
    [void]$returnSeq.Update()

    Export-Diagram $observerClassDiagram "01_observer_class_diagram.png"
    Export-Diagram $observerSeq "02_observer_sequence_diagram.png"
    Export-Diagram $stateClassDiagram "03_state_class_diagram.png"
    Export-Diagram $stateDiagram "04_state_state_diagram.png"
    Export-Diagram $normalSeq "05_state_normal_sequence.png"
    Export-Diagram $invalidSeq "06_state_invalid_sequence.png"
    Export-Diagram $returnSeq "07_state_return_sequence.png"
}
finally {
    $repo.CloseFile()
    $repo.Exit()
}

Write-Output "EA project generated: $target"
Write-Output "EA diagram images exported to: $imageDir"





