package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.ui.loc
import com.example.ui.formatCurrency
import java.util.Locale
import androidx.compose.foundation.Canvas

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
    val budget: Double = 0.0,
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
            modifier = modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)).padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nessuna spesa registrata".loc(),
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

        // Legend list (Expanded space below to support all categories)
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
                        text = String.format(Locale.ITALY, "%d%% (%s %s)", percentage, currencySymbol, item.amount.formatCurrency()),
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
    selectedCategoryId: Int? = null,
    selectedCategoryName: String = "Totale",
    currencySymbol: String = "€",
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()

    val maxAmount = points.flatMap { listOf(it.amount, it.budget) }.maxOrNull()?.coerceAtLeast(100.0) ?: 100.0
    val gridCount = 4

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
            text = "Andamento Spese".loc() + if (selectedCategoryId != null) " - $selectedCategoryName" else "",
            style = MaterialTheme.typography.titleMedium,
            color = if (isEnabled) SweetTextDark else SweetTextLight
        )
        
        if (isEnabled && selectedCategoryId == null) {
            Row(modifier = Modifier.padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LegendItem(color = SweetPrimary, label = "Spese".loc())
                LegendItem(color = PastelLavender.copy(alpha = 0.6f), label = "Budget".loc())
            }
        }

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

            // Draw Y Grid lines
            for (i in 0..gridCount) {
                val yVal = maxAmount * (i.toDouble() / gridCount.toDouble())
                val yPos = size.height - paddingBottom - (chartHeight * (i.toFloat() / gridCount.toFloat()))

                drawLine(color = SweetCardGlow, start = Offset(paddingLeft, yPos), end = Offset(size.width - paddingRight, yPos), strokeWidth = 2f)
                drawText(
                    textMeasurer = textMeasurer,
                    text = String.format(Locale.ITALY, "%s%.0f", currencySymbol, yVal),
                    topLeft = Offset(10f, yPos - 15f),
                    style = TextStyle(fontSize = 10.sp, color = SweetTextLight)
                )
            }

            val colWidth = if (points.size > 1) chartWidth / (points.size - 1) else chartWidth
            
            // Draw Budget Line (Point 12.1) - only for overall
            if (selectedCategoryId == null && isEnabled) {
                val budgetPoints = points.mapIndexed { idx, pt ->
                    Offset(paddingLeft + (idx * colWidth), size.height - paddingBottom - (chartHeight * (pt.budget / maxAmount).toFloat()))
                }
                for (i in 0 until budgetPoints.size - 1) {
                    drawLine(color = PastelLavender.copy(alpha = 0.5f), start = budgetPoints[i], end = budgetPoints[i + 1], strokeWidth = 4f, cap = StrokeCap.Round)
                }
            }

            // Draw Spending Line
            val pathPoints = points.mapIndexed { index, pt ->
                val amt = if (selectedCategoryId != null) pt.categoriesAmount[selectedCategoryId] ?: 0.0 else pt.amount
                val xPos = paddingLeft + (index * colWidth)
                val yPos = size.height - paddingBottom - (chartHeight * (amt / maxAmount).toFloat()) * animationProgress
                
                // Draw X Label
                drawText(textMeasurer = textMeasurer, text = pt.monthLabel, topLeft = Offset(xPos - 30f, size.height - paddingBottom + 10f), style = TextStyle(fontSize = 10.sp, color = SweetTextDark))
                
                Offset(xPos, yPos)
            }

            if (pathPoints.size >= 2) {
                for (i in 0 until pathPoints.size - 1) {
                    drawLine(color = SweetPrimary, start = pathPoints[i], end = pathPoints[i + 1], strokeWidth = 6f, cap = StrokeCap.Round)
                }
            }

            pathPoints.forEach { pt ->
                drawCircle(color = Color.White, radius = 7f, center = pt)
                drawCircle(color = SweetPrimary, radius = 4f, center = pt)
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = TextStyle(fontSize = 10.sp, color = SweetTextLight))
    }
}

@Composable
fun MonthlySavingsChart(
    points: List<MonthlyTrendPoint>,
    currencySymbol: String = "€",
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return
    val textMeasurer = rememberTextMeasurer()

    val maxAbsValue = points.map { Math.abs(it.savings) }.maxOrNull()?.coerceAtLeast(50.0) ?: 100.0
    val avgSaving = if (points.isNotEmpty()) points.sumOf { it.savings } / points.size else 0.0

    var animateTrigger by remember { mutableStateOf(false) }
    val animationProgress by animateFloatAsState(
        targetValue = if (animateTrigger) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(points) { animateTrigger = true }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Andamento Risparmio".loc(),
            style = MaterialTheme.typography.titleMedium,
            color = if (isEnabled) SweetTextDark else SweetTextLight
        )
        Text(
            text = "Verde = Risparmio | Rosso = Sforamento".loc(),
            style = MaterialTheme.typography.labelSmall,
            color = SweetTextLight,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
            val paddingLeft = 100f
            val paddingBottom = 40f
            val paddingTop = 10f
            val chartWidth = size.width - paddingLeft - 20f
            val chartHeight = size.height - paddingBottom - paddingTop
            val zeroY = paddingTop + (chartHeight / 2f)

            // Baseline
            drawLine(color = SweetTextLight.copy(alpha = 0.4f), start = Offset(paddingLeft, zeroY), end = Offset(size.width - 20f, zeroY), strokeWidth = 2f)

            // Average Line (Point 12.2)
            if (isEnabled) {
                val avgY = zeroY - (avgSaving / maxAbsValue).toFloat() * (chartHeight / 2f)
                drawLine(
                    color = PastelLavender,
                    start = Offset(paddingLeft, avgY),
                    end = Offset(size.width - 20f, avgY),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round,
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )
                drawText(textMeasurer, "Media: ".loc() + avgSaving.formatCurrency(), Offset(paddingLeft, avgY - 25f), style = TextStyle(fontSize = 9.sp, color = PastelLavender, fontWeight = FontWeight.Bold))
            }

            // Bars
            val barWidth = (chartWidth / points.size.toFloat()) * 0.7f
            val spacing = (chartWidth / points.size.toFloat()) * 0.3f

            points.forEachIndexed { index, pt ->
                val xPos = paddingLeft + spacing / 2 + index * (barWidth + spacing)
                val barHeight = (Math.abs(pt.savings) / maxAbsValue).toFloat() * (chartHeight / 2f) * animationProgress
                val topY = if (pt.savings >= 0) zeroY - barHeight else zeroY
                
                drawRoundRect(
                    color = if (pt.savings >= 0) PastelMint else PastelRose,
                    topLeft = Offset(xPos, topY),
                    size = Size(barWidth, barHeight.coerceAtLeast(1f)),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                )
                
                drawText(textMeasurer, pt.monthLabel, Offset(xPos + barWidth / 2 - 15f, size.height - 30f), style = TextStyle(fontSize = 9.sp, color = SweetTextDark))
            }
        }
    }
}
