package com.example.newsapp.extensions

import android.graphics.Bitmap

import com.facebook.imagepipeline.request.BasePostprocessor

class FastGreyPostProcessor : BasePostprocessor() {
    override fun process(bitmap: Bitmap) {
        val w = bitmap.width
        val h = bitmap.height
        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
        for (x in 0 until w) {
            for (y in 0 until h) {
                val offset = y * w + x
                pixels[offset] =
                    getGreyColor(pixels[offset])
            }
        }

        bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
    }

    companion object {
        fun getGreyColor(color: Int): Int {
            val alpha = color and -0x1000000
            val r = color shr 16 and 0xFF
            val g = color shr 8 and 0xFF
            val b = color and 0xFF

            val luminance = (0.2126 * r + 0.7152 * g + 0.0722 * b).toInt()
            return alpha or (luminance shl 16) or (luminance shl 8) or luminance
        }
    }
}