//package com.example.dstreak
//import android.os.Bundle
//import android.widget.Button
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import java.util.Calendar
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var resetButton: Button
//    private lateinit var currentStreakTextView: TextView
//    private lateinit var longestStreakTextView: TextView
//    private lateinit var previousStreakTextView: TextView
//
//    private var currentStreak = 0
//    private var longestStreak = 0
//    private var previousStreak = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.layout)
//
//        // Initialize views
//        resetButton = findViewById(R.id.resetButton)
//        currentStreakTextView = findViewById(R.id.currentStreak)
//        longestStreakTextView = findViewById(R.id.longestStreak)
//        previousStreakTextView = findViewById(R.id.previousStreak)
//
//        // Load streak data
//        loadStreakData()
//
//        // Reset button functionality
//        resetButton.setOnClickListener {
//            resetStreak()
//        }
//
//        // Check and update streak
//        updateStreakAtMidnight()
//    }
//
//    private fun loadStreakData() {
//        val sharedPreferences = getSharedPreferences("StreakData", MODE_PRIVATE)
//        currentStreak = sharedPreferences.getInt("currentStreak", 0)
//        longestStreak = sharedPreferences.getInt("longestStreak", 0)
//        previousStreak = sharedPreferences.getInt("previousStreak", 0)
//
//        updateUI()
//    }
//
//    private fun saveStreakData() {
//        val sharedPreferences = getSharedPreferences("StreakData", MODE_PRIVATE)
//        with(sharedPreferences.edit()) {
//            putInt("currentStreak", currentStreak)
//            putInt("longestStreak", longestStreak)
//            putInt("previousStreak", previousStreak)
//            apply()
//        }
//    }
//
//    private fun resetStreak() {
//        previousStreak = currentStreak
//        currentStreak = 0
//        saveStreakData()
//        updateUI()
//    }
//
//    private fun updateUI() {
//        currentStreakTextView.text = "Current Streak: $currentStreak"
//        longestStreakTextView.text = "Longest Streak: $longestStreak"
//        previousStreakTextView.text = "Previous Streak: $previousStreak"
//        resetButton.text = "reset"
//    }
//
//    private fun updateStreakAtMidnight() {
//        val calendar = Calendar.getInstance()
//        val hour = calendar.get(Calendar.HOUR_OF_DAY)
//        val minute = calendar.get(Calendar.MINUTE)
//
//        if (hour == 0 && minute == 0) {
//            currentStreak += 1
//            if (currentStreak > longestStreak) {
//                longestStreak = currentStreak
//            }
//            saveStreakData()
//            updateUI()
//        }
//    }
//}
//
////package com.example.dstreak
////
////import android.os.Bundle
////import android.os.Handler
////import android.widget.TextView
////import androidx.appcompat.app.AppCompatActivity
////
////class MainActivity : AppCompatActivity() {
////    private lateinit var streakCountTextView: TextView
////    private var streakCount = 0
////    private val handler = Handler()
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContentView(R.layout.layout)
////
////        // Initialize the TextView
////        streakCountTextView = findViewById(R.id.currentStreak)
////        updateStreakText()
////
////        // Start updating the count every minute
////        handler.post(updateCountRunnable)
////    }
////
////    private val updateCountRunnable = object : Runnable {
////        override fun run() {
////            streakCount++
////            updateStreakText()
////
////            // Schedule the next update after 1 minute (60,000 ms)
////            handler.postDelayed(this, 6000)
////        }
////    }
////
////    private fun updateStreakText() {
////        streakCountTextView.text = "Streak: $streakCount"
////    }
////
////    override fun onDestroy() {
////        super.onDestroy()
////
////        // Remove callbacks to avoid memory leaks
////        handler.removeCallbacks(updateCountRunnable)
////    }
////}


package com.example.dstreak

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var totalCountTextView: TextView
    private lateinit var monthlyCountTextView: TextView
    private lateinit var todaysCountTextView: TextView
    private lateinit var addCountButton: Button
    private lateinit var logTable: LinearLayout

    private var totalCount = 0
    private var monthlyCount = 0
    private var todaysCount = 0
    private val logData = mutableListOf<String>()

    private val sharedPrefs by lazy { getSharedPreferences("AppPrefs", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout)

        // Initialize views
        totalCountTextView = findViewById(R.id.totalCount)
        monthlyCountTextView = findViewById(R.id.monthlyCount)
        todaysCountTextView = findViewById(R.id.todaysCount)
        addCountButton = findViewById(R.id.addCountButton)
        logTable = findViewById(R.id.logTable)

        // Load persistent data
        loadCounts()

        // Button click logic
        addCountButton.setOnClickListener {
            incrementCounts()
        }
    }

    private fun incrementCounts() {
        totalCount++
        monthlyCount++
        todaysCount++

        // Log the entry
        val timestamp = getCurrentTimestamp()
        val entry = "Entry $totalCount: $timestamp"
        logData.add(entry)
        addLogEntryToUI(entry)

        // Update UI
        updateUI()

        // Save persistent data
        saveCounts()
    }

    private fun addLogEntryToUI(entry: String) {
        val textView = TextView(this).apply {
            text = entry
            textSize = 16f
            setTextColor(resources.getColor(android.R.color.white))
            setPadding(8, 8, 8, 8)
        }
        logTable.addView(textView)
    }

    private fun updateUI() {
        totalCountTextView.text = "Total: $totalCount"
        monthlyCountTextView.text = "Monthly: $monthlyCount"
        todaysCountTextView.text = "Today's: $todaysCount"
    }

    private fun saveCounts() {
        val editor = sharedPrefs.edit()
        editor.putInt("totalCount", totalCount)
        editor.putInt("monthlyCount", monthlyCount)
        editor.putInt("todaysCount", todaysCount)
        editor.putStringSet("logData", logData.toSet())
        editor.apply()
    }

    private fun loadCounts() {
        totalCount = sharedPrefs.getInt("totalCount", 0)
        monthlyCount = sharedPrefs.getInt("monthlyCount", 0)
        todaysCount = sharedPrefs.getInt("todaysCount", 0)
        val savedLogs = sharedPrefs.getStringSet("logData", emptySet()) ?: emptySet()
        logData.addAll(savedLogs)

        // Populate log table
        logData.forEach { addLogEntryToUI(it) }

        // Update UI
        updateUI()
    }

    private fun getCurrentTimestamp(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(Date())
    }
}
