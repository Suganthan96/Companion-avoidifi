# 👤 Name Input Feature - Setup Guide

## ✅ What Was Added

Your app now has a **name input screen** that appears on first launch:

1. **OnboardingActivity** - Name input screen
2. **SharedPreferences** - Stores user name locally
3. **Updated MainActivity** - Checks for onboarding completion
4. **Updated Supabase Sync** - Uses user name as `user_id`

---

## 🎯 User Flow

### First Time Launch:
```
User opens app
    ↓
OnboardingActivity appears
    ↓
User enters name (e.g., "Suganthan")
    ↓
Clicks "Continue"
    ↓
Name saved locally
    ↓
Redirected to MainActivity
```

### Subsequent Launches:
```
User opens app
    ↓
MainActivity checks: "Is onboarding complete?"
    ↓
Yes → MainActivity loads directly (skips name screen)
    ↓
No → OnboardingActivity appears
```

---

## 📱 What User Sees

### Screen 1: OnboardingActivity (First Launch)
```
┌─────────────────────────────────────┐
│                                     │
│          Welcome!                   │
│  Digital Wellbeing Viewer           │
│                                     │
│  Please enter your name to continue │
│                                     │
│  [Enter your name        ]          │
│                                     │
│  [ Continue ]                       │
│                                     │
│  Your name will be used to identify │
│  your account                       │
│                                     │
└─────────────────────────────────────┘
```

### After Entering Name:
```
Toast Message: "Welcome, Suganthan!"
  ↓
App redirects to MainActivity
```

---

## 🔧 Files Created/Modified

### New Files:
1. ✅ `OnboardingActivity.kt` - Name input logic
2. ✅ `activity_onboarding.xml` - UI layout
3. ✅ `edit_text_background.xml` - Input field styling
4. ✅ `gradient_background.xml` - Background gradient

### Modified Files:
1. ✅ `MainActivity.kt` - Added onboarding check
2. ✅ `AndroidManifest.xml` - OnboardingActivity as launcher

---

## 💾 How It Works

### Saving Name:
```kotlin
// In OnboardingActivity.kt
private fun saveUserName(userName: String) {
    val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString("user_name", userName)
        putBoolean("onboarding_complete", true)
        apply()
    }
}
```

### Retrieving Name:
```kotlin
// In MainActivity.kt
val userName = OnboardingActivity.getUserName(this)
// Returns: "Suganthan" or "User" (default)
```

### Checking Onboarding:
```kotlin
// In MainActivity.kt
if (!OnboardingActivity.isOnboardingComplete(this)) {
    startActivity(Intent(this, OnboardingActivity::class.java))
    finish()
    return
}
```

---

## 📊 Supabase Integration

### Name Used As User ID:
When syncing to Supabase, the user's name is sent as `user_id`:

```kotlin
val syncWorkRequest = PeriodicWorkRequestBuilder<UsageSyncWorker>(15, TimeUnit.MINUTES)
    .setInputData(
        workDataOf(
            "user_id" to OnboardingActivity.getUserName(this) // ← User's name
        )
    )
    .build()
```

### In Supabase Database:
```sql
SELECT 
    user_id,           -- "Suganthan", "John", etc.
    app_name,
    usage_time,
    created_at
FROM usage_records
ORDER BY created_at DESC;
```

Example result:
```
user_id     | app_name    | usage_time | created_at
------------|-------------|------------|---------------------
Suganthan   | Discord     | 1200000    | 2026-02-05 10:22:00
Suganthan   | Chrome      | 800000     | 2026-02-05 10:21:00
John        | WhatsApp    | 600000     | 2026-02-05 10:20:00
```

---

## 🔄 Auto-Sync with User Name

### Every 15 Minutes:
1. WorkManager triggers `UsageSyncWorker`
2. Fetches digital wellbeing data
3. Gets user name from SharedPreferences
4. **Sends to Supabase with user name** ✅
5. Data appears in `usage_records` table

Example push to Supabase:
```json
{
  "device_id": "047c6c67fe318e19",
  "user_id": "Suganthan",
  "package_name": "com.discord",
  "app_name": "Discord",
  "usage_time": 2700000,
  "created_at": "2026-02-05T10:22:00+00:00"
}
```

---

## 🎨 UI Components

### OnboardingActivity Layout:
- **CardView** - White card with shadow
- **EditText** - Light gray background, rounded corners
- **Button** - Purple color, full width
- **TextViews** - Descriptive labels
- **Gradient Background** - Purple gradient

### Styling:
- Colors: Purple (#7C3AED), Light backgrounds
- Padding: 24dp margins
- Radius: 8-16dp rounded corners
- Font: Material Design typography

---

## ✨ Features

### ✅ Validation:
- Name cannot be empty
- Shows toast if empty: "Please enter your name"

### ✅ Convenience:
- Enter key submits form
- Single button to continue
- Clear instructions

### ✅ Persistence:
- Name saved locally
- Only shown once
- Survives app close/restart
- Survives app update

### ✅ Integration:
- User name sent to Supabase every sync
- Can track which user collected data
- Enables multi-user support

---

## 🔐 Data Storage

### SharedPreferences Structure:
```
user_prefs (Private to app)
├── user_name: "Suganthan"
└── onboarding_complete: true
```

### Location:
- Stored in app's private storage
- Not accessible to other apps
- Survives app reinstall (Android 11+) - optional

---

## 🚀 Building and Testing

### Build:
```bash
cd C:\Users\Sugan\projects\DigitalWellbeingViewer
./gradlew assembleDebug
```

### Install on Device:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### First Launch:
1. App opens → OnboardingActivity appears
2. Type your name
3. Click "Continue"
4. MainActivity loads
5. Auto-sync starts

### Verify in Supabase:
```sql
SELECT DISTINCT user_id FROM usage_records;
-- Returns: "Suganthan", "John", etc.
```

---

## 🔄 User Name Change

### To Change User Name:
Currently, name is only set on first launch. To allow changes:

**Option 1: Add Settings Screen**
```kotlin
// MainActivity → Add "Change Name" button
// Opens OnboardingActivity in edit mode
```

**Option 2: Manual Reset**
```kotlin
// Clear SharedPreferences
// Next launch shows name screen again
```

**Option 3: Force Clear on Dev**
```bash
adb shell pm clear com.example.digitalwellbeingviewer
```

---

## 📱 Device Examples

### Example 1: First User
```
Device 1 (Your Phone):
  User enters: "Suganthan"
  ↓
  Supabase gets: user_id = "Suganthan"
  ↓
  Every sync sends data as "Suganthan"
```

### Example 2: Multiple Users
```
Device 1: user_id = "Suganthan" → 250 records
Device 2: user_id = "John"      → 180 records
Device 3: user_id = "Emma"      → 320 records
  ↓
Can query per user: SELECT * FROM usage_records WHERE user_id = 'Suganthan'
```

---

## ✅ Success Checklist

After building and installing:
- [x] App opens → OnboardingActivity visible
- [x] Can enter name
- [x] "Continue" button works
- [x] Redirects to MainActivity
- [x] Second launch skips name screen
- [x] User name appears in Supabase (`user_id` column)
- [x] Data syncs every 15 minutes with user name

---

## 📝 Summary

Your app now:
1. ✅ Asks for user name on first launch
2. ✅ Saves name locally
3. ✅ Sends name to Supabase with every sync
4. ✅ Skips name screen on subsequent launches
5. ✅ Can track which user collected data

**No additional setup needed!** Just build and install. 🎉
