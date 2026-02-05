# ⚡ QUICK START: Use Existing Supabase Tables

## 🎯 What to Do RIGHT NOW

### Step 1: Clean Database (Supabase Dashboard)
Go to: https://supabase.com/dashboard/project/cjkkzrtuoupbdclolhpu/editor/cjkkzrtuoupbdclolhpu

1. Click **SQL Editor** (left sidebar)
2. Click **+ New Query**
3. Copy & Paste this:

```sql
DELETE FROM usage_records 
WHERE user_id = 'user_wallet_address_or_ens' OR user_id IS NULL;

DELETE FROM daily_usage_summary 
WHERE user_id = 'user_wallet_address_or_ens' OR user_id IS NULL;
```

4. Click **RUN** (blue button)
5. Wait for success ✅

---

### Step 2: Clear App & Reinstall
Open PowerShell and run:

```powershell
adb shell pm clear com.example.digitalwellbeingviewer
cd C:\Users\Sugan\projects\DigitalWellbeingViewer
.\gradlew clean assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

### Step 3: Open App on Phone
1. Tap the app icon
2. **OnboardingActivity** appears (this is FIRST TIME ONLY)
3. Enter your name: `Suganthan` (or anything you want)
4. Tap **Continue**
5. See toast: "Auto-sync enabled for Suganthan ✓"

---

### Step 4: Wait 15 Minutes
Just wait. WorkManager runs in background.

---

### Step 5: Check Supabase
1. Go to Supabase Dashboard
2. Click **Table Editor** (left sidebar)
3. Click `usage_records` table
4. **Refresh** (click refresh icon or refresh browser)
5. You should see your data! 🎉

Look for:
- `user_id` = Your entered name (e.g., "Suganthan")
- `device_id` = Your phone's Android ID
- `app_name` = Apps you use (Chrome, Discord, WhatsApp, etc.)
- `usage_time` = Milliseconds used

---

## 🔄 After That - What Happens Automatically

✅ Every 15 minutes → New records appear in `usage_records`
✅ Every day → New row in `daily_usage_summary`
✅ All with your name as `user_id`
✅ All with correct timestamps (IST)

---

## ✅ Success Looks Like This

**In Supabase Table Editor:**
```
Page 1 of 32 | 100 rows | 3,145 records

id                  device_id   user_id     app_name        usage_time
-----------------------------------------------------------------
0654a441-afbd-46...  047c6c67f...  Suganthan  Chrome          1800000
0654a441-afbd-47...  047c6c67f...  Suganthan  Discord         900000  
0654a441-afbd-48...  047c6c67f...  Suganthan  WhatsApp        600000
```

Where:
- `device_id` = Your phone ID
- `user_id` = **Your actual name** (not placeholder!)
- `app_name` = Real apps you use
- `usage_time` = Real usage in milliseconds

---

## 🚨 If Something Goes Wrong

**Problem: Still shows "user_wallet_address_or_ens"**
- Delete old data in Supabase (Step 1)
- Clear app data: `adb shell pm clear com.example.digitalwellbeingviewer`
- Rebuild: `.\gradlew clean assembleDebug`
- Reinstall: `adb install -r app/build/outputs/apk/debug/app-debug.apk`
- Try again

**Problem: No data after 30 minutes**
- Check app has Usage Access permission
- Check phone is connected to internet
- Check Supabase is online
- Check SupabaseClient.kt has correct URL and key

**Problem: OnboardingActivity doesn't appear second time**
- That's CORRECT! (It only appears once)
- Your name is saved in phone's memory

---

## 📊 What Your Data Looks Like

### `usage_records` table:
```
Every 15 minutes you get ~15-50 new rows
One row per app per sync
Example:
- Chrome: 1800000 ms (30 min)
- Discord: 900000 ms (15 min)
- WhatsApp: 600000 ms (10 min)
- etc.
```

### `daily_usage_summary` table:
```
One row per day per device
Example:
date: 2026-02-05
total_screen_time: 43200000 (12 hours)
app_count: 23
most_used_app: Chrome
```

---

## 🎉 That's It!

You now have a fully working app that:
✅ Collects your app usage data
✅ Syncs to Supabase every 15 minutes
✅ Shows your actual name (not placeholder)
✅ Accumulates data for analytics
✅ Ready for Next.js frontend

**Your data is ready to build on!**

---

## 📚 For More Details
Read: `HOW_TO_USE_EXISTING_TABLES.md` (comprehensive guide)
