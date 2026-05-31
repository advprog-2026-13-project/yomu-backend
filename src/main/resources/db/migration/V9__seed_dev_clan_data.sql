-- =============================================================
-- V9__seed_dev_clan_data.sql
-- DEV-ONLY seed: akun, clan, anggota, dan join request dummy
-- untuk keperluan testing UI Social & Leaderboard.
--
-- ⚠️  HAPUS migration ini sebelum deploy ke production.
--
-- Password semua akun: Test1234!
-- =============================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ─────────────────────────────────────────────
-- USERS  (12 akun: 10 user biasa, 1 admin, 1 free)
-- ─────────────────────────────────────────────
INSERT INTO users (id, username, display_name, email, password_hash, role, created_at)
VALUES
  -- Free user (tidak punya clan) — untuk login manual testing
  ('aaaaaaaa-0000-0000-0000-000000000001',
   'fattanazz', 'Fatta Nazzaka', 'fatta@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),

  -- Diamond clan leaders & members
  ('aaaaaaaa-0000-0000-0000-000000000002',
   'budi_elite', 'Budi Santoso', 'budi@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000003',
   'sari_cahaya', 'Sari Dewi', 'sari@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000004',
   'agus_baca', 'Agus Prasetyo', 'agus@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),

  -- Gold clan leaders & members
  ('aaaaaaaa-0000-0000-0000-000000000005',
   'dodi_pena', 'Dodi Permana', 'dodi@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000006',
   'rini_garuda', 'Rini Rahayu', 'rini@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000007',
   'toni_emas', 'Toni Wijaya', 'toni@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),

  -- Silver clan leaders & members
  ('aaaaaaaa-0000-0000-0000-000000000008',
   'dewi_argenta', 'Dewi Anggraini', 'dewi@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000009',
   'joko_perak', 'Joko Susilo', 'joko@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),

  -- Bronze clan leaders
  ('aaaaaaaa-0000-0000-0000-000000000010',
   'wati_perunggu', 'Wati Setiawan', 'wati@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000011',
   'andi_pemula', 'Andi Firmansyah', 'andi@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),

  -- Admin
  ('aaaaaaaa-0000-0000-0000-000000000099',
   'admin_yomu', 'Admin Yomu', 'admin@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'ADMIN', NOW())

ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────
-- CLANS  (2 per tier = 8 clan total)
-- ─────────────────────────────────────────────
INSERT INTO clans (id, name, tier, score, leader_id, created_at)
VALUES
  -- DIAMOND (2 clan)
  ('cccccccc-0000-0000-0000-000000000001',
   'Nusantara Elite', 'DIAMOND', 98500,
   'aaaaaaaa-0000-0000-0000-000000000002', NOW()),
  ('cccccccc-0000-0000-0000-000000000002',
   'Cahaya Timur', 'DIAMOND', 87200,
   'aaaaaaaa-0000-0000-0000-000000000003', NOW()),

  -- GOLD (2 clan)
  ('cccccccc-0000-0000-0000-000000000003',
   'Pena Emas', 'GOLD', 54800,
   'aaaaaaaa-0000-0000-0000-000000000005', NOW()),
  ('cccccccc-0000-0000-0000-000000000004',
   'Garuda Pena', 'GOLD', 47300,
   'aaaaaaaa-0000-0000-0000-000000000006', NOW()),

  -- SILVER (2 clan)
  ('cccccccc-0000-0000-0000-000000000005',
   'Argenta FC', 'SILVER', 21500,
   'aaaaaaaa-0000-0000-0000-000000000008', NOW()),
  ('cccccccc-0000-0000-0000-000000000006',
   'Langit Biru', 'SILVER', 18900,
   'aaaaaaaa-0000-0000-0000-000000000009', NOW()),

  -- BRONZE (2 clan)
  ('cccccccc-0000-0000-0000-000000000007',
   'Bintang Literasi', 'BRONZE', 8200,
   'aaaaaaaa-0000-0000-0000-000000000010', NOW()),
  ('cccccccc-0000-0000-0000-000000000008',
   'Pemula Sejati', 'BRONZE', 5600,
   'aaaaaaaa-0000-0000-0000-000000000011', NOW())

ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────
-- CLAN MEMBERS  (setiap leader = LEADER role,
--               beberapa member tambahan)
-- ─────────────────────────────────────────────
INSERT INTO clan_members (id, clan_id, user_id, role, joined_at)
VALUES
  -- Nusantara Elite (DIAMOND)
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000002', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000004', 'MEMBER', NOW()),

  -- Cahaya Timur (DIAMOND)
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000003', 'LEADER', NOW()),

  -- Pena Emas (GOLD)
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000003', 'aaaaaaaa-0000-0000-0000-000000000005', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000003', 'aaaaaaaa-0000-0000-0000-000000000007', 'MEMBER', NOW()),

  -- Garuda Pena (GOLD)
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000004', 'aaaaaaaa-0000-0000-0000-000000000006', 'LEADER', NOW()),

  -- Argenta FC (SILVER)
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000005', 'aaaaaaaa-0000-0000-0000-000000000008', 'LEADER', NOW()),

  -- Langit Biru (SILVER)
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000006', 'aaaaaaaa-0000-0000-0000-000000000009', 'LEADER', NOW()),

  -- Bintang Literasi (BRONZE)
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000007', 'aaaaaaaa-0000-0000-0000-000000000010', 'LEADER', NOW()),

  -- Pemula Sejati (BRONZE)
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000008', 'aaaaaaaa-0000-0000-0000-000000000011', 'LEADER', NOW())

ON CONFLICT (user_id) DO NOTHING;

-- ─────────────────────────────────────────────
-- JOIN REQUESTS  (pending ke Nusantara Elite)
-- User fattanazz (u01) & user lain minta gabung
-- ─────────────────────────────────────────────
INSERT INTO clan_join_requests (id, clan_id, user_id, status, created_at)
VALUES
  (gen_random_uuid(),
   'cccccccc-0000-0000-0000-000000000001',
   'aaaaaaaa-0000-0000-0000-000000000001',
   'PENDING', NOW()),
  (gen_random_uuid(),
   'cccccccc-0000-0000-0000-000000000001',
   'aaaaaaaa-0000-0000-0000-000000000099',
   'PENDING', NOW() - INTERVAL '2 days')

ON CONFLICT DO NOTHING;
