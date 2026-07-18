package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeOff
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmActiveScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeAyahText by viewModel.activeAyahText.collectAsState()
    val activeAyahTranslation by viewModel.activeAyahTranslation.collectAsState()
    val activeSurahTransliteration by viewModel.activeSurahTransliteration.collectAsState()

    val speechText by viewModel.speechText.collectAsState()
    val matchScore by viewModel.matchScore.collectAsState()
    val highestScore by viewModel.highestScore.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val speechError by viewModel.speechError.collectAsState()

    val remainingSeconds by viewModel.remainingSeconds.collectAsState()
    val isAudioMuted by viewModel.isAudioMuted.collectAsState()

    // Pulse animation for microphone
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Manual simulator text state
    var simulatorText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSlate)
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mock Status Bar / Time Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val nowTime = remember {
                val cal = java.util.Calendar.getInstance()
                val hr = String.format("%02d", cal.get(java.util.Calendar.HOUR_OF_DAY))
                val min = String.format("%02d", cal.get(java.util.Calendar.MINUTE))
                "$hr:$min"
            }
            Text(
                text = nowTime,
                color = TextWhite.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .border(1.dp, TextWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(TextWhite)
                    )
                }
                Text(
                    text = "LTE",
                    color = TextWhite.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Header Section with Alarm Info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                // Pulsing indicator dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(RadiantEmerald)
                        .blur(if (isListening) 4.dp else 0.dp)
                )
                Text(
                    text = "ALARM ACTIVE",
                    color = RadiantEmerald,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp,
                    fontSize = 11.sp
                )
            }
            Text(
                text = "Recite & Rise",
                color = TextWhite,
                fontWeight = FontWeight.Light,
                fontSize = 44.sp,
                letterSpacing = (-1).sp
            )
            Text(
                text = "$activeSurahTransliteration",
                color = SecondarySilver,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Main Content (Verse Card)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(NavyGray)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
                    .padding(28.dp)
            ) {
                // Abstract background glow
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 30.dp, y = (-30).dp)
                        .size(120.dp)
                        .blur(40.dp)
                        .background(RadiantEmerald.copy(alpha = 0.08f))
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Arabic Text
                    Text(
                        text = activeAyahText,
                        color = TextWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        lineHeight = 48.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                            .testTag("active_ayah_arabic")
                    )

                    // Minimalist custom divider
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(1.dp)
                            .background(RadiantEmerald.copy(alpha = 0.3f))
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // English Translation text
                    Text(
                        text = "\"$activeAyahTranslation\"",
                        color = SecondarySilver,
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("active_ayah_translation")
                    )
                }
            }
        }

        // Speech & Listening Info
        AnimatedVisibility(visible = speechText.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "RECOGNIZED SPEECH:",
                    color = RadiantEmerald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = speechText,
                    color = TextWhite,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Safety Mute Timer Banner
        if (isAudioMuted) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(RadiantEmerald.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeOff,
                        contentDescription = "Muted",
                        tint = RadiantEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Recitation Mode: Alarm muted. Complete recitation in ${remainingSeconds}s!",
                        color = RadiantEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Speech Error Snackbar/Text helper
        speechError?.let { err ->
            Text(
                text = err,
                color = Color.Red,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Footer & Controls Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp, top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Accuracy Score Panel
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "RECITATION ACCURACY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SecondarySilver,
                            letterSpacing = 1.sp
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${(highestScore * 100).toInt()}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = RadiantEmerald
                            )
                            Text(
                                text = "%",
                                fontSize = 16.sp,
                                color = RadiantEmerald,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                    Text(
                        text = "Target: 80%",
                        fontSize = 11.sp,
                        color = SecondarySilver,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Modern visual linear progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(NavyGray)
                        .border(1.dp, Color.White.copy(alpha = 0.05f), CircleShape)
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = highestScore.toFloat().coerceIn(0f, 1f))
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(RadiantEmerald, Color(0xFF34D399))
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Microphone Trigger with pulsating layout
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isListening) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(RadiantEmerald.copy(alpha = 0.2f))
                    )
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .scale(pulseScale * 0.85f)
                            .clip(CircleShape)
                            .background(RadiantEmerald.copy(alpha = 0.1f))
                    )
                }

                IconButton(
                    onClick = {
                        if (isListening) {
                            viewModel.stopRecitation()
                        } else {
                            viewModel.startRecitation(context)
                        }
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(RadiantEmerald)
                        .testTag("microphone_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.PlayArrow else Icons.Default.Mic,
                        contentDescription = "Microphone Trigger",
                        tint = DeepSlate,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Text(
                text = if (isListening) "LISTENING FOR RECITATION..." else "TAP MIC TO RECITE AND DISMISS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = SecondarySilver,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Dynamic Simulator Widget for emulator testing
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NavyGray)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "EMULATOR SIMULATION MODE",
                        color = RadiantEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = simulatorText,
                        onValueChange = { simulatorText = it },
                        placeholder = { Text("Type Arabic or English to simulate...", color = SecondarySilver, fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag("simulator_text_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RadiantEmerald,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedContainerColor = DeepSlate,
                            unfocusedContainerColor = DeepSlate,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.submitManualRecitation(simulatorText)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RadiantEmerald,
                                contentColor = TextWhite
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("submit_simulation_button")
                        ) {
                            Text("Submit Match", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                // Instantly matches 100% by submitting the target verse text directly
                                viewModel.submitManualRecitation(activeAyahText)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NavyGray,
                                contentColor = RadiantEmerald
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("bypass_recitation_button")
                        ) {
                            Text("Bypass (100% Match)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
