package com.example.digitalwellbeingviewer

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.digitalwellbeingviewer.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var usageManager: UsageStatsManager
    private lateinit var adapter: UsageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usageManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        // Setup RecyclerView
        adapter = UsageAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.btnGrantAccess.setOnClickListener {
            if (!hasUsageStatsPermission()) {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            } else {
                loadUsageData()
            }
        }

        binding.btnRefresh.setOnClickListener {
            if (hasUsageStatsPermission()) {
                loadUsageData()
            } else {
                showPermissionMessage()
            }
        }

        // Radio button listeners for time range
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (hasUsageStatsPermission()) {
                loadUsageData()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Check permission and load data when returning from settings
        if (hasUsageStatsPermission()) {
            binding.permissionMessage.visibility = View.GONE
            binding.btnGrantAccess.visibility = View.GONE
            binding.contentLayout.visibility = View.VISIBLE
            loadUsageData()
        } else {
            showPermissionMessage()
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun showPermissionMessage() {
        binding.permissionMessage.visibility = View.VISIBLE
        binding.btnGrantAccess.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE
    }

    private fun loadUsageData() {
        binding.progressBar.visibility = View.VISIBLE
        
        // Get selected time range
        val (start, end) = getSelectedTimeRange()
        
        // Query usage stats
        val stats: List<UsageStats> = usageManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, start, end
        ) ?: emptyList()

        // Aggregate usage by package with detailed info
        val usageMap = mutableMapOf<String, UsageData>()
        for (stat in stats) {
            val time = stat.totalTimeInForeground
            if (time > 0) {
                val existing = usageMap[stat.packageName]
                if (existing != null) {
                    usageMap[stat.packageName] = UsageData(
                        totalTime = existing.totalTime + time,
                        lastUsed = maxOf(existing.lastUsed, stat.lastTimeUsed),
                        firstUsed = minOf(existing.firstUsed, stat.firstTimeStamp)
                    )
                } else {
                    usageMap[stat.packageName] = UsageData(
                        totalTime = time,
                        lastUsed = stat.lastTimeUsed,
                        firstUsed = stat.firstTimeStamp
                    )
                }
            }
        }

        // Convert to list of UsageItem with app names
        val usageItems = usageMap.map { (packageName, data) ->
            UsageItem(
                packageName = packageName,
                appName = getAppName(packageName),
                usageTime = data.totalTime,
                lastUsedTime = data.lastUsed,
                firstUsedTime = data.firstUsed
            )
        }.sortedByDescending { it.usageTime }

        // Update UI
        binding.progressBar.visibility = View.GONE
        adapter.submitList(usageItems)
        
        val totalTime = usageItems.sumOf { it.usageTime }
        binding.totalUsage.text = "Total screen time: ${formatDuration(totalTime)}"
        
        if (usageItems.isEmpty()) {
            binding.emptyMessage.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyMessage.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun getSelectedTimeRange(): Pair<Long, Long> {
        val end = System.currentTimeMillis()
        val start = when (binding.radioGroup.checkedRadioButtonId) {
            R.id.radio1Hour -> end - TimeUnit.HOURS.toMillis(1)
            R.id.radio24Hours -> end - TimeUnit.HOURS.toMillis(24)
            R.id.radio7Days -> end - TimeUnit.DAYS.toMillis(7)
            R.id.radio30Days -> end - TimeUnit.DAYS.toMillis(30)
            else -> end - TimeUnit.HOURS.toMillis(24) // default 24 hours
        }
        return Pair(start, end)
    }

    private fun getAppName(packageName: String): String {
        return try {
            val appInfo: ApplicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }
    }

    private fun formatDuration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        
        return when {
            hours > 0 -> String.format("%dh %dm", hours, minutes)
            minutes > 0 -> String.format("%dm %ds", minutes, seconds)
            else -> String.format("%ds", seconds)
        }
    }
}

data class UsageItem(
    val packageName: String,
    val appName: String,
    val usageTime: Long,
    val lastUsedTime: Long = 0L,
    val firstUsedTime: Long = 0L
)

data class UsageData(
    val totalTime: Long,
    val lastUsed: Long,
    val firstUsed: Long
)
