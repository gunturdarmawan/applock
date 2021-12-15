package com.example.applock.model

import android.content.Context
import io.paperdb.Paper

class Password(ctx: Context?) {
    val PASSWORD_KEY = "PASSWORD_KEY"
    var STATUS_FIRST_STEP = "Draw an unlock pattern"
    var STATUS_NEXT_STEP = "Draw pattern again to confirm"
    var STATUS_PASSWORD_CORRECT = "Pattern Set"
    var STATUS_PASSWORD_INCORRECT = "Try Again"
    var SHEMA_FAILED = "Connect at least 4 dots"
    var isFirstStep = true
    fun setResetPassword() {
        Paper.book().delete(PASSWORD_KEY)
    }

    var password: String
        get() = Paper.book().read(PASSWORD_KEY)
        set(pwd) {
            Paper.book().write(PASSWORD_KEY, pwd)
        }

    fun isCorrect(pwd: String): Boolean {
        return pwd == password
    }

    init {
        Paper.init(ctx)
    }
}