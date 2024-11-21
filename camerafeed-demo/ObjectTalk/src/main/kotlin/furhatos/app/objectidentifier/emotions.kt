package furhatos.app.objectidentifier

enum class emotions (val displayName: String) {
    ANGRY("angry"),
    DISGUST("disgust"),
    FEAR("fear"),
    HAPPY("happy"),
    SAD("sad"),
    SURPRISE("surprise"),
    NEUTRAL("neutral"),
    NO_EMOTION("no emotion detected");

    override fun toString(): String {
        return displayName
    }
}
    fun getEmotionFromString(em : String): emotions {
        for (emotion in emotions.values()) {
            if (emotion.toString().equals(em))
                return emotion
        }
        return emotions.NEUTRAL
    }

