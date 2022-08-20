package se.umu.nien1121.truthgame

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner


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
 * @return true/false if image has been set successfully
 */
fun setPicture(imageView: ImageView, imageUri: Uri, context: Context): Boolean {
    //Get filepath
    val path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        .toString() + "/" + imageUri.lastPathSegment

    //Get custom factory options
    val options = BitmapFactory.Options().apply {
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

/**
 * Utility method for configuring the ActionBar, setting the correct title and navigational options
 * @param title: The title to be set in the ActionBar
 * @param activity: The activity which owns the ActionBar
 * @param lifecycleOwner: owner for lifecycles of views within fragment (viewLifecycleOwner)
 */
fun setSupportActionBar(title: String, activity: FragmentActivity, lifecycleOwner: LifecycleOwner) {
    activity.addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
            // Clear current options, set title and add correct menu options
            menu.clear()
            (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(
                true
            )
            activity.supportActionBar!!.title = title
        }

        override fun onMenuItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                android.R.id.home -> {
                    activity.supportFragmentManager.popBackStack()
                }
            }
            return true
        }
    }, lifecycleOwner, Lifecycle.State.RESUMED)
}
