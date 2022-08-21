package com.mustfaibra.shoesstore.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.LayoutDirection
import android.widget.Toast
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.offset
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.core.text.layoutDirection
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.mustfaibra.shoesstore.models.BookmarkItemWithProduct
import com.mustfaibra.shoesstore.models.CartItem
import com.mustfaibra.shoesstore.models.CartItemWithProduct
import com.mustfaibra.shoesstore.models.LocalManufacturer
import com.mustfaibra.shoesstore.models.LocalProduct
import com.mustfaibra.shoesstore.models.Manufacturer
import com.mustfaibra.shoesstore.models.Product
import com.mustfaibra.shoesstore.sealed.DataResponse
import com.mustfaibra.shoesstore.sealed.Error
import com.mustfaibra.shoesstore.sealed.Orientation
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.statement.*
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

/** An extension function that is used to convert the API response to a JSONObject & return the field message from it */
suspend fun HttpResponse.getMessage(): String {
    /** The json string */
    val responseAsString = this.receive<String>()
    /** convert the json string to a JSONObject that we can extract the message from it */
    return try {
        val jsonObj = JSONObject(responseAsString)
        jsonObj.getString("message") ?: "No message provided !"
    } catch (exception: JSONException) {
        "Server error, please call the support!"
    }
}

/**
 * An extension function that is used to handle the exception that occur when fetching from server
 * it send the report log - later - to server and return a valid response
 */
fun <T> Throwable.handleResponseException(): DataResponse<T> {
    return when (this) {
        is RedirectResponseException -> {
            DataResponse.Error(error = Error.Empty)
        }
        is ClientRequestException -> {
            DataResponse.Error(error = Error.Network)
        }
        is ServerResponseException -> {
            DataResponse.Error(error = Error.Unknown)
        }
        else -> {
            DataResponse.Error(error = Error.Network)
        }
    }
}


/** An extension function that is used to mirror the compose icons when using rtl languages like arabic and urdu */
fun Modifier.mirror(): Modifier {
    return when (Locale.getDefault().layoutDirection) {
        /** If app layout direction is rtl , then flip our icon horizontally (as a mirror) */
        LayoutDirection.RTL -> this.scale(scaleX = -1f, scaleY = 1f)
        /** If is ltr , just forget about this amigo ! */
        else -> this
    }
}


/** An extension function to convert time in milliseconds to a formatted time HH:MM:ss */
@Composable
fun Long.toTimeString(): String {
    return if (this <= 0) "--:--" else {
        val time = this / 1000 // convert from milliseconds to seconds
        val timeString = StringBuilder()

        val hours = time / 3600
        timeString.append(if (hours > 0) hours.prettifyTime().plus(":") else "")
        val minutes = (time % 3600) / 60
        timeString.append(minutes.prettifyTime().plus(":"))
        val seconds = time % 60
        timeString.append(seconds.prettifyTime())
        Timber.d("Milliseconds is $this and formatted time is $timeString")
        return timeString.toString()
    }
}

private fun Long.prettifyTime() = if (this < 10) "0$this" else "$this"

@SuppressLint("ComposableModifierFactory")
@Composable
fun Modifier.myPlaceHolder(
    visible: Boolean,
    shape: Shape = MaterialTheme.shapes.small,
    color: Color = MaterialTheme.colors.onBackground.copy(alpha = 0.1f),
    highLightColor: Color = MaterialTheme.colors.surface,
) = this.placeholder(
    visible = visible,
    color = color,
    shape = shape,
    highlight = PlaceholderHighlight.shimmer(
        highlightColor = highLightColor
    ),
)

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showToast(message: Int) {
    Toast.makeText(this, this.getText(message), Toast.LENGTH_LONG).show()
}

/**
 *  An extension function that is used to append an element to a list - or remove it in case it already exist.
 * Return the element if added or null if removed
 */
fun <T> MutableList<T>.appendOrRemove(element: T): T? {
    remove(element).also { removed ->
        return if (removed) {
            /** Removed successfully */
            null
        } else {
            /** Not exist, we should add it */
            this.add(element = element)
            element
        }
    }
}

fun Modifier.addMoveAnimation(orientation: Orientation, from: Dp, to: Dp, duration: Int): Modifier =
    composed {
        var contentOffset by remember { mutableStateOf(from) }
        val animatedContentOffset by animateDpAsState(
            targetValue = contentOffset,
            animationSpec = TweenSpec(
                durationMillis = duration,
            )
        ).also {
            contentOffset = to
        }
        when (orientation) {
            is Orientation.Vertical -> this.offset(y = animatedContentOffset)
            is Orientation.Horizontal -> this.offset(x = animatedContentOffset)
        }
    }

fun Modifier.addFadeAnimation(from: Float, to: Float, duration: Int): Modifier = composed {
    var contentAlpha by remember { mutableStateOf(from) }
    val animatedContentAlpha by animateFloatAsState(
        targetValue = contentAlpha,
        animationSpec = TweenSpec(
            durationMillis = duration,
        )
    ).also {
        contentAlpha = to
    }
    this.alpha(animatedContentAlpha)
}

fun String.getValidColor() = when (this) {
    "white" -> 0xFFFFFFFF
    "gold" -> 0xFFFFC107
    "yellow" -> 0xFFFFEB3B
    "green" -> 0xFF4CAF50
    "dark-green" -> 0xFF3C613E
    "lemon" -> 0xFF44FF00
    "red" -> 0xFFF44336
    "black" -> 0xFF000000
    "gray" -> 0xFF494949
    "pink" -> 0xFFC95E90
    else -> 0xFF000000
}

fun Double.getDiscountedValue(discount: Int) = this - this.times((discount.div(100)))

fun List<LocalManufacturer>.getStructuredManufacturers(): List<Manufacturer> {
    return this.map { localManufacturer ->
        localManufacturer.manufacturer.also {
            it.products.addAll(localManufacturer.products.getStructuredProducts())
        }
    }
}

fun List<LocalProduct>.getStructuredProducts(): List<Product> {
    return this.map { localProduct ->
        localProduct.product.also { product ->
            product.manufacturer = localProduct.manufacturer
            product.copies = localProduct.copies
            product.reviews = localProduct.reviews
            product.sizes = localProduct.sizes
        }
    }
}

fun LocalProduct.getStructuredProduct() = this.product.also { product ->
    product.manufacturer = this.manufacturer
    product.copies = this.copies
    product.reviews = this.reviews
    product.sizes = this.sizes
}

fun MutableList<CartItemWithProduct>.getStructuredCartItems(): List<CartItem> {
    return this.map {
        it.details.apply {
            this.product = it.product.getStructuredProduct()
        }
    }
}

fun MutableList<BookmarkItemWithProduct>.getStructuredBookmarkItems(): List<Product> {
    return this.map {
        it.product.getStructuredProduct()
    }
}

fun List<Any?>.neitherNull() : Boolean {
    return this.all { it != null }
}

