# 🚀 DIRECT COMMANDS TO USE YOUR EXISTING TABLES

## Command 1: Clean Old Data in Supabase
**Copy this entire block and paste into Supabase SQL Editor:**

```sql
DELETE FROM usage_records 
WHERE user_id = 'user_wallet_address_or_ens' 
   OR user_id IS NULL 
   OR user_id = '';

DELETE FROM daily_usage_summary
WHERE user_id = 'user_wallet_address_or_ens'
   OR user_id IS NULL
   OR user_id = '';

-- Verify
SELECT 'usage_records' as table_name, COUNT(*) as records FROM usage_records
UNION ALL
SELECT 'daily_usage_summary' as table_name, COUNT(*) as records FROM daily_usage_summary;
```

**Steps:**
1. Go to: https://supabase.com/dashboard/project/cjkkzrtuoupbdclolhpu/editor
2. Click **SQL Editor** (left sidebar)
3. Click **+ New Query**
4. Paste the SQL above
5. Click **RUN** (blue button)
6. Wait for ✅ success message

---

## Command 2: Clear App and Rebuild
**Copy this and run in PowerShell in your project directory:**

```powershell
# Clear app data
adb shell pm clear com.example.digitalwellbeingviewer

# Navigate to project
cd C:\Users\Sugan\projects\DigitalWellbeingViewer

# Clean and build
.\gradlew clean assembleDebug

# Install
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

**Steps:**
1. Open PowerShell
2. Copy the entire block above
3. Paste into PowerShell
4. Press Enter
5. Wait for completion
6. You'll see: `com.example.digitalwellbeingviewer` installed

---

## Command 3: After Installation, Verify Data

**On your phone:**
1. Tap Digital Wellbeing Viewer app
2. You should see **OnboardingActivity** (name input screen)
3. Enter your name (e.g., `Suganthan`)
4. Tap **Continue**
5. See toast: "Auto-sync enabled for Suganthan ✓"
6. App shows main screen
7. **Wait 15-20 minutes** ⏱️

---

## Command 4: Check Supabase After Sync

**Query in Supabase SQL Editor:**

```sql
-- See all your records
SELECT 
    user_id,
    app_name,
    ROUND(usage_time / 1000.0 / 60.0, 2) as usage_minutes,
    created_at AT TIME ZONE 'Asia/Kolkata' as created_time
FROM usage_records
ORDER BY created_at DESC
LIMIT 50;
```

**Steps:**
1. Go to Supabase Dashboard
2. SQL Editor
3. Click **+ New Query**
4. Paste the query above
5. Click **RUN**
6. You should see rows with your actual name! ✅

---

## Command 5: View Summary Data

```sql
-- See daily totals
SELECT 
    user_id,
    date,
    ROUND(total_screen_time / 1000.0 / 60.0 / 60.0, 2) as total_hours,
    app_count,
    most_used_app,
    created_at AT TIME ZONE 'Asia/Kolkata' as created_time
FROM daily_usage_summary
ORDER BY date DESC;
```

---

## Command 6: Monitor Sync Progress

**Check how many records you have:**

```sql
SELECT 
    COUNT(*) as total_records,
    COUNT(DISTINCT device_id) as devices,
    COUNT(DISTINCT user_id) as users,
    MIN(created_at AT TIME ZONE 'Asia/Kolkata') as first_record,
    MAX(created_at AT TIME ZONE 'Asia/Kolkata') as last_record
FROM usage_records;
```

Run this every 15 minutes to see data accumulate! ✨

---

## Command 7: See What Apps You Use Most

```sql
SELECT 
    app_name,
    COUNT(*) as sync_count,
    SUM(usage_time) as total_ms,
    ROUND(SUM(usage_time) / 1000.0 / 60.0, 2) as total_minutes,
    ROUND(AVG(usage_time) / 1000.0 / 60.0, 2) as avg_minutes_per_sync
FROM usage_records
WHERE user_id = 'Suganthan'
GROUP BY app_name
ORDER BY total_ms DESC
LIMIT 20;
```

**Replace 'Suganthan' with your entered name!**

---

## Shortcut: Full Setup in One Go

If you want the **complete flow** at once:

**Terminal (PowerShell):**
```powershell
# 1. Clear app
adb shell pm clear com.example.digitalwellbeingviewer

# 2. Build
cd C:\Users\Sugan\projects\DigitalWellbeingViewer
.\gradlew clean assembleDebug

# 3. Install
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 4. Done!
Write-Host "✅ App installed! Open it on phone and enter your name."
```

**Supabase SQL (one after another):**
```sql
-- First query: Clean old data
DELETE FROM usage_records WHERE user_id IS NULL OR user_id = '' OR user_id = 'user_wallet_address_or_ens';
DELETE FROM daily_usage_summary WHERE user_id IS NULL OR user_id = '' OR user_id = 'user_wallet_address_or_ens';

-- Second query: Check your data (run this after 15 minutes)
SELECT * FROM usage_records WHERE user_id = 'YOUR_NAME_HERE' ORDER BY created_at DESC LIMIT 20;
```

---

## 📋 Complete Step-by-Step Execution

### STEP 1: Clean Database (5 minutes)
```
1. Open browser → https://supabase.com/dashboard/project/cjkkzrtuoupbdclolhpu/editor
2. Click SQL Editor
3. Run: DELETE FROM usage_records WHERE user_id IS NULL OR user_id = 'user_wallet_address_or_ens';
4. Run: DELETE FROM daily_usage_summary WHERE user_id IS NULL OR user_id = 'user_wallet_address_or_ens';
5. Wait for ✅ success
```

### STEP 2: Rebuild App (5 minutes)
```powershell
cd C:\Users\Sugan\projects\DigitalWellbeingViewer
.\gradlew clean assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### STEP 3: Use App (2 minutes)
```
1. Tap app on phone
2. See OnboardingActivity
3. Enter name: Suganthan
4. Click Continue
5. See toast: "Auto-sync enabled for Suganthan ✓"
```

### STEP 4: Wait (15 minutes)
```
⏱️ Wait 15-20 minutes for first sync
```

### STEP 5: Verify (2 minutes)
```sql
-- In Supabase SQL Editor
SELECT * FROM usage_records 
WHERE user_id = 'Suganthan' 
ORDER BY created_at DESC 
LIMIT 20;
```

**Total Time: ~30 minutes** ✨

---

## ✅ Success Indicators

**✓ Terminal output shows:**
```
Installing com.example.digitalwellbeingviewer...
Success!
```

**✓ Phone shows:**
```
OnboardingActivity (first time only)
Name input
Toast: "Auto-sync enabled for Suganthan ✓"
MainActivity loads
```

**✓ Supabase shows:**
```
usage_records table has rows
user_id column shows "Suganthan" (your name)
app_name shows real apps (Chrome, Discord, etc.)
usage_time shows real milliseconds
```

---

## 🎉 You're Done!

Your existing Supabase tables now have:
✅ Real user data  
✅ Actual user names  
✅ Automatic syncs every 15 minutes  
✅ Clean database  
✅ Ready for next steps  

**Next:** Build your Next.js frontend to display this data! 🚀
