package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Alarm
import com.example.ui.theme.DeepSlate
import com.example.ui.theme.NavyGray
import com.example.ui.theme.RadiantEmerald
import com.example.ui.theme.SecondarySilver
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.MainViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val alarms by viewModel.alarms.collectAsState()
    val streakData by viewModel.streak.collectAsState()
    val surahs by viewModel.surahs.collectAsState()

    val currentStreak = streakData?.currentStreak ?: 0

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DeepSlate,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "TARTEEL RISE",
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        letterSpacing = 2.sp,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DeepSlate
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = RadiantEmerald,
                contentColor = TextWhite,
                shape = CircleShape,
                modifier = Modifier
                    .padding(16.dp)
                    .testTag("add_alarm_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Alarm",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Energetic Streak Badge Layout
            StreakBadge(streakCount = currentStreak)

            Spacer(modifier = Modifier.height(20.dp))

            // Test Diagnostic Button
            Button(
                onClick = {
                    // Schedules a diagnostic alarm for exactly 5 seconds from now
                    val now = Calendar.getInstance()
                    val targetHour = now.get(Calendar.HOUR_OF_DAY)
                    val targetMinute = now.get(Calendar.MINUTE)
                    val targetSecond = now.get(Calendar.SECOND) + 4
                    
                    val adjustedMinute = if (targetSecond >= 60) targetMinute + 1 else targetMinute
                    val adjustedHour = if (adjustedMinute >= 60) (targetHour + 1) % 24 else targetHour
                    val finalMinute = adjustedMinute % 60
                    
                    val diagAlarm = Alarm(
                        id = 9999, // Specific diagnostic id
                        hour = adjustedHour,
                        minute = finalMinute,
                        isActive = true,
                        daysSelected = "Mon,Tue,Wed,Thu,Fri,Sat,Sun",
                        surahId = 112, // Surah Al-Ikhlas
                        ayahNumber = 1 // Ayah 1 (Qul Huwa Allahu Ahad)
                    )
                    
                    viewModel.addAlarm(
                        context = context,
                        hour = adjustedHour,
                        minute = finalMinute,
                        surahId = 112,
                        ayahNumber = 1,
                        days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    )
                    
                    // Show confirmation
                    ScaffoldMessenger.showSnackbar(context, "Diagnostic alarm scheduled in 4 seconds!")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavyGray,
                    contentColor = RadiantEmerald
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("test_trigger_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = "Test Trigger",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TEST ACTIVE ALARM TRIGGER",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Alarm List Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Alarms",
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Text(
                    text = "${alarms.size} total",
                    color = SecondarySilver,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (alarms.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AlarmOff,
                            contentDescription = "No Alarms",
                            tint = SecondarySilver,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No alarms set",
                            color = TextWhite,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap the + button below to create your first morning recitation alarm.",
                            color = SecondarySilver,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(alarms) { alarm ->
                        AlarmItemCard(
                            alarm = alarm,
                            surahName = surahs.firstOrNull { it.id == alarm.surahId }?.transliteration ?: "Surah",
                            onToggle = { viewModel.toggleAlarmActive(context, alarm) },
                            onDelete = { viewModel.deleteAlarm(context, alarm) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StreakBadge(streakCount: Int) {
    val displayDays = if (streakCount == 1) "Day" else "Days"
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        RadiantEmerald.copy(alpha = 0.15f),
                        RadiantEmerald.copy(alpha = 0.05f)
                    )
                )
            )
            .clickable { }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circle Icon Accent
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(RadiantEmerald.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = "Streak",
                    tint = RadiantEmerald,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "MORNING STREAK",
                    color = RadiantEmerald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$streakCount ",
                        color = TextWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp
                    )
                    Text(
                        text = displayDays,
                        color = SecondarySilver,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Keep rising!",
                color = RadiantEmerald,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun AlarmItemCard(
    alarm: Alarm,
    surahName: String,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(NavyGray)
            .padding(16.dp)
            .testTag("alarm_item_${alarm.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alarm.formattedTime,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Verse",
                        tint = RadiantEmerald,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$surahName — Ayah ${alarm.ayahNumber}",
                        color = SecondarySilver,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (alarm.daysSelected.isEmpty()) "Once" else alarm.daysSelected.replace(",", ", "),
                    color = SecondarySilver.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Delete Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_alarm_button_${alarm.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Alarm",
                        tint = Color.Red.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Toggle Switch
                Switch(
                    checked = alarm.isActive,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = RadiantEmerald,
                        checkedTrackColor = RadiantEmerald.copy(alpha = 0.4f),
                        uncheckedThumbColor = SecondarySilver,
                        uncheckedTrackColor = NavyGray.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.testTag("alarm_toggle_switch_${alarm.id}")
                )
            }
        }
    }
}

object ScaffoldMessenger {
    fun showSnackbar(context: Context, message: String) {
        // Simple snackbar wrapper or fallback Toast
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}
