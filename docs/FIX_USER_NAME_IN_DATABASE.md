# 🔧 FIX: User Name Not Appearing in Database

## Problem
You see `user_wallet_address_or_ens` in the `user_id` column instead of your actual name.

## Why This Happened
1. Old app data was synced before the name input feature was added
2. New app code is correct, but old records exist with placeholder value
3. WorkManager cached old work parameters

## Solution: 3-Step Fix

### Step 1: Clean Database
Run this in **Supabase SQL Editor**:

```sql
DELETE FROM usage_records 
WHERE user_id = 'user_wallet_address_or_ens';

DELETE FROM daily_usage_summary 
WHERE user_id = 'user_wallet_address_or_ens';
```

This removes all old placeholder data. ✅

### Step 2: Clear App Data
On your phone:
```
Settings → Apps → Digital Wellbeing Viewer → Storage → Clear Data
```

Or use ADB:
```bash
adb shell pm clear com.example.digitalwellbeingviewer
```

This clears the cached SharedPreferences. ✅

### Step 3: Reinstall and Test

1. Uninstall app from phone:
```bash
adb uninstall com.example.digitalwellbeingviewer
```

2. Build fresh APK:
```bash
cd C:\Users\Sugan\projects\DigitalWellbeingViewer
.\gradlew clean assembleDebug
```

3. Install:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

4. Open app:
   - OnboardingActivity appears
   - Enter your actual name (e.g., "Suganthan")
   - Click "Continue"
   - Check toast: "Auto-sync enabled for Suganthan ✓"

5. Wait 15-20 minutes for first sync

6. Check Supabase:
```sql
SELECT DISTINCT user_id FROM usage_records;
```

**Expected Result:** Your name appears! 🎉

---

## Verification Steps

### After First Sync:
```sql
-- Should see your name
SELECT user_id, COUNT(*) as records 
FROM usage_records 
GROUP BY user_id;

-- Result should be:
-- user_id  | records
-- ---------|--------
-- Suganthan| 25
```

### Check Logs:
```bash
adb logcat | grep UsageSyncWorker
```

Should show:
```
D/MainActivity: Scheduling sync with user_id: Suganthan
D/UsageSyncWorker: Starting background sync... user_id: Suganthan
D/UsageSyncWorker: Found 8 apps to upload
D/UsageSyncWorker: Successfully uploaded to Supabase!
```

---

## Code Changes Made

### MainActivity.kt:
```kotlin
private fun scheduleUsageSync() {
    val userName = OnboardingActivity.getUserName(this)
    android.util.Log.d("MainActivity", "Scheduling sync with user_id: $userName")
    
    // ... rest of code uses userName ...
}
```

### UsageSyncWorker.kt:
```kotlin
override suspend fun doWork(): Result {
    val userId = inputData.getString("user_id")
    android.util.Log.d("UsageSyncWorker", "Starting background sync... user_id: $userId")
    
    // ... rest of code uses userId ...
}
```

---

## Complete Checklist

- [ ] Delete old placeholder data from Supabase
- [ ] Clear app data on phone
- [ ] Uninstall app
- [ ] Build fresh APK: `./gradlew clean assembleDebug`
- [ ] Install clean APK: `adb install -r ...apk`
- [ ] Open app
- [ ] Enter your name
- [ ] See confirmation toast
- [ ] Wait 15-20 minutes
- [ ] Check Supabase - see your name in user_id ✅

---

## What Will Happen Next

### Every 15 Minutes:
1. UsageSyncWorker runs
2. Gets user name from SharedPreferences: "Suganthan"
3. Logs: "user_id: Suganthan"
4. Creates records with `user_id = "Suganthan"`
5. Uploads to Supabase
6. Your Supabase table now shows **your actual name** instead of placeholder

### Results:
```
usage_records table:
- user_id: "Suganthan" (instead of "user_wallet_address_or_ens")
- app_name: Discord, Chrome, WhatsApp, etc.
- usage_time: actual values
- created_at: current timestamp
```

---

## Why This Works

**Before Fix:**
- App code had placeholder: `"user_wallet_address_or_ens"`
- Data synced with placeholder
- Supabase showed placeholder

**After Fix:**
- App code reads actual name from SharedPreferences
- Passes to WorkManager
- WorkManager passes to UsageSyncWorker
- Worker uses it in every record
- Supabase shows **your actual name** ✅

---

## If Still Not Working

### Check 1: SharedPreferences Saved?
```kotlin
val name = OnboardingActivity.getUserName(this)
Log.d("DEBUG", "Saved name: $name")
```

### Check 2: WorkManager Updated?
```bash
adb shell dumpsys jobscheduler | grep "usage_sync"
```

### Check 3: Supabase Receiving?
```sql
SELECT MAX(created_at), COUNT(*) 
FROM usage_records;
```

Check if new records appear every 15 minutes.

---

## Support

If after all steps your name still doesn't appear:
1. Share logs: `adb logcat | grep UsageSyncWorker`
2. Check Supabase: `SELECT * FROM usage_records LIMIT 1;`
3. Verify permissions: Settings → Usage Access → App enabled?

---

## TL;DR

```bash
# 1. Clean database
# Run in Supabase SQL Editor:
DELETE FROM usage_records WHERE user_id = 'user_wallet_address_or_ens';

# 2. Clear app
adb shell pm clear com.example.digitalwellbeingviewer

# 3. Rebuild
./gradlew clean assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 4. Open app, enter name, wait 15 min
# 5. Check Supabase - your name appears! ✅
```
