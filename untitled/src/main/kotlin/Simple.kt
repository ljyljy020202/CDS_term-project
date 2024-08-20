import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.*
import org.w3c.dom.events.EventListener
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import kotlin.math.PI
import kotlin.math.abs
//import kotlin.browser.document

class StompClient(private val webSocket: WebSocket) {
    var isWebSocketConnected = false
    fun connect() {
        webSocket.onopen = {
            isWebSocketConnected = true
            val connectFrame = "CONNECT\n\n\u0000"
            webSocket.send(connectFrame)
        }
    }

    fun sendMessage(destination: String, message: String) {
        if (!isWebSocketConnected) {
            // WebSocket이 연결되지 않은 상태일 때는 메시지를 보낼 수 없음
            println("WebSocket이 연결되지 않았습니다.")
            return
        }
        val stompFrame = "SEND\ndestination:$destination\n\n$message\u0000"
        webSocket.send(stompFrame)
        println("send message: $stompFrame")

        webSocket.onmessage = { event ->
            val message = (event as MessageEvent).data as String
//            val type = message.get(0)
//
//            if(type.equals("circle")) {
//                receiveCircle(message)
//            }
//            else if(type.equals("rectangle")) {
//                receiveRectangle(message)
//            }
//            else if(type.equals("line")) {
//                receiveLine(message)
//            }
//            else if( type.equals("text")) {
//                receiveText(message)
//            }
            println("Received message: $message")
            // Handle the received message as needed
        }

        webSocket.onerror = { errorEvent ->
            val error = errorEvent.asDynamic().error as String
            println("WebSocket error: $error")
        }

        // 연결을 시도하는데 실패했을 때
        webSocket.onclose = {
            println("WebSocket connection failed")
        }

        window.onbeforeunload = {
            webSocket.close()
            null
        }
    }

    fun disconnect() {
        val disconnectFrame = "DISCONNECT\n\n\u0000"
        webSocket.send(disconnectFrame)
    }
}

// Canvas 요소와 컨텍스트 가져오기
val canvas = document.getElementById("Canvas") as HTMLCanvasElement
val ctx = canvas.getContext("2d") as CanvasRenderingContext2D

fun main() {

    // 기본 값 설정
    var isDrawing = false
    var lineWidth = 2
    var strokeColor = "#000000"
    var fillColor = "#transparent"
    var downX = 0.0
    var downY = 0.0

    //텍스트 박스
    val textInput = document.getElementById("TextInput") as HTMLInputElement


    // 웹소켓
    val webSocket = WebSocket("ws://localhost:9090/")
    val stompClient = StompClient(webSocket)

    stompClient.connect()


    // 리스너 정의
    // 원 그리는 이벤트리스너
    val circleListener = EventListener { event ->
        ctx.lineWidth = lineWidth.toDouble()
        ctx.strokeStyle = strokeColor

        val upX = event.asDynamic().offsetX.toString().toDouble()
        val upY = event.asDynamic().offsetY.toString().toDouble()

        val centerX = (upX + downX) / 2
        val centerY = (upY + downY) / 2
        val radiusX = abs((upX - downX) / 2)
        val radiusY = abs((upY - downY) / 2)

        ctx.beginPath()
        ctx.ellipse(centerX, centerY, radiusX, radiusY, 0.0, 0.0, 2*PI)
        ctx.closePath()
        ctx.stroke()
        ctx.fill()

        stompClient.sendMessage("ws://localhost:9090/ws", "{\"id\":\"circle\", \"type\":\"circle\", \"lineWidth\":\"" + lineWidth +
                "\", \"strokeColor\":\"" + strokeColor + "\", \"fillColor\":\"" + fillColor + "\", \"startPoint\":{\"x\":\"" +
                upX + "\", \"y\":\"" + upY + "\"}, \"endPoint\":{\"x\":\"" + downX + "\", \"y\":\"" + downY + "\"}, \"msg\":\"\"}");

    }
    // 사각형 그리는 이벤트리스너
    val rectangleListener = EventListener { event ->
        ctx.lineWidth = lineWidth.toDouble()
        ctx.strokeStyle = strokeColor

        val upX = event.asDynamic().offsetX.toString().toDouble()
        val upY = event.asDynamic().offsetY.toString().toDouble()
        val subX = abs(upX - downX) //가로
        val subY = abs(upY - downY) //세로

        ctx.beginPath()
        ctx.rect(downX, downY, subX, subY)
        ctx.closePath()
        ctx.stroke()
        ctx.fill()

        stompClient.sendMessage("ws://localhost:9090/ws", "{\"id\":\"rectangle\", \"type\":\"circle\", \"lineWidth\":\"" + lineWidth +
                "\", \"strokeColor\":\"" + strokeColor + "\", \"fillColor\":\"" + fillColor + "\", \"startPoint\":{\"x\":\"" +
                upX + "\", \"y\":\"" + upY + "\"}, \"endPoint\":{\"x\":\"" + downX + "\", \"y\":\"" + downY + "\"}, \"msg\":\"\"}");
    }
    // 선 그리는 이벤트리스너
    val lineListener = EventListener { event ->
        ctx.lineWidth = lineWidth.toDouble()
        ctx.strokeStyle = strokeColor

        val upX = event.asDynamic().offsetX.toString().toDouble()
        val upY = event.asDynamic().offsetY.toString().toDouble()

        ctx.beginPath()
        ctx.moveTo(downX, downY)
        ctx.lineTo(upX, upY)
        ctx.closePath()
        ctx.stroke()

        stompClient.sendMessage("ws://localhost:9090/ws", "{\"id\":\"line\", \"type\":\"circle\", \"lineWidth\":\"" + lineWidth +
                "\", \"strokeColor\":\"" + strokeColor + "\", \"fillColor\":\"" + fillColor + "\", \"startPoint\":{\"x\":\"" +
                upX + "\", \"y\":\"" + upY + "\"}, \"endPoint\":{\"x\":\"" + downX + "\", \"y\":\"" + downY + "\"}, \"msg\":\"\"}");
    }
    // 텍스트 이벤트리스너
    val textListener = EventListener { event ->
        val upX = event.asDynamic().offsetX.toString().toDouble()
        val upY = event.asDynamic().offsetY.toString().toDouble()
        val subY = abs(upY - downY)
        val text = textInput.value
        ctx.font = "20px Arial"
        ctx.fillStyle = fillColor
        ctx.strokeStyle = strokeColor
        ctx.fillText(text, downX, downY, subY)
        ctx.strokeText(text, downX, downY, subY)

        stompClient.sendMessage("ws://localhost:9090/ws", "{\"id\":\"text\", \"type\":\"circle\", \"lineWidth\":\"" + lineWidth +
                "\", \"strokeColor\":\"" + strokeColor + "\", \"fillColor\":\"" + fillColor + "\", \"startPoint\":{\"x\":\"" +
                upX + "\", \"y\":\"" + upY + "\"}, \"endPoint\":{\"x\":\"" + downX + "\", \"y\":\"" + downY + "\"}, \"msg\":" + text + "\"\"}");
    }
    // 버튼에 리스너 붙이기
    fun drawCircle() {
        canvas.removeEventListener("mouseup", rectangleListener)
        canvas.removeEventListener("mouseup", lineListener)
        canvas.removeEventListener("mouseup", textListener)
        canvas.addEventListener("mouseup", circleListener)
    }
    fun drawRectangle() {
        canvas.removeEventListener("mouseup", circleListener)
        canvas.removeEventListener("mouseup", lineListener)
        canvas.removeEventListener("mouseup", textListener)
        canvas.addEventListener("mouseup", rectangleListener)
    }
    fun drawLine() {
        canvas.removeEventListener("mouseup", circleListener)
        canvas.removeEventListener("mouseup", rectangleListener)
        canvas.removeEventListener("mouseup", textListener)
        canvas.addEventListener("mouseup", lineListener)
    }
    fun drawText() {
        canvas.removeEventListener("mouseup", circleListener)
        canvas.removeEventListener("mouseup", rectangleListener)
        canvas.removeEventListener("mouseup", lineListener)
        canvas.addEventListener("mouseup", textListener)
    }

    // 마우스 다운 이벤트 처리
    canvas.addEventListener("mousedown", { event ->
        isDrawing = true
        downX = event.asDynamic().offsetX.toString().toDouble()
        downY = event.asDynamic().offsetY.toString().toDouble()
        ctx.fillStyle = fillColor
    })

    // 마우스 이동 이벤트 처리
    canvas.addEventListener("mousemove", { event ->
        if (!isDrawing) return@addEventListener
        val nowX = event.asDynamic().offsetX
        val nowY = event.asDynamic().offsetY
    })

    // 마우스 업 이벤트 처리
    canvas.addEventListener("mouseup", {
        isDrawing = false
    })

    // 선 두께 변경 이벤트 처리
    val lineWidthInput = document.getElementById("LineWidth") as HTMLInputElement
    lineWidthInput.addEventListener("input", {
        lineWidth = lineWidthInput.value.toInt()
    })

    // 스트로크 색상 변경 이벤트 처리
    val strokeColorInput = document.getElementById("StrokeColor") as HTMLInputElement
    strokeColorInput.addEventListener("input", {
        strokeColor = strokeColorInput.value
    })

    // 채우기 색상 변경 이벤트 처리
    val fillColorInput = document.getElementById("FillColor") as HTMLInputElement
    fillColorInput.addEventListener("input", {
        fillColor = fillColorInput.value
    })

    // fill 버튼 클릭 이벤트 처리
    val fillColorBtn = document.getElementById("fill") as HTMLButtonElement
    fillColorBtn.addEventListener("click", {
        ctx.fillStyle = fillColorInput.value
        fillColor = fillColorInput.value
    })

    //transparent 버튼 클릭 이벤트 처리
    val transparentBtn = document.getElementById("transparent") as HTMLButtonElement
    transparentBtn.addEventListener("click", {
        ctx.fillStyle = "transparent"
        fillColor = "transparent"
    })

    // 도형 버튼 이벤트 처리
    val circleBtn = document.getElementById("Circle") as HTMLButtonElement
    val rectangleBtn = document.getElementById("Rectangle") as HTMLButtonElement
    val lineBtn = document.getElementById("Line") as HTMLButtonElement
    val textBtn = document.getElementById("Text") as HTMLButtonElement

    circleBtn.addEventListener("click", { drawCircle() })
    rectangleBtn.addEventListener("click", { drawRectangle() })
    lineBtn.addEventListener("click", { drawLine() })
    textBtn.addEventListener("click", { drawText() })
}

fun receiveCircle(message: String) {
    val lineWidth = message.get(1)
    val strokeStyle = message.get(2)
    val fillStyle = message.get(3)

    val startPoint = message.get(4)
    val endPoint = message.get(5)

    val upX = startPoint.toString().get(0) as Double
    val upY = startPoint.toString().get(1) as Double
    val downX = endPoint.toString().get(0) as Double
    val downY = endPoint.toString().get(1) as Double

    val centerX = (upX + downX) / 2
    val centerY = (upY + downY) / 2
    val radiusX = abs((upX - downX) / 2)
    val radiusY = abs((upY - downY) / 2)

    ctx.beginPath()
    ctx.ellipse(centerX, centerY, radiusX, radiusY, 0.0, 0.0, 2*PI)
    ctx.closePath()
    ctx.stroke()
    ctx.fill()
}

fun receiveRectangle(message: String) {
    val lineWidth = message.get(1)
    val strokeStyle = message.get(2)
    val fillStyle = message.get(3)

    val startPoint = message.get(4)
    val endPoint = message.get(5)

    val upX = startPoint.toString().get(0) as Double
    val upY = startPoint.toString().get(1) as Double
    val downX = endPoint.toString().get(0) as Double
    val downY = endPoint.toString().get(1) as Double
    val subX = abs(upX - downX) //가로
    val subY = abs(upY - downY) //세로

    ctx.beginPath()
    ctx.rect(downX, downY, subX, subY)
    ctx.closePath()
    ctx.stroke()
    ctx.fill()
}

fun receiveLine(message: String) {
    val lineWidth = message.get(1)
    val strokeStyle = message.get(2)
    val fillStyle = message.get(3)

    val startPoint = message.get(4)
    val endPoint = message.get(5)

    val upX = startPoint.toString().get(0) as Double
    val upY = startPoint.toString().get(1) as Double
    val downX = endPoint.toString().get(0) as Double
    val downY = endPoint.toString().get(1) as Double

    ctx.beginPath()
    ctx.moveTo(downX, downY)
    ctx.lineTo(upX, upY)
    ctx.closePath()
    ctx.stroke()
}

fun receiveText(message: String) {
    val lineWidth = message.get(1)
    val strokeStyle = message.get(2)
    val fillStyle = message.get(3)

    val startPoint = message.get(4)
    val endPoint = message.get(5)

    val text = message.get(6)

    val upX = startPoint.toString().get(0) as Double
    val upY = startPoint.toString().get(1) as Double
    val downX = endPoint.toString().get(0) as Double
    val downY = endPoint.toString().get(1) as Double

    val subY = abs(upY - downY)

    ctx.font = "20px Arial"
    ctx.fillText(text.toString(), downX, downY, subY)
    ctx.strokeText(text.toString(), downX, downY, subY)
}