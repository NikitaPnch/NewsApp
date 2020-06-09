package com.example.newsapp.extensions

import android.net.Uri
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.Priority
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder

fun SimpleDraweeView.createImageRequest(uri: String) {
    val request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
        .setResizeOptions(ResizeOptions(480, 480))
        .setRequestPriority(Priority.HIGH)
        .setProgressiveRenderingEnabled(true)
        .setPostprocessor(FastGreyPostProcessor())
        .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
        .build()

    val roundingParams = RoundingParams
        .fromCornersRadius(16f)
        .setPaintFilterBitmap(true)

    this.hierarchy.roundingParams = roundingParams

    this.controller = Fresco.newDraweeControllerBuilder()
        .setOldController(this.controller)
        .setImageRequest(request)
        .build()
}