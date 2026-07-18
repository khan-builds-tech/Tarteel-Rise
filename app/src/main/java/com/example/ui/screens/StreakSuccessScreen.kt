package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun StreakSuccessScreen(
    viewModel: MainViewModel,
    onNavigateToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val streakData by viewModel.streak.collectAsState()
    val currentStreak = streakData?.currentStreak ?: 0

    // Animation scale for the celebration badge
    val transition = rememberInfiniteTransition(label = "celebration")
    val badgeScale by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badgeScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSlate)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Large Success Verification Badge
        Box(
            modifier = Modifier
                .size(140.dp)
                .scale(badgeScale),
            contentAlignment = Alignment.Center
        ) {
            // Background glow rings
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(RadiantEmerald.copy(alpha = 0.15f))
                    .blur(12.dp)
            )
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(RadiantEmerald.copy(alpha = 0.25f))
            )

            // Dynamic core fire / check indicator
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success Verification",
                tint = RadiantEmerald,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Success Text Headings
        Text(
            text = "ALARM DISMISSED",
            color = RadiantEmerald,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Recitation Verified",
            color = TextWhite,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your recitation matched the vocalized Ayah successfully at 80%+ accuracy.",
            color = SecondarySilver,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Updated Streak Counter Badge Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(NavyGray)
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak Fire",
                        tint = RadiantEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NEW STREAK LEVEL",
                        color = RadiantEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val displaysCount = currentStreak + 1
                Text(
                    text = "$displaysCount Days",
                    color = TextWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 36.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "A beautiful start to your day with Quranic recitation. Sleep well, rise early, and keep the streak alive tomorrow!",
                    color = SecondarySilver,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1.2f))

        // Confirm dismissal and Route back to Dashboard
        Button(
            onClick = {
                viewModel.completeStreakAndDismiss(context)
                onNavigateToDashboard()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = RadiantEmerald,
                contentColor = TextWhite
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("continue_to_dashboard_button")
        ) {
            Text(
                text = "CONTINUE TO DASHBOARD",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 1.sp
            )
        }
    }
}
