package com.example.newsapp.extensions

import android.net.Uri
import com.example.newsapp.R
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.AutoRotateDrawable
import com.facebook.drawee.drawable.ScalingUtils
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
        .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
        .build()

    val progressBarDrawable = resources.getDrawable(R.drawable.ic_circle_notch_solid, null)

    val roundingParams = RoundingParams
        .fromCornersRadius(16f)
        .setPaintFilterBitmap(true)

    this.hierarchy.apply {
        this.roundingParams = roundingParams
        this.setProgressBarImage(
            AutoRotateDrawable(progressBarDrawable, 1000),
            ScalingUtils.ScaleType.CENTER
        )
    }

    this.controller = Fresco.newDraweeControllerBuilder()
        .setOldController(this.controller)
        .setImageRequest(request)
        .build()
}