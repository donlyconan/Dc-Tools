package base.settings

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type
import java.util.prefs.Preferences


class Settings {

    companion object {
        private var instance: Settings? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance = Settings()
            instance!!
        }
    }

    var preferences: Preferences = Preferences.userNodeForPackage(javaClass)
        private set
    var folderOfListFile by DelegateStringType("list_file", preferences, null)
    var fontSize by DelegateDoubleType("font_size", preferences, 13.0)
    var fontFamily by DelegateStringType("font_family", preferences, "Consolas")
    var charset by DelegateStringType("charset", preferences, "UTF-8")
    var currentTabFile by DelegateStringType("current_tab", preferences, null)
    var openTabs by DelegateStringType("open_tab", preferences, null)


    init {
        // TODO
    }


    fun getListPaths(): List<String> {
        if(openTabs != null) {
            val json = JsonParser.parseString(openTabs)
            val gson = Gson()
            val listType: Type = object : TypeToken<List<String?>?>() {}.type
            return gson.fromJson(json, listType)
        } else {
            return arrayListOf()
        }
    }

    fun getDir(): File? {
        if(folderOfListFile != null) {
            return File(folderOfListFile)
        }
        return null
    }

    fun removeAll() {
        preferences.clear()
    }

}

