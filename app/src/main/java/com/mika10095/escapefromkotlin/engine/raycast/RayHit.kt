package com.mika10095.escapefromkotlin.engine.raycast

data class RayHit(
    val distance: Float,
    val wallX: Float,
    val tile: Int,
    val side: Int
)