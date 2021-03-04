package com.example.newsapp.extensions

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

// вытаскивает timestamp из строки с датой
fun getTimestampFromString(dateStr: String): Long {
    val newString = dateStr.replace("T", " ").replace("Z", "")
    val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
    val date: Date = formatter.parse(newString) as Date
    return date.time
}

// показать клавиатуру для выбранного editText
fun showKeyboard(activity: Activity) {
    val imm: InputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view: View? = activity.currentFocus
    if (view == null) {
        view = View(activity)
    }
    imm.showSoftInput(view, 0)
}

// скрыть клавиатуру
fun hideKeyboard(activity: Activity) {
    val imm: InputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view: View? = activity.currentFocus
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

// приводит дату к формату который нужно передать в апи запросе
fun toQueryDate(year: Int, month: Int, dayOfMonth: Int): String {
    return "$year-${month + 1}-${dayOfMonth}"
}

// открывает ссылку внутри браузера в приложении
fun openUrlInCustomTabs(context: Context, url: String) {
    val customTabsIntent = CustomTabsIntent.Builder().apply {
        addDefaultShareMenuItem()
    }.build()
    customTabsIntent.launchUrl(context, Uri.parse(url))
}

// показывает toast с любым сообщением
fun Context.showToastMessage(@StringRes message: Int) {
    Toast.makeText(
        this,
        resources.getString(message),
        Toast.LENGTH_SHORT
    ).show()
}

// меняет привязку constraint с анимацией
fun updateConstraints(
    layout: ConstraintLayout,
    @IdRes root: Int,
    @IdRes mainBar: Int,
    isShow: Boolean
) {
    Handler(Looper.getMainLooper()).postDelayed({
        val set = ConstraintSet()
        set.clone(layout)
        set.connect(
            root, ConstraintSet.TOP,
            mainBar, if (isShow) ConstraintSet.BOTTOM else ConstraintSet.TOP, 0
        )
        val transitionSet = TransitionSet().addTransition(ChangeBounds())
        transitionSet.duration = 250
        TransitionManager.beginDelayedTransition(layout, transitionSet)
        set.applyTo(layout)
    }, 250)
}

fun convertDpToPixel(dp: Float): Float {
    val metrics: DisplayMetrics = Resources.getSystem().displayMetrics
    val px = dp * (metrics.densityDpi / 160f)
    return px.roundToInt().toFloat()
}

// загрузка изображения с помощью glide
fun ImageView.loadImage(url: String) {
    Glide.with(this.context)
        .load(url)
        .apply(
            RequestOptions().transform(
                CenterCrop(),
                RoundedCorners(convertDpToPixel(8f).toInt())
            )
        )
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

inline fun <reified T> attempt(func: () -> T): Either<Throwable, T> = try {
    Either.Right(func.invoke())
} catch (e: Throwable) {
    Either.Left(e)
}
