package se.umu.nien1121.truthgame

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.fragment.app.Fragment


/**
 * Utility method  for hiding keyboard in [Fragment]. Dependent on [Context.hideKeyboard].
 * Source: [stackoverflow.com](https://stackoverflow.com/questions/41790357/close-hide-the-android-soft-keyboard-with-kotlin)
 */
fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

/**
 * Utility method for hiding keyboard in [Activity]. Dependent on [Context.hideKeyboard]
 * Source: [stackoverflow.com](https://stackoverflow.com/questions/41790357/close-hide-the-android-soft-keyboard-with-kotlin)
 */
fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

/**
 * Main utility method for manually hiding software keyboard.
 * @param view: [View] in which to hide keyboard.
 * Source: [stackoverflow.com](https://stackoverflow.com/questions/41790357/close-hide-the-android-soft-keyboard-with-kotlin)
 */
fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * Utility method which loads file URI and displays in given ImageView.
 * @param imageView: ImageView in which to display given image URI
 * @param imageUri: URI for image to be displayed
 * @param context: Context object needed for fetching correct file paths
 */
fun setPicture(imageView: ImageView, imageUri: Uri, context: Context): Boolean {
    //Get filepath
    val path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        .toString() + "/" + imageUri.lastPathSegment

    //Get custom factory options
    var options = BitmapFactory.Options().apply {
        //Preserves memory
        inJustDecodeBounds = true
    }

    //Decode file using options, then check if file is image
    BitmapFactory.decodeFile(path, options)
    if (options.outWidth != -1 && options.outHeight != -1) {

        //Assign image with real decoding
        imageView.setImageBitmap(BitmapFactory.decodeFile(path))

        return true
    }
    return false
}
