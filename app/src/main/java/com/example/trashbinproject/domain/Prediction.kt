package com.example.trashbinproject.domain

import com.google.gson.annotations.SerializedName

data class ClassificationResult(
    @SerializedName("output_image") val outputImage: String,
    val predictions: PredictionBlock
)

data class Prediction(
    val width: Int,
    val height: Int,
    val x: Float,
    val y: Float,
    val confidence: Float,
    val class_id: Int,
    @SerializedName("class") val className: String,
    @SerializedName("detection_id") val detectionId: String,
    @SerializedName("parent_id") val parentId: String
)

data class ImageInfo(
    val width: Int,
    val height: Int
)

data class PredictionBlock(
    val image: ImageInfo,
    val predictions: List<Prediction>
)