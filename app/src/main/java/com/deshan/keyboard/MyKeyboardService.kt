package com.deshan.keyboard

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo

class MyKeyboardService : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    private lateinit var keyboardView: KeyboardView
    private lateinit var englishKeyboard: Keyboard
    private lateinit var sinhalaLettersKeyboard: Keyboard
    private lateinit var sinhalaSignsKeyboard: Keyboard
    private var isShifted = false

    override fun onCreateInputView(): View {
        keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null) as KeyboardView
        englishKeyboard = Keyboard(this, R.xml.qwerty)
        sinhalaLettersKeyboard = Keyboard(this, R.xml.sinhala_letters)
        sinhalaSignsKeyboard = Keyboard(this, R.xml.sinhala_signs)
        keyboardView.keyboard = englishKeyboard
        keyboardView.setOnKeyboardActionListener(this)
        return keyboardView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        keyboardView.keyboard = englishKeyboard
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val ic = currentInputConnection ?: return
        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> ic.deleteSurroundingText(1, 0)
            Keyboard.KEYCODE_SHIFT -> {
                isShifted = !isShifted
                keyboardView.keyboard?.isShifted = isShifted
                keyboardView.invalidateAllKeys()
            }
            Keyboard.KEYCODE_DONE -> ic.sendKeyEvent(
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
            )
            -2 -> {
                // TODO: switch to a symbols/numbers keyboard layout
            }
            -10 -> keyboardView.keyboard = sinhalaLettersKeyboard
            -11 -> keyboardView.keyboard = englishKeyboard
            -12 -> keyboardView.keyboard = sinhalaSignsKeyboard
            -13 -> keyboardView.keyboard = sinhalaLettersKeyboard
            else -> {
                var code = primaryCode.toChar()
                if (Character.isLetter(code) && isShifted && keyboardView.keyboard == englishKeyboard) {
                    code = code.uppercaseChar()
                }
                ic.commitText(code.toString(), 1)
            }
        }
    }

    override fun onPress(primaryCode: Int) {}
    override fun onRelease(primaryCode: Int) {}
    override fun onText(text: CharSequence?) {}
    override fun swipeLeft() {}
    override fun swipeRight() {}
    override fun swipeDown() {}
    override fun swipeUp() {}
}
