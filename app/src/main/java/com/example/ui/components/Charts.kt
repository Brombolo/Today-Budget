package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// Data representation for Charts
data class ChartCategoryData(
    val categoryId: Int,
    val name: String,
    val icon: String,
    val amount: Double,
    val color: Color
)

data class MonthlyTrendPoint(
    val monthLabel: String,
    val amount: Double,
    val savings: Double, // positive = saved, negative = overspend
    val categoriesAmount: Map<Int, Double> = emptyMap() // categoryId to spent amount
)

@Composable
fun DonutChart(
    data: List<ChartCategoryData>,
    modifier: Modifier = Modifier,
    centerLabel: String = "Totale",
    centerValue: String = "€0.00",
    currencySymbol: String = "€"
) {
    val total = data.sumOf { it.amount }
    if (total == 0.0) {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nessuna spesa registrata",
                style = MaterialTheme.typography.bodyMedium,
                color = SweetTextLight
            )
        }
        return
    }

    var animateTrigger by remember { mutableStateOf(false) }
    val animationProgress by animateFloatAsState(
        targetValue = if (animateTrigger) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(data) {
        animateTrigger = true
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Chart sphere - Centered
        Box(
            modifier = Modifier
                .size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 32f
                val sizeMin = size.minDimension
                val radius = (sizeMin - strokeWidth) / 2
                val centerPoint = Offset(size.width / 2, size.height / 2)

                var startAngle = -90f
                data.forEach { item ->
                    val sweepAngle = ((item.amount / total) * 360f).toFloat() * animationProgress
                    drawArc(
                        color = item.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(centerPoint.x - radius, centerPoint.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    startAngle += sweepAngle
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = centerLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = SweetTextLight
                )
                Text(
                    text = centerValue,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
            }
        }

        // Legend list (Expanded space below to support all 10 categories)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            data.filter { it.amount > 0.0 }.sortedByDescending { it.amount }.forEach { item ->
                val percentage = (item.amount / total * 100).toInt()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(item.color, RoundedCornerShape(5.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${item.icon} ${item.name}",
                        style = TextStyle(fontSize = 12.sp, color = SweetTextDark),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = String.format("%d%% (%s %.2f)", percentage, currencySymbol, item.amount),
                        style = TextStyle(fontSize = 11.sp, color = SweetTextLight, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun TrendLineChart(
    points: List<MonthlyTrendPoint>,
    selectedCategoryId: Int? = null, // null for total, non-null for specific category
    selectedCategoryName: String = "Totale",
    currencySymbol: String = "€",
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()

    val amounts = points.map { pt ->
        if (selectedCategoryId != null) {
            pt.categoriesAmount[selectedCategoryId] ?: 0.0
        } else {
            pt.amount
        }
    }

    val maxAmount = (amounts.maxOrNull() ?: 100.0).coerceAtLeast(10.0)
    val gridCount = 4

    var animateTrigger by remember { mutableStateOf(false) }
    val animationProgress by animateFloatAsState(
        targetValue = if (animateTrigger) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(points, selectedCategoryId) {
        animateTrigger = true
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Andamento Spese - $selectedCategoryName",
            style = MaterialTheme.typography.titleMedium,
            color = SweetTextDark
        )
        Text(
            text = "Valori mensili degli ultimi cicli inseriti",
            style = MaterialTheme.typography.labelSmall,
            color = SweetTextLight,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            val paddingLeft = 110f
            val paddingBottom = 60f
            val paddingTop = 10f
            val paddingRight = 20f

            val chartWidth = size.width - paddingLeft - paddingRight
            val chartHeight = size.height - paddingBottom - paddingTop

            // Draw Y Grid lines & text labels
            for (i in 0..gridCount) {
                val yVal = maxAmount * (i.toDouble() / gridCount.toDouble())
                val yPos = size.height - paddingBottom - (chartHeight * (i.toFloat() / gridCount.toFloat()))

                drawLine(
                    color = SweetCardGlow,
                    start = Offset(paddingLeft, yPos),
                    end = Offset(size.width - paddingRight, yPos),
                    strokeWidth = 2f
                )

                // Label Text
                val labelText = String.format("%s%.0f", currencySymbol, yVal)
                drawText(
                    textMeasurer = textMeasurer,
                    text = labelText,
                    topLeft = Offset(10f, yPos - 20f),
                    style = TextStyle(fontSize = 10.sp, color = SweetTextLight)
                )
            }

            // Draw Points & Lines
            val colWidth = if (points.size > 1) chartWidth / (points.size - 1) else chartWidth
            val pathPoints = mutableListOf<Offset>()

            points.forEachIndexed { index, pt ->
                val amt = if (selectedCategoryId != null) {
                    pt.categoriesAmount[selectedCategoryId] ?: 0.0
                } else {
                    pt.amount
                }

                val xPos = paddingLeft + (index * colWidth)
                val yFraction = (amt / maxAmount).toFloat()
                val yPos = size.height - paddingBottom - (chartHeight * yFraction) * animationProgress

                val pointOffset = Offset(xPos, yPos)
                pathPoints.add(pointOffset)

                // Draw X Label for months
                drawText(
                    textMeasurer = textMeasurer,
                    text = pt.monthLabel,
                    topLeft = Offset(xPos - 50f, size.height - paddingBottom + 10f),
                    style = TextStyle(fontSize = 11.sp, color = SweetTextDark)
                )
            }

            // Draw line
            if (pathPoints.size >= 2) {
                for (i in 0 until pathPoints.size - 1) {
                    drawLine(
                        color = SweetPrimary,
                        start = pathPoints[i],
                        end = pathPoints[i + 1],
                        strokeWidth = 6f,
                        cap = StrokeCap.Round
                    )
                }

                // Fill area below trend line with light gradient
                val fillPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(pathPoints.first().x, size.height - paddingBottom)
                    pathPoints.forEach { linePoint ->
                        lineTo(linePoint.x, linePoint.y)
                    }
                    lineTo(pathPoints.last().x, size.height - paddingBottom)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(SweetPrimary.copy(alpha = 0.3f), Color.Transparent),
                        startY = paddingTop,
                        endY = size.height - paddingBottom
                    )
                )
            }

            // Draw dot on nodes
            pathPoints.forEach { pt ->
                drawCircle(
                    color = Color.White,
                    radius = 8f,
                    center = pt
                )
                drawCircle(
                    color = SweetPrimary,
                    radius = 5f,
                    center = pt
                )
            }
        }
    }
}

/**
 * Savings vs Overspend Chart.
 * Upward green bars for savings, Downward red bars for overspent budgets.
 */
@Composable
fun MonthlySavingsChart(
    points: List<MonthlyTrendPoint>,
    currencySymbol: String = "€",
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()

    val maxAbsValue = points.map { Math.abs(it.savings) }.maxOrNull()?.coerceAtLeast(50.0) ?: 100.0

    var animateTrigger by remember { mutableStateOf(false) }
    val animationProgress by animateFloatAsState(
        targetValue = if (animateTrigger) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(points) {
        animateTrigger = true
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Andamento Risparmio",
            style = MaterialTheme.typography.titleMedium,
            color = SweetTextDark
        )
        Text(
            text = "Verde = Risparmio | Rosso = Sforamento",
            style = MaterialTheme.typography.labelSmall,
            color = SweetTextLight,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            val paddingLeft = 100f
            val paddingBottom = 40f
            val paddingTop = 10f
            val paddingRight = 20f

            val chartWidth = size.width - paddingLeft - paddingRight
            val chartHeight = size.height - paddingBottom - paddingTop

            // Baseline for zero
            val zeroY = paddingTop + (chartHeight / 2f)

            // Draw baseline
            drawLine(
                color = SweetTextLight.copy(alpha = 0.6f),
                start = Offset(paddingLeft, zeroY),
                end = Offset(size.width - paddingRight, zeroY),
                strokeWidth = 3f
            )

            // Draw bounds texts
            drawText(
                textMeasurer = textMeasurer,
                text = String.format("+%s%.0f", currencySymbol, maxAbsValue),
                topLeft = Offset(10f, paddingTop),
                style = TextStyle(fontSize = 10.sp, color = PastelMint)
            )
            drawText(
                textMeasurer = textMeasurer,
                text = String.format("-%s%.0f", currencySymbol, maxAbsValue),
                topLeft = Offset(10f, size.height - paddingBottom - 25f),
                style = TextStyle(fontSize = 10.sp, color = PastelRose)
            )

            // Draw positive/negative bars
            val barCount = points.size
            val spacing = 25f
            val totalSpacing = spacing * (barCount + 1)
            val barWidth = (chartWidth - totalSpacing) / barCount

            points.forEachIndexed { index, pt ->
                val saving = pt.savings
                val isPositive = saving >= 0.0

                val barHeightFraction = (Math.abs(saving) / maxAbsValue).toFloat() * animationProgress
                val barHeight = (chartHeight / 2f) * barHeightFraction

                val xPos = paddingLeft + spacing + index * (barWidth + spacing)
                val barColor = if (isPositive) PastelMint else PastelRose

                val topY = if (isPositive) zeroY - barHeight else zeroY
                val rectSize = Size(barWidth, barHeight)

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(xPos, topY),
                    size = rectSize,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                )

                // Label on X bar
                val labelOffset = if (isPositive) zeroY + 10f else zeroY - 30f
                drawText(
                    textMeasurer = textMeasurer,
                    text = pt.monthLabel,
                    topLeft = Offset(xPos + (barWidth / 2f) - 25f, labelOffset),
                    style = TextStyle(fontSize = 10.sp, color = SweetTextDark)
                )
            }
        }
    }
}
