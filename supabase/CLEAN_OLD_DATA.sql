-- ============================================
-- CLEAN EXISTING TABLES - RUN THIS FIRST
-- ============================================
-- Copy this entire script and paste into Supabase SQL Editor
-- This removes all old placeholder data
-- ============================================

-- Delete old placeholder records from usage_records
DELETE FROM usage_records 
WHERE user_id = 'user_wallet_address_or_ens' 
   OR user_id IS NULL 
   OR user_id = '';

-- Delete old placeholder records from daily_usage_summary  
DELETE FROM daily_usage_summary
WHERE user_id = 'user_wallet_address_or_ens'
   OR user_id IS NULL
   OR user_id = '';

-- Verify deletion
SELECT 
    'usage_records' as table_name,
    COUNT(*) as remaining_records
FROM usage_records
UNION ALL
SELECT 
    'daily_usage_summary' as table_name,
    COUNT(*) as remaining_records
FROM daily_usage_summary;

-- If you want to see what you deleted:
-- SELECT COUNT(*) as deleted_records FROM usage_records 
-- WHERE user_id = 'user_wallet_address_or_ens';
