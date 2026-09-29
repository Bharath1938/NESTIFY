package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NestifyPrimary
import com.example.ui.theme.NestifySecondary
import com.example.ui.theme.NestifySuccess
import kotlin.math.roundToInt

@Composable
fun BeforeAfterTransformationCard(
  title: String = "Master Closet Revamp",
  serviceCategory: String = "Wardrobe",
  beforeSubtitle: String = "Chaotic stacks, tangled hangers & lost items",
  afterSubtitle: String = "Boutique color-coded layout with velvet hangers",
  modifier: Modifier = Modifier
) {
  var isSideBySide by remember { mutableStateOf(false) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .animateContentSize(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header with mode toggle
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = NestifyPrimary.copy(alpha = 0.12f)
            ) {
              Text(
                text = serviceCategory.uppercase(),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                color = NestifyPrimary,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "100% Real Home Transformation",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        }

        IconButton(
          onClick = { isSideBySide = !isSideBySide },
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = if (isSideBySide) Icons.Default.CompareArrows else Icons.Default.ViewColumn,
            contentDescription = "Switch view mode",
            tint = NestifyPrimary
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      if (!isSideBySide) {
        // Interactive Slider View
        InteractiveSliderCanvas(
          modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(16.dp))
        )
      } else {
        // Side by Side View
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(210.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
          ) {
            BeforeVisual(modifier = Modifier.fillMaxSize())
            Surface(
              modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp),
              shape = RoundedCornerShape(6.dp),
              color = Color(0xCCEF4444)
            ) {
              Text(
                text = "BEFORE",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
          ) {
            AfterVisual(modifier = Modifier.fillMaxSize())
            Surface(
              modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
              shape = RoundedCornerShape(6.dp),
              color = Color(0xCC22C55E)
            ) {
              Text(
                text = "AFTER",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Transformation Results Metric Strip
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
          .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        MetricItem(label = "Space Reclaimed", value = "+65%")
        MetricItem(label = "Time Saved", value = "25m/day")
        MetricItem(label = "Organization", value = "KonMari")
      }
    }
  }
}

@Composable
fun InteractiveSliderCanvas(
  modifier: Modifier = Modifier
) {
  var sliderRatio by remember { mutableFloatStateOf(0.5f) }
  val density = LocalDensity.current

  BoxWithConstraints(modifier = modifier) {
    val totalWidth = maxWidth
    val totalWidthPx = with(density) { totalWidth.toPx() }

    // Bottom layer: After (Organized)
    AfterVisual(modifier = Modifier.fillMaxSize())

    // Top layer clipped to sliderRatio: Before (Cluttered)
    Box(
      modifier = Modifier
        .fillMaxSize()
        .width(totalWidth * sliderRatio)
        .clip(RoundedCornerShape(0.dp))
    ) {
      BeforeVisual(modifier = Modifier.fillMaxSize())
    }

    // Divider Line and Handle
    val handleOffsetPx = totalWidthPx * sliderRatio
    val handleOffsetDp = with(density) { handleOffsetPx.toDp() }

    // Vertical Divider Bar
    Box(
      modifier = Modifier
        .offset { IntOffset(x = handleOffsetPx.roundToInt() - with(density) { 1.dp.toPx() }.roundToInt(), y = 0) }
        .width(2.dp)
        .fillMaxSize()
        .background(Color.White)
    )

    // Draggable Handle
    Box(
      modifier = Modifier
        .align(Alignment.CenterStart)
        .offset(x = handleOffsetDp - 20.dp)
        .size(40.dp)
        .shadow(6.dp, CircleShape)
        .background(Color.White, CircleShape)
        .border(2.dp, NestifyPrimary, CircleShape)
        .pointerInput(Unit) {
          detectDragGestures { change, dragAmount ->
            change.consume()
            val newX = (sliderRatio * totalWidthPx) + dragAmount.x
            sliderRatio = (newX / totalWidthPx).coerceIn(0.05f, 0.95f)
          }
        },
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Compare,
        contentDescription = "Slide to compare before and after",
        tint = NestifyPrimary,
        modifier = Modifier.size(20.dp)
      )
    }

    // "BEFORE" Badge (Left)
    Surface(
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(10.dp),
      shape = RoundedCornerShape(6.dp),
      color = Color(0xB31F2937)
    ) {
      Text(
        text = "BEFORE",
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      )
    }

    // "AFTER" Badge (Right)
    Surface(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(10.dp),
      shape = RoundedCornerShape(6.dp),
      color = Color(0xCC22C55E)
    ) {
      Text(
        text = "AFTER",
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      )
    }

    // Instruction prompt
    Surface(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 8.dp),
      shape = RoundedCornerShape(12.dp),
      color = Color(0x99000000)
    ) {
      Text(
        text = "⟵ Drag divider to compare ⟶",
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
        color = Color.White,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

@Composable
fun BeforeVisual(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFFE8D5C8),
            Color(0xFFC7B198),
            Color(0xFF8F7A65)
          )
        )
      )
  ) {
    // Stylized disorganized messy closet graphic representation
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(14.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top shelf cluttered
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(28.dp)
          .background(Color(0x66594A38), RoundedCornerShape(4.dp))
          .padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(modifier = Modifier.size(20.dp, 16.dp).background(Color(0xFFD4A373), RoundedCornerShape(2.dp)))
        Box(modifier = Modifier.size(32.dp, 20.dp).background(Color(0xFFA3704C), RoundedCornerShape(2.dp)))
        Box(modifier = Modifier.size(16.dp, 18.dp).background(Color(0xFF6B4D36), RoundedCornerShape(2.dp)))
        Box(modifier = Modifier.size(24.dp, 14.dp).background(Color(0xFFE29578), RoundedCornerShape(2.dp)))
      }

      // Middle hanging clothes - mismatched & jammed
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(90.dp)
          .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        val messyColors = listOf(
          Color(0xFF4A4E69), Color(0xFFC9ADA7), Color(0xFF22223B),
          Color(0xFF9A8C98), Color(0xFF6D597A), Color(0xFFB56576),
          Color(0xFFE56B6F), Color(0xFF355070)
        )
        messyColors.forEachIndexed { i, col ->
          val heightOffset = (i % 3) * 12
          Box(
            modifier = Modifier
              .width(14.dp)
              .height((75 - heightOffset).dp)
              .background(col, RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp))
          )
        }
      }

      // Floor cluttered pile
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(38.dp)
          .background(Color(0x773D312A), RoundedCornerShape(4.dp))
          .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
      ) {
        Box(modifier = Modifier.size(24.dp, 20.dp).background(Color(0xFF8D6E63), CircleShape))
        Box(modifier = Modifier.size(36.dp, 16.dp).background(Color(0xFFBCAAA4), RoundedCornerShape(4.dp)))
        Box(modifier = Modifier.size(28.dp, 26.dp).background(Color(0xFF5D4037), RoundedCornerShape(2.dp)))
        Box(modifier = Modifier.size(20.dp, 18.dp).background(Color(0xFFA1887F), CircleShape))
      }
    }
  }
}

@Composable
fun AfterVisual(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFFF8FAFC),
            Color(0xFFE2E8F0),
            Color(0xFFCBD5E1)
          )
        )
      )
  ) {
    // Stylized pristine, color-coordinated boutique organized closet
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(14.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top shelf: Matching labeled linen bins with uniform spacing
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(30.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        repeat(4) {
          Box(
            modifier = Modifier
              .weight(1f)
              .height(26.dp)
              .padding(horizontal = 3.dp)
              .background(Color.White, RoundedCornerShape(4.dp))
              .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
          ) {
            Box(
              modifier = Modifier
                .width(18.dp)
                .height(4.dp)
                .background(NestifyPrimary.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
            )
          }
        }
      }

      // Middle: Beautiful rainbow color gradient, identical matching slim hangers
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(95.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        val cleanGradient = listOf(
          Color(0xFFFFFFFF), Color(0xFFF1F5F9), Color(0xFFE2E8F0),
          Color(0xFF94A3B8), Color(0xFF64748B), Color(0xFF475569),
          Color(0xFF334155), Color(0xFF1E293B), Color(0xFF0F172A),
          Color(0xFF6366F1), Color(0xFF4F46E5), Color(0xFF4338CA)
        )
        cleanGradient.forEach { col ->
          Box(
            modifier = Modifier
              .width(12.dp)
              .height(80.dp)
              .background(col, RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
              .border(0.5.dp, Color.White.copy(alpha = 0.3f))
          )
        }
      }

      // Bottom shelf: Stackable clear drawer dividers & shoe boxes
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(34.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        repeat(5) {
          Box(
            modifier = Modifier
              .weight(1f)
              .height(28.dp)
              .padding(horizontal = 2.dp)
              .background(Color.White, RoundedCornerShape(4.dp))
              .border(1.dp, NestifySecondary.copy(alpha = 0.3f), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .background(NestifySuccess, CircleShape)
            )
          }
        }
      }
    }
  }
}

@Composable
fun MetricItem(label: String, value: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = value,
      style = MaterialTheme.typography.titleMedium,
      color = NestifyPrimary,
      fontWeight = FontWeight.Bold
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
