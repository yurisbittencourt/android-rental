package com.example.rent_mobile_app.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

class MessageUtils {

    companion object {
        var superMessage = ""

        fun snack(root: View, text: String) {
            Snackbar.make(root, text, Snackbar.LENGTH_SHORT).show()
        }

        fun superMessage(root: View) {
            if (superMessage.isNotEmpty()) {
                snack(root, superMessage)
                superMessage = ""
            }
        }
    }
}