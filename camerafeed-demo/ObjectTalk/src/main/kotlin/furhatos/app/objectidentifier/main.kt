package furhatos.app.objectidentifier

import furhatos.app.objectidentifier.flow.Main
import furhatos.skills.Skill
import furhatos.flow.kotlin.*
import furhatos.util.CommonUtils
import org.zeromq.ZMQ
import kotlinx.coroutines.launch
import kotlinx.coroutines.GlobalScope
import zmq.ZMQ.ZMQ_SUB


val logger = CommonUtils.getRootLogger()
val objserv = "tcp://172.25.249.240:9999" //The TCP socket of the object server

val subSocket: ZMQ.Socket = getConnectedSocket(ZMQ_SUB, objserv) //Makes a socket of the object server
val enter = "enter_" //Objects that enter the view start with this string
val leave = "leave_" //Objects that leave the view start with this string
var handler_one = EmotionHandler()

/**
 * Parses a message from the object server, turns the message into a list of objects.
 */
fun getObjects(message: String, delimiter: String): List<String> {
    val objects = mutableListOf<String>()
    message.split(" ").forEach {
        if(it.startsWith(delimiter)) {
            objects.add(it.removePrefix(delimiter))
        }
    }
    return objects
}

/**
 * Function that starts a thread which continuously polls the object server.
 * Based on what is in the message will either send:
 *  - EnterEvent, for objects coming into view.
 *  - LeaveEvent, for objects going out of view.
 *
 *  These events can be caught in the flow (Main), and be responded to.
 */
fun startListenThread() {
    GlobalScope.launch { // launch a new coroutine in background and continue
        logger.warn("LAUNCHING COROUTINE")
        subSocket.subscribe("")
        while (true) {
            print("recieving...")
            val message = subSocket.recvStr()
            processResponse(message)
            logger.warn("got: $message")
            processResponse(message)
        }
    }
}

class ObjectIdentifierSkill : Skill() {
    override fun start() {
        startListenThread()
        Flow().run(Main)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}

fun getHandler() : EmotionHandler {
    return handler_one
}

fun processResponse (message: String) {
    handler_one.getDominantEmotion(message)
}