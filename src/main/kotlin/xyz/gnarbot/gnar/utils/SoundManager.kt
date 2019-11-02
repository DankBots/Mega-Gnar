package xyz.gnarbot.gnar.utils

import xyz.gnarbot.gnar.Bot
import java.io.File
import java.net.URL
import java.util.regex.Pattern

class SoundManager {

    var map : HashMap<String, String> = HashMap()

    fun loadSounds()
    {
        try {
            for (s in File("/home/Gnar-bot/sounds").listFiles()) {
                print(s)
                map[s.name.replace(".mp3", "").replace("sounds\\", "")] = s.path
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}