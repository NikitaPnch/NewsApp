package com.example.newsapp

import android.net.Uri
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder

fun SimpleDraweeView.createImageRequest(uri: String) {
    val request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
        .setResizeOptions(ResizeOptions(480, 480))
        .build()

    this.controller = Fresco.newDraweeControllerBuilder()
        .setOldController(this.controller)
        .setImageRequest(request)
        .build()
}