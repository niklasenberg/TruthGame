package se.umu.nien1121.truthgame

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment


/**
 * Utility method  for hiding keyboard in [Fragment]. Dependent on [Context.hideKeyboard]
 */
fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

/**
 * Utility method for hiding keyboard in [Activity]. Dependent on [Context.hideKeyboard]
 */
fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

/**
 * Main utility method for manually hiding software keyboard.
 * @param view: [View] in which to hide keyboard.
 */
fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}