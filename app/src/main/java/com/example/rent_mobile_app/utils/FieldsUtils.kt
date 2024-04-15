package com.example.rent_mobile_app.utils

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.Switch

class FieldsUtils {

    companion object {

        fun disable(i: EditText, status: Boolean) {
            i.isFocusable = status
            i.isFocusableInTouchMode = status
            i.isEnabled = status
        }

        fun disable(i: Spinner, status: Boolean) {
            i.isFocusable = status
            i.isFocusableInTouchMode = status
            i.isEnabled = status
        }

        fun disable(i: Switch, status: Boolean) {
            i.isFocusable = status
            i.isFocusableInTouchMode = status
            i.isEnabled = status
        }

        fun disable(i: Button, status: Boolean) {
            i.isFocusable = status
            i.isFocusableInTouchMode = status
            i.isEnabled = status
        }

        fun disable(i: NumberPicker, status: Boolean) {
            i.isFocusable = status
            i.isFocusableInTouchMode = status
            i.isEnabled = status
        }

        fun invisible(i: Button) {
            i.visibility = View.GONE
        }

        fun invisible(i: NumberPicker) {
            i.visibility = View.GONE
        }

        fun visible(i: Button) {
            i.visibility = View.VISIBLE
        }

        fun visible(i: LinearLayout) {
            i.visibility = View.VISIBLE
        }

        fun setError(field: EditText) {
            field.error = field.hint.toString()
        }

        fun startNumberPiker(screenNumberPickers: MutableList<NumberPicker>) {
            for (i in screenNumberPickers) {
                setNumberPiker(i)
            }
        }

        private fun setNumberPiker(numberPicker: NumberPicker) {
            numberPicker.minValue = 1
            numberPicker.maxValue = 100
            numberPicker.value = 1
        }

        fun setNumberPikerPlus(numberPicker: NumberPicker) {
            numberPicker.minValue = 1
            numberPicker.maxValue = 1000
            numberPicker.value = 1
        }
    }
}