package com.example.fiorichartcardsapp.utilities

import android.content.Context
import java.io.BufferedReader
import java.io.IOException

class CovidDataBuilder {
    companion object {

        @JvmStatic
        fun getJsonDataFromAsset(context: Context, fileName: String): String? {
            val jsonString: String
            var reader: BufferedReader? = null
            try {
                reader = context.assets.open(fileName).bufferedReader()
                jsonString = reader.use { it.readText() }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                return null
            } finally {
                reader?.close()
            }
            return jsonString
        }
    }
}