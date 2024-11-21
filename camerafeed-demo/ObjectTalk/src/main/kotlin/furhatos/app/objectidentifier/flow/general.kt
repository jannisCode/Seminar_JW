package furhatos.app.objectidentifier.flow

import furhatos.app.objectidentifier.getHandler
import furhatos.event.Event
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.state

/**
 * Events used for communication between the thread and the flow.
 */
class EnterEvent(val objects: List<String>): Event()
class LeaveEvent(val objects: List<String>): Event()

/**
 * Main flow that starts the camera feed and awaits events sent from the thread
 */
val Main = state {

    onEntry {
        furhat.cameraFeed.enable()
        furhat.say(getHandler().getEmotion().toString())
    }

    onEvent<EnterEvent> {// Objects that enter the view
        furhat.say("you are" + getHandler().getEmotion().toString())
    }

    onEvent<LeaveEvent> {
        furhat.say("you are" + getHandler().getEmotion().toString())
    }

    onExit {
        furhat.cameraFeed.disable()
    }
}