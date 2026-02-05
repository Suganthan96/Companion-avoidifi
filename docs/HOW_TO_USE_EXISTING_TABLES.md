# ✅ HOW TO USE EXISTING SUPABASE TABLES

Your app is already configured to insert data into your existing Supabase tables. Here's how everything works:

---

## 📊 Your Existing Tables

### 1. `usage_records` Table
```sql
-- What it stores:
- id (UUID, auto-generated)
- device_id (from your Android device)
- user_id (name you enter on first launch)
- package_name (app package)
- app_name (app display name)
- usage_time (milliseconds)
- first_used, last_used (timestamps)
- timestamp (when data was synced)
- start_period, end_period (sync period)
- created_at (database timestamp)
```

### 2. `daily_usage_summary` Table
```sql
-- What it stores:
- id (UUID, auto-generated)
- device_id (from your Android device)
- user_id (name you enter on first launch)
- date (YYYY-MM-DD format)
- total_screen_time (total milliseconds for the day)
- app_count (number of apps used)
- most_used_app (app with highest usage)
- created_at (database timestamp)
```

---

## 🔧 How Your App Uses These Tables

### Code Flow:
```
App Launch
    ↓
OnboardingActivity (First Time Only)
    ↓ User enters name (e.g., "Suganthan")
    ↓ Saves to SharedPreferences
    ↓
MainActivity
    ↓
Every 15 minutes → WorkManager PeriodicWorkRequest
    ↓
UsageSyncWorker runs
    ↓
1. Fetches usage stats from UsageStatsManager
2. Creates UsageRecord objects with:
   - device_id (from Settings.Secure.ANDROID_ID)
   - user_id (from SharedPreferences - your name)
   - app_name, package_name, usage_time, etc.
3. Calls UsageRepository.uploadUsageData()
    ↓
UsageRepository (your code in UsageRepository.kt)
    ↓
supabase.from("usage_records").insert(usageRecords)
    ↓
Data inserted into Supabase `usage_records` table ✅
```

---

## 🚀 Step-by-Step: How to Use Your Tables

### Step 1: Delete Old Placeholder Data (IMPORTANT)
In Supabase SQL Editor, run:
```sql
DELETE FROM usage_records 
WHERE user_id = 'user_wallet_address_or_ens' 
OR user_id IS NULL;

DELETE FROM daily_usage_summary 
WHERE user_id = 'user_wallet_address_or_ens' 
OR user_id IS NULL;
```

### Step 2: Clear App Data
Your phone needs a fresh start:

**Option A - Via Settings:**
- Settings → Apps → Digital Wellbeing Viewer → Storage → Clear Data

**Option B - Via ADB (Terminal):**
```bash
adb shell pm clear com.example.digitalwellbeingviewer
```

### Step 3: Rebuild App
```bash
cd C:\Users\Sugan\projects\DigitalWellbeingViewer
.\gradlew clean assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Step 4: Open App and Enter Your Name
1. App opens
2. **OnboardingActivity** appears (first time only)
3. Enter your name: `Suganthan` (or whatever you want)
4. Click **Continue**
5. See toast: "Auto-sync enabled for Suganthan ✓"
6. **MainActivity** opens

### Step 5: Wait for First Sync
- First sync happens within 15 minutes
- WorkManager will run UsageSyncWorker
- Your usage data gets uploaded

### Step 6: Verify in Supabase
Go to Supabase Dashboard → Table Editor → Click `usage_records`

You should see:
```
| device_id | user_id   | app_name      | usage_time | timestamp       |
|-----------|-----------|---------------|------------|-----------------|
| a1b2c3d4  | Suganthan | Chrome        | 1800000    | 1707123456789   |
| a1b2c3d4  | Suganthan | Discord       | 900000     | 1707123456789   |
| a1b2c3d4  | Suganthan | WhatsApp      | 600000     | 1707123456789   |
```

---

## 📱 What You'll See in Supabase

### After 1 Sync (15 minutes):
- ~10-30 rows in `usage_records` table
- 1 row in `daily_usage_summary` table
- All with `user_id = "Suganthan"`

### After Multiple Syncs (several hours):
- ~60-200 rows in `usage_records` table (4-8 syncs × 10-30 apps each)
- Multiple rows in `daily_usage_summary` (one per day per device)
- Data steadily accumulating

### After 24 Hours:
- ~1000+ rows in `usage_records`
- Clear pattern of your app usage throughout the day
- Accurate daily summaries

---

## 🔍 How to Query Your Data

### In Supabase SQL Editor:

**Get all your records:**
```sql
SELECT * FROM usage_records 
WHERE user_id = 'Suganthan'
ORDER BY created_at DESC
LIMIT 100;
```

**Get today's usage per app:**
```sql
SELECT 
    app_name,
    SUM(usage_time) as total_ms,
    ROUND(SUM(usage_time) / 1000.0 / 60.0, 2) as total_minutes
FROM usage_records
WHERE user_id = 'Suganthan'
  AND DATE(created_at AT TIME ZONE 'Asia/Kolkata') = CURRENT_DATE
GROUP BY app_name
ORDER BY total_ms DESC;
```

**Get daily summary:**
```sql
SELECT * FROM daily_usage_summary
WHERE user_id = 'Suganthan'
ORDER BY date DESC;
```

**Get last sync time:**
```sql
SELECT 
    MAX(created_at AT TIME ZONE 'Asia/Kolkata') as last_sync,
    COUNT(*) as records_count
FROM usage_records
WHERE user_id = 'Suganthan';
```

---

## 📝 Your Code Does This Automatically

### `UsageRepository.kt` - Handles uploads:
```kotlin
suspend fun uploadUsageData(usageRecords: List<UsageRecord>): Boolean {
    supabase.from("usage_records").insert(usageRecords)  // ← Inserts here
}

suspend fun uploadDailySummary(summary: DailyUsageSummary): Boolean {
    supabase.from("daily_usage_summary").insert(summary)  // ← Inserts here
}
```

### `UsageSyncWorker.kt` - Runs every 15 minutes:
```kotlin
// Fetches your name from SharedPreferences
val userId = inputData.getString("user_id")  // Your entered name

// Queries usage stats
val stats = usageManager.queryUsageStats(...)

// Creates records with your name
val record = UsageRecord(
    device_id = deviceId,
    user_id = userId,  // ← Your actual name!
    ...
)

// Uploads to Supabase
repository.uploadUsageData(usageRecords)  // ← Goes to usage_records table
```

---

## ⚠️ Important Notes

### 1. First Launch is Special
- **First time**: Shows OnboardingActivity (name input)
- **After first time**: Skips to MainActivity (name saved in SharedPreferences)

### 2. Placeholder Data Needs Cleaning
- Old data with `user_id = 'user_wallet_address_or_ens'` should be deleted
- Your new data will have the actual name you enter

### 3. Sync is Automatic
- Happens every 15 minutes in background
- App doesn't need to be open
- Requires internet connection

### 4. Device ID is Unique
- Each device gets its own device_id (Android device ID)
- Same user on different devices = different device_ids

### 5. Multi-Device Support
```
Phone 1: Enters "Suganthan"
  ↓ Syncs with device_id = "phone1_id", user_id = "Suganthan"

Phone 2: Enters "John"  
  ↓ Syncs with device_id = "phone2_id", user_id = "John"

Supabase gets:
- Records with Suganthan's usage (device_id = phone1_id)
- Records with John's usage (device_id = phone2_id)
- Can track per user or per device
```

---

## ✅ Success Checklist

- [ ] Deleted old placeholder data
- [ ] Cleared app data
- [ ] Rebuilt and installed app
- [ ] Opened app and entered your name
- [ ] Waited 15-20 minutes for first sync
- [ ] Checked Supabase and saw records with your actual name
- [ ] Records appear every 15 minutes ✅
- [ ] Multiple apps shown in your data
- [ ] Daily summary table has entries

---

## 🎉 You're Done!

Your app is now syncing real data to your existing Supabase tables with your actual name. Every 15 minutes, new records get added automatically.

**Next Steps:**
1. Build your Next.js frontend to display this data
2. Query the tables via Supabase API
3. Create dashboards, graphs, analytics
4. Add challenges/rewards features

---

## 📞 Common Issues

**Q: No data appearing in Supabase?**
A: 
1. Wait 15-20 minutes for first sync
2. Check app has Usage Access permission (Settings → Apps → Special App Access → Usage Access)
3. Check phone has internet connection
4. Check Supabase credentials in SupabaseClient.kt

**Q: Data appears but user_id is still placeholder?**
A:
1. Rebuild app: `.\gradlew clean assembleDebug`
2. Clear app data: `adb shell pm clear com.example.digitalwellbeingviewer`
3. Uninstall and reinstall
4. OnboardingActivity should appear on fresh start

**Q: Multiple user_ids showing up?**
A: This is expected if you:
- Tested with placeholder data before
- Entered different names on different devices
- Run: `SELECT DISTINCT user_id FROM usage_records;` to see all users

**Q: Times look wrong?**
A: All times are converted to IST (Asia/Kolkata) in the views. Raw tables use milliseconds since epoch.

---

## 🚀 You're Ready!

Your existing Supabase tables are now receiving real user data with actual names. Build on top of this! 🎉
