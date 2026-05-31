-- =============================================================
-- V10__seed_expand_clans.sql
-- DEV-ONLY: Tambah 3 clan per tier agar setiap tier punya 5 clan.
-- (V9 sudah ada 2 clan per tier)
-- ⚠️  HAPUS sebelum deploy ke production.
-- =============================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ─────────────────────────────────────────────
-- USERS BARU — leader untuk 12 clan tambahan
-- password semua: Test1234!
-- ─────────────────────────────────────────────
INSERT INTO users (id, username, display_name, email, password_hash, role, created_at)
VALUES
  -- Diamond extra leaders
  ('aaaaaaaa-0000-0000-0000-000000000012',
   'reza_kejora', 'Reza Firmanto', 'reza@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000013',
   'hendra_pro', 'Hendra Kusuma', 'hendra@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000014',
   'nisa_agung', 'Nisa Rahmawati', 'nisa@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),

  -- Gold extra leaders
  ('aaaaaaaa-0000-0000-0000-000000000015',
   'bagas_aksara', 'Bagas Wicaksono', 'bagas@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000016',
   'putri_sabda', 'Putri Handayani', 'putri@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000017',
   'fajar_wira', 'Fajar Nugroho', 'fajar@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),

  -- Silver extra leaders
  ('aaaaaaaa-0000-0000-0000-000000000018',
   'laila_perak', 'Laila Sari', 'laila@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000019',
   'yoga_awan', 'Yoga Pratama', 'yoga@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000020',
   'mira_sinar', 'Mira Anggraeni', 'mira@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),

  -- Bronze extra leaders
  ('aaaaaaaa-0000-0000-0000-000000000021',
   'ryan_tunas', 'Ryan Setiabudi', 'ryan@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000022',
   'sinta_lentera', 'Sinta Maharani', 'sinta@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000023',
   'eko_generasi', 'Eko Prabowo', 'eko@yomu.id',
   crypt('Test1234!', gen_salt('bf', 10)), 'USER', NOW())

ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────
-- CLAN TAMBAHAN (3 per tier)
-- V9 sudah ada: c01-c02 (Diamond), c03-c04 (Gold),
--               c05-c06 (Silver),  c07-c08 (Bronze)
-- ─────────────────────────────────────────────
INSERT INTO clans (id, name, tier, score, leader_id, created_at)
VALUES
  -- DIAMOND (+3)
  ('cccccccc-0000-0000-0000-000000000009',
   'Bintang Kejora', 'DIAMOND', 76800,
   'aaaaaaaa-0000-0000-0000-000000000012', NOW()),
  ('cccccccc-0000-0000-0000-000000000010',
   'Merah Putih Pro', 'DIAMOND', 65400,
   'aaaaaaaa-0000-0000-0000-000000000013', NOW()),
  ('cccccccc-0000-0000-0000-000000000011',
   'Garuda Agung', 'DIAMOND', 54900,
   'aaaaaaaa-0000-0000-0000-000000000014', NOW()),

  -- GOLD (+3)
  ('cccccccc-0000-0000-0000-000000000012',
   'Aksara Jaya', 'GOLD', 43100,
   'aaaaaaaa-0000-0000-0000-000000000015', NOW()),
  ('cccccccc-0000-0000-0000-000000000013',
   'Sabda Sakti', 'GOLD', 38600,
   'aaaaaaaa-0000-0000-0000-000000000016', NOW()),
  ('cccccccc-0000-0000-0000-000000000014',
   'Wira Utama', 'GOLD', 31200,
   'aaaaaaaa-0000-0000-0000-000000000017', NOW()),

  -- SILVER (+3)
  ('cccccccc-0000-0000-0000-000000000015',
   'Perak Mandiri', 'SILVER', 16400,
   'aaaaaaaa-0000-0000-0000-000000000018', NOW()),
  ('cccccccc-0000-0000-0000-000000000016',
   'Awan Putih', 'SILVER', 14200,
   'aaaaaaaa-0000-0000-0000-000000000019', NOW()),
  ('cccccccc-0000-0000-0000-000000000017',
   'Sinar Harapan', 'SILVER', 11800,
   'aaaaaaaa-0000-0000-0000-000000000020', NOW()),

  -- BRONZE (+3)
  ('cccccccc-0000-0000-0000-000000000018',
   'Tunas Muda', 'BRONZE', 4300,
   'aaaaaaaa-0000-0000-0000-000000000021', NOW()),
  ('cccccccc-0000-0000-0000-000000000019',
   'Lentera Baru', 'BRONZE', 3100,
   'aaaaaaaa-0000-0000-0000-000000000022', NOW()),
  ('cccccccc-0000-0000-0000-000000000020',
   'Generasi Emas', 'BRONZE', 1800,
   'aaaaaaaa-0000-0000-0000-000000000023', NOW())

ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────
-- CLAN MEMBERS — setiap leader baru jadi LEADER
-- ─────────────────────────────────────────────
INSERT INTO clan_members (id, clan_id, user_id, role, joined_at)
VALUES
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000009',  'aaaaaaaa-0000-0000-0000-000000000012', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000010', 'aaaaaaaa-0000-0000-0000-000000000013', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000011', 'aaaaaaaa-0000-0000-0000-000000000014', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000012', 'aaaaaaaa-0000-0000-0000-000000000015', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000013', 'aaaaaaaa-0000-0000-0000-000000000016', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000014', 'aaaaaaaa-0000-0000-0000-000000000017', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000015', 'aaaaaaaa-0000-0000-0000-000000000018', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000016', 'aaaaaaaa-0000-0000-0000-000000000019', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000017', 'aaaaaaaa-0000-0000-0000-000000000020', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000018', 'aaaaaaaa-0000-0000-0000-000000000021', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000019', 'aaaaaaaa-0000-0000-0000-000000000022', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000020', 'aaaaaaaa-0000-0000-0000-000000000023', 'LEADER', NOW())

ON CONFLICT (user_id) DO NOTHING;
