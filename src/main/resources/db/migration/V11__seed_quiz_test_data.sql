-- =============================================================
-- V11__seed_quiz_test_data.sql
-- DEV-ONLY: Data untuk test manual alur quiz → clan score.
--
-- Skenario:
--   Login sebagai fattanazz (Test1234!) → buka Bacaan → kerjakan kuis
--   → cek skor TestClan Yomu bertambah di leaderboard BRONZE.
--
-- ⚠️  HAPUS migration ini sebelum deploy ke production.
-- =============================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ─────────────────────────────────────────────
-- USER: ketua khusus TestClan (tidak untuk login manual)
-- ─────────────────────────────────────────────
INSERT INTO users (id, username, display_name, email, password_hash, role, created_at)
VALUES (
  'aaaaaaaa-0000-0000-0000-000000000024',
  'yomu_ketua',
  'Ketua Test',
  'ketua@yomu.id',
  crypt('Test1234!', gen_salt('bf', 10)),
  'USER',
  NOW()
)
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────
-- CLAN: TestClan Yomu — skor awal 0 agar delta quiz jelas terlihat
-- ─────────────────────────────────────────────
INSERT INTO clans (id, name, tier, score, leader_id, created_at)
VALUES (
  'cccccccc-0000-0000-0000-000000000021',
  'TestClan Yomu',
  'BRONZE',
  0,
  'aaaaaaaa-0000-0000-0000-000000000024',
  NOW()
)
ON CONFLICT (id) DO NOTHING;

-- ─────────────────────────────────────────────
-- CLAN MEMBERS
-- ─────────────────────────────────────────────
INSERT INTO clan_members (id, clan_id, user_id, role, joined_at)
VALUES
  -- ketua
  (gen_random_uuid(),
   'cccccccc-0000-0000-0000-000000000021',
   'aaaaaaaa-0000-0000-0000-000000000024',
   'LEADER', NOW()),
  -- fattanazz sebagai anggota (target testing)
  (gen_random_uuid(),
   'cccccccc-0000-0000-0000-000000000021',
   'aaaaaaaa-0000-0000-0000-000000000001',
   'MEMBER', NOW())
ON CONFLICT (user_id) DO NOTHING;

-- Batalkan join request fattanazz ke Nusantara Elite (sudah masuk TestClan)
UPDATE clan_join_requests
SET status = 'REJECTED', resolved_at = NOW()
WHERE user_id  = 'aaaaaaaa-0000-0000-0000-000000000001'
  AND status   = 'PENDING';

-- ─────────────────────────────────────────────
-- READING: bacaan uji dengan 4 soal
-- Semua jawaban benar → score = 100 → clan +100 poin
-- ─────────────────────────────────────────────
INSERT INTO reading (reading_id, title, content, category, author_id, hidden)
VALUES (
  'eeeeeeee-0000-0000-0000-000000000001',
  'Arsitektur Hexagonal & Design Pattern di Yomu',
  'Arsitektur Hexagonal (Ports and Adapters) memisahkan domain murni dari infrastruktur. '
  'Domain layer tidak boleh mengimpor Spring atau JPA. '
  'Strategy Pattern dipakai untuk algoritma ranking berbeda per tier. '
  'Decorator Pattern dipakai untuk Buff/Debuff stackable pada skor clan. '
  'Observer Pattern (Spring Events) dipakai agar modul tidak saling bergantung langsung.',
  'Teknologi',
  'aaaaaaaa-0000-0000-0000-000000000099',
  false
)
ON CONFLICT (reading_id) DO NOTHING;

-- Soal 1
INSERT INTO question (question_id, reading_id, question_text, correct_answer)
VALUES (
  'ffffffff-0000-0000-0000-000000000001',
  'eeeeeeee-0000-0000-0000-000000000001',
  'Apa nama lain dari Arsitektur Hexagonal?',
  'Ports and Adapters'
)
ON CONFLICT (question_id) DO NOTHING;

INSERT INTO question_options (question_question_id, options)
SELECT 'ffffffff-0000-0000-0000-000000000001', opt
FROM (VALUES ('Ports and Adapters'), ('MVC'), ('Microservices'), ('CQRS')) AS t(opt)
WHERE NOT EXISTS (
  SELECT 1 FROM question_options
  WHERE question_question_id = 'ffffffff-0000-0000-0000-000000000001'
);

-- Soal 2
INSERT INTO question (question_id, reading_id, question_text, correct_answer)
VALUES (
  'ffffffff-0000-0000-0000-000000000002',
  'eeeeeeee-0000-0000-0000-000000000001',
  'Design pattern apa yang dipakai untuk ranking berbeda per tier di modul social?',
  'Strategy'
)
ON CONFLICT (question_id) DO NOTHING;

INSERT INTO question_options (question_question_id, options)
SELECT 'ffffffff-0000-0000-0000-000000000002', opt
FROM (VALUES ('Strategy'), ('Singleton'), ('Builder'), ('Command')) AS t(opt)
WHERE NOT EXISTS (
  SELECT 1 FROM question_options
  WHERE question_question_id = 'ffffffff-0000-0000-0000-000000000002'
);

-- Soal 3
INSERT INTO question (question_id, reading_id, question_text, correct_answer)
VALUES (
  'ffffffff-0000-0000-0000-000000000003',
  'eeeeeeee-0000-0000-0000-000000000001',
  'Design pattern apa yang dipakai untuk Buff/Debuff stackable pada skor clan?',
  'Decorator'
)
ON CONFLICT (question_id) DO NOTHING;

INSERT INTO question_options (question_question_id, options)
SELECT 'ffffffff-0000-0000-0000-000000000003', opt
FROM (VALUES ('Decorator'), ('Observer'), ('Factory'), ('Proxy')) AS t(opt)
WHERE NOT EXISTS (
  SELECT 1 FROM question_options
  WHERE question_question_id = 'ffffffff-0000-0000-0000-000000000003'
);

-- Soal 4
INSERT INTO question (question_id, reading_id, question_text, correct_answer)
VALUES (
  'ffffffff-0000-0000-0000-000000000004',
  'eeeeeeee-0000-0000-0000-000000000001',
  'Apa yang dipakai modul social agar antar-modul tidak saling bergantung langsung?',
  'Spring Events'
)
ON CONFLICT (question_id) DO NOTHING;

INSERT INTO question_options (question_question_id, options)
SELECT 'ffffffff-0000-0000-0000-000000000004', opt
FROM (VALUES ('Spring Events'), ('Direct method call'), ('Shared database'), ('REST API')) AS t(opt)
WHERE NOT EXISTS (
  SELECT 1 FROM question_options
  WHERE question_question_id = 'ffffffff-0000-0000-0000-000000000004'
);
