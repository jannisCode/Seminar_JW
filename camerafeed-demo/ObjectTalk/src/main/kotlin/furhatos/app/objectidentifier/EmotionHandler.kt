package furhatos.app.objectidentifier

class EmotionHandler {
    private var currentEmotion = emotions.NO_EMOTION

    fun setEmotion(emotion: emotions) {
        this.currentEmotion = emotion
    }

    fun getEmotion(): emotions {
        return this.currentEmotion
    }

    fun getDominantEmotion(jsonString: String) {
        val startIndex = jsonString.indexOf("\"dominant_emotion\":") + "\"dominant_emotion\":".length
        val endIndex = jsonString.indexOf(",", startIndex)
        val em = jsonString.substring(startIndex, endIndex).trim().replace("\"", "")
        println(em)
        currentEmotion = furhatos.app.objectidentifier.getEmotionFromString(em)
        println("current Emotion: " + currentEmotion.toString())
    }

    fun euclidian_distance(x_one: Int, y_one: Int, x_two: Int, y_two:Int): Double {
        val x_distance = x_one - x_two
        val y_distance = y_one - y_two
        return Math.sqrt((x_distance * x_distance + y_distance * y_distance).toDouble())
    }




}