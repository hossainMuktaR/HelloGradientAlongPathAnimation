package com.example.hellogradientalongpathanimation


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.flatten
import kotlin.math.floor

@Preview
@Composable
fun GradientAlongPathAnimation() {
    val path = remember {
        HelloPath.path.toPath()
    }
    val bounds = remember {
        path.getBounds()
        }

    val totalLength = remember {
        PathMeasure().apply {
            setPath(path, false)
        }.length
    }
    val lines = remember {
        path.asAndroidPath().flatten(0.5f)
    }
    val progress = remember {
        Animatable(0f)
    }
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(5000),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .padding(top = 32.dp, start = 64.dp, bottom = 32.dp, end = 32.dp)
                .aspectRatio(bounds.width / bounds.height)
                .size(400.dp)
                .align(Alignment.Center),
            onDraw = {
                val currentLength = totalLength * progress.value
                lines.forEach{ line ->
                    if ((line.startFraction * totalLength) < currentLength){
                        val startColor = interpolateColors(line.startFraction)
                        val endColor = interpolateColors(line.endFraction)
                        drawLine(
                            brush = Brush.linearGradient(listOf(startColor,endColor)),
                            start = Offset(line.start.x, line.start.y),
                            end = Offset(line.end.x, line.end.y),
                            strokeWidth = 30f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            } ,
        )
    }
}

private val colors = listOf(
    Color(0xFF3FCEBC),
    Color(0xFF3CBCEB),
    Color(0xFF5F96E7),
    Color(0xFF816FE3),
    Color(0xFF9F5EE2),
    Color(0xFFBD4CE0),
    Color(0xFFDE589F),
    Color(0xFFFF645E),
    Color(0xFFFDA859),
    Color(0xFFFAEC54),
    Color(0xFF9EE671),
    Color(0xFF67E282),
    Color(0xFF3FCEBC)
)

private object HelloPath {
    val path = PathParser().parsePathString(
        "M13.63 248.31C13.63 248.31 51.84 206.67 84.21 169.31C140.84 103.97 202.79 27.66 150.14 14.88C131.01 10.23 116.36 29.88 107.26 45.33C69.7 108.92 58.03 214.33 57.54 302.57C67.75 271.83 104.43 190.85 140.18 193.08C181.47 195.65 145.26 257.57 154.53 284.39C168.85 322.18 208.22 292.83 229.98 277.45C265.92 252.03 288.98 231.22 288.98 200.45C288.98 161.55 235.29 174.02 223.3 205.14C213.93 229.44 214.3 265.89 229.3 284.14C247.49 306.28 287.67 309.93 312.18 288.46C337 266.71 354.66 234.56 368.68 213.03C403.92 158.87 464.36 86.15 449.06 30.03C446.98 22.4 440.36 16.57 432.46 16.26C393.62 14.75 381.84 99.18 375.35 129.31C368.78 159.83 345.17 261.31 373.11 293.06C404.43 328.58 446.29 262.4 464.66 231.67C468.66 225.31 472.59 218.43 476.08 213.07C511.33 158.91 571.77 86.19 556.46 30.07C554.39 22.44 547.77 16.61 539.87 16.3C501.03 14.79 489.25 99.22 482.76 129.35C476.18 159.87 452.58 261.35 480.52 293.1C511.83 328.62 562.4 265.53 572.64 232.86C587.34 185.92 620.94 171.58 660.91 180.29C616 166.66 580.86 199.67 572.64 233.16C566.81 256.93 573.52 282.16 599.25 295.77C668.54 332.41 742.8 211.69 660.91 180.29C643.67 181.89 636.15 204.77 643.29 227.78C654.29 263.97 704.29 268.27 733.08 256"
    )
}
private fun interpolateColors(
    progress: Float,
    colorsInput: List<Color> = colors
): Color{
    if(progress == 1f) return colorsInput.last()

    val scaledProgress = (colorsInput.size - 1) * progress
    val oldColor = colorsInput[scaledProgress.toInt()]
    val newColor = colorsInput[(scaledProgress + 1f).toInt()]
    val newScaledAnimationValue = scaledProgress - floor(scaledProgress)
    return lerp(
        start = oldColor,
        stop = newColor,
        fraction = newScaledAnimationValue
    )

}