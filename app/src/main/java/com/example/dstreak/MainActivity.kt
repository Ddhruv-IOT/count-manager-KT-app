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
