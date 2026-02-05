# ✅ Testing & Verification Guide

## 🧪 Test the Name Input Feature

### Test 1: First Launch (Clean Install)
1. Uninstall app (if installed)
2. Build and install fresh APK
3. **Expected:** OnboardingActivity appears
4. **Action:** Enter name (e.g., "TestUser")
5. **Expected:** Toast shows "Welcome, TestUser!"
6. **Expected:** MainActivity loads

### Test 2: Second Launch
1. Close app
2. Reopen app
3. **Expected:** MainActivity loads directly (NO name screen)
4. **Verify:** Name screen skipped ✅

### Test 3: Check Supabase
1. Go to Supabase Dashboard
2. Open `usage_records` table
3. Find recent records
4. **Expected:** `user_id` column shows "TestUser"
5. **Verify:** Name appears in database ✅

---

## 🔍 Verification Queries

### Query 1: Verify User Name in Database
```sql
SELECT DISTINCT user_id 
FROM usage_records
ORDER BY user_id;
```

**Expected Output:**
```
user_id
---------
TestUser
```

---

### Query 2: Records by User
```sql
SELECT 
    user_id,
    COUNT(*) as total_records,
    MAX(created_at AT TIME ZONE 'Asia/Kolkata') as last_sync
FROM usage_records
GROUP BY user_id
ORDER BY COUNT(*) DESC;
```

**Expected Output:**
```
user_id  | total_records | last_sync
---------|---------------|---------------------
TestUser | 45            | 2026-02-05 10:22:00
```

---

### Query 3: Recent Records with User Name
```sql
SELECT 
    user_id,
    app_name,
    ROUND(usage_time / 60000.0, 1) as minutes,
    created_at AT TIME ZONE 'Asia/Kolkata' as synced_at
FROM usage_records
WHERE user_id = 'TestUser'
ORDER BY created_at DESC
LIMIT 10;
```

**Expected Output:**
```
user_id  | app_name              | minutes | synced_at
---------|----------------------|---------|---------------------
TestUser | Discord              | 45.2    | 2026-02-05 10:22:00
TestUser | Chrome               | 23.5    | 2026-02-05 10:21:00
TestUser | WhatsApp             | 15.3    | 2026-02-05 10:20:00
...
```

---

## 📱 Android Logcat Verification

### Check Logs During First Launch:
```bash
adb logcat | grep -E "UsageSyncWorker|user_id"
```

**Expected Logs:**
```
D/UsageSyncWorker: Starting background sync...
D/UsageSyncWorker: Found 5 apps to upload
D/UsageSyncWorker: Successfully uploaded to Supabase!
```

---

## 🎯 Multi-Device Testing

### Setup Multiple Devices:
1. **Device 1:** Install app, enter name "User1"
2. **Device 2:** Install app, enter name "User2"

### Wait 15 minutes for sync

### Query in Supabase:
```sql
SELECT 
    user_id,
    device_id,
    COUNT(*) as syncs
FROM usage_records
GROUP BY user_id, device_id
ORDER BY user_id;
```

**Expected Output:**
```
user_id | device_id            | syncs
--------|----------------------|-------
User1   | device_id_phone1     | 45
User2   | device_id_phone2     | 38
```

---

## 🔄 Sync Verification Timeline

### Minute 0: Install & Open App
- OnboardingActivity appears
- User enters name
- MainActivity loads
- WorkManager starts

### Minute 1-5: First Sync
- UsageSyncWorker runs
- Data uploaded to Supabase
- Check database for records

### Minute 20: Second Sync
- Another batch of records appears
- Same `user_id` as before

### Minute 35: Third Sync
- More records added
- Pattern continues

---

## ✅ Final Verification Checklist

- [ ] **Onboarding Screen Appears** on first launch
- [ ] **Name Input Works** - can type name
- [ ] **Continue Button Works** - proceeds to MainActivity
- [ ] **Name Saved** - second launch skips screen
- [ ] **Supabase Integration** - `user_id` shows entered name
- [ ] **Auto-Sync Working** - records appear every 15 min
- [ ] **User Name in Records** - all records have correct user_id
- [ ] **Multiple Names** - can test with different names

---

## 🐛 Troubleshooting

### Issue: Name Screen Appears Every Launch
**Solution:** Check if `onboarding_complete` is being saved
```kotlin
// Debug in MainActivity
val isComplete = OnboardingActivity.isOnboardingComplete(this)
Log.d("DEBUG", "Onboarding complete: $isComplete")
```

### Issue: Name Not Appearing in Supabase
**Solution:** Check if user_id is being passed correctly
```kotlin
// In MainActivity.kt
val userName = OnboardingActivity.getUserName(this)
Log.d("DEBUG", "User name: $userName")
```

### Issue: App Crashes on Name Entry
**Solution:** Check ViewBinding in OnboardingActivity
```kotlin
// Ensure binding is initialized
binding = ActivityOnboardingBinding.inflate(layoutInflater)
setContentView(binding.root)
```

---

## 📊 Expected Data Flow

```
Device
  ↓
OnboardingActivity: User enters "Suganthan"
  ↓
SharedPreferences: Saves {user_name: "Suganthan", onboarding_complete: true}
  ↓
MainActivity: Loads (onboarding check passes)
  ↓
WorkManager: Schedules sync with user_id = "Suganthan"
  ↓
Every 15 minutes:
  UsageSyncWorker runs
  ↓
  Fetches usage data
  ↓
  Creates records with user_id = "Suganthan"
  ↓
  Uploads to Supabase usage_records table
  ↓
Supabase:
  Records appear with user_id = "Suganthan"
```

---

## 🎉 Success Indicators

### You'll Know It's Working When:
1. ✅ First launch shows name screen
2. ✅ Second launch skips name screen
3. ✅ Supabase shows records with `user_id` = your entered name
4. ✅ New records appear every 15 minutes with same user_id
5. ✅ Multiple devices → different user_ids in database

---

## 📝 Sample Test Data

### After 1 Hour of Testing:
```
Supabase usage_records table:
- Total records: ~60-120
- user_id: "TestUser"
- Multiple apps: Discord, Chrome, WhatsApp, etc.
- Evenly spaced: 15-min intervals

Supabase daily_usage_summary table:
- Records: 1 per day per device
- total_screen_time: Sum of all usage
- most_used_app: App with highest usage
```

---

## 🚀 Next Steps

After verifying everything works:

1. **Deploy to Real Device** - Install on actual phone
2. **Test Multi-User** - Get friends/family to test
3. **Monitor Supabase** - Check data quality
4. **Build Frontend** - Create Next.js dashboard to display user data
5. **Scale Up** - Deploy to production

---

## 📞 Quick Commands

### Build & Install:
```bash
cd C:\Users\Sugan\projects\DigitalWellbeingViewer
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### View Logs:
```bash
adb logcat | grep UsageSyncWorker
```

### Clear App Data:
```bash
adb shell pm clear com.example.digitalwellbeingviewer
```

### Check Database Size:
```sql
SELECT 
    COUNT(*) as records,
    PG_SIZE_PRETTY(SUM(pg_column_size(row(usage_records.*))))
FROM usage_records;
```
