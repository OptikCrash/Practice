package com.digitalrelay.practice.data

import android.content.Context
import android.content.SharedPreferences
import com.digitalrelay.practice.PracticeApp
import com.digitalrelay.practice.data.model.LoggedInUser
import org.json.JSONObject
import java.io.IOException
import java.util.*

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */

class LoginDataSource {
    private val sharedPreferences: SharedPreferences =
        PracticeApp.applicationContext
            .getSharedPreferences("private-keystore-alias", Context.MODE_PRIVATE)

    companion object {
//        private lateinit var queue: RequestQueue
//        internal val requestQ: RequestQueue
//            get() { return if (!::queue.isInitialized) Volley.newRequestQueue(PracticeApp.applicationContext) else queue}
    }

    fun login(userEmail: String, password: String): Result<LoggedInUser> {
        return if (!sharedPreferences.contains(userEmail)) {
            Result.Error(Exception("No record for $userEmail"))
        } else {
            val result: String = sharedPreferences.getString(userEmail, "").toString()
            val userObj = JSONObject(result)
            val hash = hashMapOf(userEmail to password)
            if (result.isNotEmpty() && userObj["pass-hash"] == hash) {
                try {
                    val user = LoggedInUser(userObj["id"] as String, userObj["handle"] as String)
                    Result.Success(user)
                } catch (e: Throwable) {
                    Result.Error(IOException("An error occurred", e))
                }
            } else {
                Result.Error(IOException("Incorrect password"))
            }
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }

    fun createUser(userEmail: String, firstPass: String, secondPass: String): Result<LoggedInUser> {
        val firstHash = hashMapOf(userEmail to firstPass)
        val secondHash = hashMapOf(userEmail to secondPass)
        return if (firstHash != secondHash) {
            Result.Error(IOException("Passwords do not match"))
        } else {
            val newId = UUID.randomUUID()
            val userObj = JSONObject("{id:$newId,email:$userEmail,pass-hash:$firstHash,handle:$userEmail,firstName:,lastName:,phone:}")
            sharedPreferences.edit().putString(userEmail, userObj.toString()).apply()
            login(userEmail, firstPass)
        }
    }
}