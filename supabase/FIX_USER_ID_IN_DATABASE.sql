-- ============================================
-- FIX: Update user_id from placeholder to actual user name
-- Run this in Supabase SQL Editor
-- ============================================

-- IMPORTANT: Before running this, backup your data!

-- Option 1: DELETE OLD DATA (Recommended - start fresh)
-- This will delete all old records with placeholder user_id
-- New records will be synced with actual user names
DELETE FROM usage_records 
WHERE user_id = 'user_wallet_address_or_ens';

DELETE FROM daily_usage_summary 
WHERE user_id = 'user_wallet_address_or_ens';

-- VERIFY: Check that old data is gone
SELECT COUNT(*) as remaining_records FROM usage_records;
-- Expected: 0 or lower number

-- Option 2: UPDATE OLD DATA (If you want to keep it)
-- Uncomment below to update old records with a user name
-- First check how many records have the placeholder:
SELECT COUNT(*) as old_records 
FROM usage_records 
WHERE user_id = 'user_wallet_address_or_ens';

-- Then update them:
-- UPDATE usage_records 
-- SET user_id = 'YourActualName'
-- WHERE user_id = 'user_wallet_address_or_ens';
--
-- UPDATE daily_usage_summary 
-- SET user_id = 'YourActualName'
-- WHERE user_id = 'user_wallet_address_or_ens';

-- ============================================
-- After running this:
-- 1. Rebuild and install app
-- 2. Uninstall old app (to clear SharedPreferences)
-- 3. Install fresh APK
-- 4. Open app
-- 5. Enter your actual name when prompted
-- 6. Wait 15-20 minutes for first sync
-- 7. Check Supabase - you'll see your name in user_id!
-- ============================================
