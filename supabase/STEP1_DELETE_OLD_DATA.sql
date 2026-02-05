-- ============================================
-- STEP 1: DELETE OLD PLACEHOLDER DATA
-- Run this in Supabase SQL Editor RIGHT NOW
-- ============================================

-- Delete all records with the placeholder
DELETE FROM usage_records 
WHERE user_id = 'user_wallet_address_or_ens';

DELETE FROM daily_usage_summary 
WHERE user_id = 'user_wallet_address_or_ens';

-- Verify it's deleted
SELECT COUNT(*) as total_records FROM usage_records;
-- Should show 0 or very few records

-- Check what user_ids exist now
SELECT DISTINCT user_id FROM usage_records;
-- Should be empty or show only new user names
