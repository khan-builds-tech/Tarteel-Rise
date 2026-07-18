package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Surah
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmCreateScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val surahs by viewModel.surahs.collectAsState()

    // TimePicker State (using basic state or M3 TimePickerState)
    val timeState = rememberTimePickerState(initialHour = 6, initialMinute = 0, is24Hour = false)

    // Surah Selection State
    var selectedSurahId by remember { mutableStateOf(1) }
    val selectedSurah = surahs.firstOrNull { it.id == selectedSurahId }

    // Ayah Selection State
    var selectedAyahNumber by remember { mutableStateOf(1) }

    // If surah changes, reset selected ayah to 1
    LaunchedEffect(selectedSurahId) {
        selectedAyahNumber = 1
    }

    // Search and Dropdown dialogs
    var isSurahDialogVisible by remember { mutableStateOf(false) }
    var isAyahDropdownExpanded by remember { mutableStateOf(false) }

    // Selected Days State
    val daysList = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val selectedDays = remember { mutableStateListOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DeepSlate,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "NEW ALARM",
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepSlate
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                // Time Picker Card
                item {
                    Text(
                        text = "SELECT TIME",
                        color = RadiantEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(NavyGray)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TimePicker(
                            state = timeState,
                            colors = TimePickerDefaults.colors(
                                clockDialColor = DeepSlate,
                                clockDialSelectedContentColor = TextWhite,
                                clockDialUnselectedContentColor = SecondarySilver,
                                selectorColor = RadiantEmerald,
                                periodSelectorBorderColor = RadiantEmerald,
                                periodSelectorSelectedContainerColor = RadiantEmerald,
                                periodSelectorUnselectedContainerColor = DeepSlate,
                                periodSelectorSelectedContentColor = TextWhite,
                                periodSelectorUnselectedContentColor = SecondarySilver,
                                timeSelectorSelectedContainerColor = RadiantEmerald,
                                timeSelectorUnselectedContainerColor = DeepSlate,
                                timeSelectorSelectedContentColor = TextWhite,
                                timeSelectorUnselectedContentColor = SecondarySilver
                            )
                        )
                    }
                }

                // Days Selection Card
                item {
                    Text(
                        text = "REPEAT DAYS",
                        color = RadiantEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(NavyGray)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        daysList.forEach { day ->
                            val isSelected = selectedDays.contains(day)
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) RadiantEmerald else DeepSlate)
                                    .clickable {
                                        if (isSelected) {
                                            selectedDays.remove(day)
                                        } else {
                                            selectedDays.add(day)
                                        }
                                    }
                                    .testTag("day_button_$day"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.first().toString(),
                                    color = if (isSelected) TextWhite else SecondarySilver,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // Surah Selector Card (Searchable Dialog trigger)
                item {
                    Text(
                        text = "CHOOSE SURAH",
                        color = RadiantEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(NavyGray)
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                            .clickable { isSurahDialogVisible = true }
                            .padding(16.dp)
                            .testTag("surah_dropdown_trigger")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                if (selectedSurah != null) {
                                    Text(
                                        text = "${selectedSurah.id}. ${selectedSurah.transliteration} (${selectedSurah.name})",
                                        color = TextWhite,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "${selectedSurah.translation} • ${selectedSurah.totalVerses} Ayahs",
                                        color = SecondarySilver,
                                        fontSize = 13.sp
                                    )
                                } else {
                                    Text(
                                        text = "Select Surah",
                                        color = SecondarySilver,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = RadiantEmerald
                            )
                        }
                    }
                }

                // Ayah Selector Card
                item {
                    Text(
                        text = "CHOOSE AYAH",
                        color = RadiantEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(NavyGray)
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                            .clickable { isAyahDropdownExpanded = true }
                            .padding(16.dp)
                            .testTag("ayah_dropdown_trigger")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Ayah $selectedAyahNumber",
                                    color = TextWhite,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                                val currentAyahText = selectedSurah?.verses?.firstOrNull { it.id == selectedAyahNumber }?.translation ?: ""
                                if (currentAyahText.isNotEmpty()) {
                                    Text(
                                        text = if (currentAyahText.length > 50) currentAyahText.take(50) + "..." else currentAyahText,
                                        color = SecondarySilver,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = RadiantEmerald
                            )

                            DropdownMenu(
                                expanded = isAyahDropdownExpanded,
                                onDismissRequest = { isAyahDropdownExpanded = false },
                                modifier = Modifier
                                    .background(NavyGray)
                                    .width(200.dp)
                            ) {
                                val maxVerses = selectedSurah?.totalVerses ?: 7
                                (1..maxVerses).forEach { num ->
                                    DropdownMenuItem(
                                        text = { Text("Ayah $num", color = TextWhite) },
                                        onClick = {
                                            selectedAyahNumber = num
                                            isAyahDropdownExpanded = false
                                        },
                                        modifier = Modifier.testTag("ayah_option_$num")
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Save Alarm Button
            Button(
                onClick = {
                    viewModel.addAlarm(
                        context = context,
                        hour = timeState.hour,
                        minute = timeState.minute,
                        surahId = selectedSurahId,
                        ayahNumber = selectedAyahNumber,
                        days = selectedDays.toList()
                    )
                    ScaffoldMessenger.showSnackbar(context, "Alarm created successfully!")
                    onNavigateBack()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = RadiantEmerald,
                    contentColor = TextWhite
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(56.dp)
                    .testTag("save_alarm_button")
            ) {
                Text(
                    text = "SAVE ALARM",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }

    // Searchable Surah Dialog
    if (isSurahDialogVisible) {
        SurahSearchDialog(
            surahs = surahs,
            onSurahSelected = { surah ->
                selectedSurahId = surah.id
                isSurahDialogVisible = false
            },
            onDismiss = { isSurahDialogVisible = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahSearchDialog(
    surahs: List<Surah>,
    onSurahSelected: (Surah) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredSurahs = remember(searchQuery, surahs) {
        surahs.filter {
            it.transliteration.contains(searchQuery, ignoreCase = true) ||
                    it.translation.contains(searchQuery, ignoreCase = true) ||
                    it.name.contains(searchQuery) ||
                    it.id.toString() == searchQuery
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(28.dp),
            color = NavyGray
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Select Surah",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search surah...", color = SecondarySilver) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .testTag("surah_search_input"),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = RadiantEmerald)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RadiantEmerald,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = DeepSlate,
                        unfocusedContainerColor = DeepSlate,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredSurahs) { surah ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DeepSlate)
                                .clickable { onSurahSelected(surah) }
                                .padding(16.dp)
                                .testTag("surah_option_${surah.id}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${surah.id}. ${surah.transliteration}",
                                        color = TextWhite,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "${surah.translation} • ${surah.totalVerses} Verses",
                                        color = SecondarySilver,
                                        fontSize = 12.sp
                                    )
                                }
                                Text(
                                    text = surah.name,
                                    color = RadiantEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
