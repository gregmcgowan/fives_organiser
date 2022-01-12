package com.gregmcgowan.fivesorganiser.core.ui

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

import java.lang.ref.WeakReference

class EditTextDebounce constructor(
        editText: EditText,
        delayMillis: Int = 300
) {

    private val editTextWeakReference: WeakReference<EditText>?
    private val debounceHandler: Handler = Handler(Looper.getMainLooper())
    private lateinit var debounceCallback: (String) -> Unit?
    private var debounceWorker: Runnable? = null
    private val textWatcher: TextWatcher

    init {
        this.debounceWorker = DebounceRunnable("", { _ -> })
        this.textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //unused
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //unused
            }

            override fun afterTextChanged(s: Editable) {
                debounceHandler.removeCallbacks(debounceWorker!!)
                debounceWorker = DebounceRunnable(s.toString(), debounceCallback)
                debounceHandler.postDelayed(debounceWorker!!, delayMillis.toLong())
            }
        }
        this.editTextWeakReference = WeakReference(editText)
        this.editTextWeakReference.get()?.addTextChangedListener(textWatcher)
    }

    fun watch(debounceCallback: (String) -> Unit) {
        this.debounceCallback = debounceCallback
    }

    fun unwatch() {
        if (editTextWeakReference != null) {
            val editText = editTextWeakReference.get()
            if (editText != null) {
                editText.removeTextChangedListener(textWatcher)
                editTextWeakReference.clear()
                debounceHandler.removeCallbacks(debounceWorker!!)
            }
        }
    }


    private class DebounceRunnable internal constructor(
            private val result: String,
            private val debounceCallback: (String) -> Unit?
    ) : Runnable {

        override fun run() {
            debounceCallback.invoke(result)
        }
    }


}