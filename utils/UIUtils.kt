package com.kippu.trace.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Applies a horizontal fade-to-transparent effect on the right edge of the content.
 * Fixed the "translucent" issue by using a much sharper step-based gradient stop.
 */
fun Modifier.fadeRightEdge(
    fadeWidth: Dp = 48.dp 
): Modifier = this.graphicsLayer {
    // Crucial: Forces the content to be drawn to an offscreen buffer first.
    // This allows BlendMode.DstIn to mask the entire content correctly 
    // against the background.
    compositingStrategy = CompositingStrategy.Offscreen
}.drawWithContent {
    drawContent()
    val fadeWidthPx = fadeWidth.toPx()
    val width = size.width
    
    if (width > fadeWidthPx) {
        // RADICAL FIX FOR "TRANSLUCENT" LOOK:
        // We use discrete, tight color stops to create a "Step" function.
        // Instead of a smooth curve, we force the alpha to plunge at a specific point.
        drawRect(
            brush = Brush.horizontalGradient(
                colorStops = arrayOf(
                    0.0f to Color.Black,
                    0.6f to Color.Black,      // Hold 100% solid for 60% of the zone
                    0.7f to Color.Transparent, // PLUNGE to 0% in just 10% distance
                    1.0f to Color.Transparent  // Remaining space is total void
                ),
                startX = width - fadeWidthPx,
                endX = width
            ),
            blendMode = BlendMode.DstIn
        )
    }
}

/**
 * Applies a fade-out effect ONLY to the bottom-right corner of a multi-line text block.
 */
fun Modifier.fadeLastLineEdge(
    fadeWidth: Dp = 48.dp,
    lastLineHeightFraction: Float = 0.25f 
): Modifier = this.graphicsLayer {
    compositingStrategy = CompositingStrategy.Offscreen
}.drawWithContent {
    drawContent()
    
    val width = size.width
    val height = size.height
    val lastLineStart = height * (1f - lastLineHeightFraction)
    val fadeWidthPx = fadeWidth.toPx()

    // 1. Preserve the top 3 lines (or non-last line area)
    drawRect(
        color = Color.Black,
        size = androidx.compose.ui.geometry.Size(width, lastLineStart),
        blendMode = BlendMode.DstIn
    )

    // 2. Apply the SHARP step-down fade to the bottom portion
    if (width > fadeWidthPx) {
        drawRect(
            brush = Brush.horizontalGradient(
                colorStops = arrayOf(
                    0.0f to Color.Black,
                    0.6f to Color.Black,
                    0.7f to Color.Transparent,
                    1.0f to Color.Transparent
                ),
                startX = width - fadeWidthPx,
                endX = width
            ),
            topLeft = androidx.compose.ui.geometry.Offset(0f, lastLineStart),
            size = androidx.compose.ui.geometry.Size(width, height - lastLineStart),
            blendMode = BlendMode.DstIn
        )
    } else {
        drawRect(
            color = Color.Black,
            topLeft = androidx.compose.ui.geometry.Offset(0f, lastLineStart),
            size = androidx.compose.ui.geometry.Size(width, height - lastLineStart),
            blendMode = BlendMode.DstIn
        )
    }
}
