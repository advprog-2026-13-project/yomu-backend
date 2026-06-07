-- Menyimpan kapan season liga saat ini dimulai.
-- Dipakai modul social untuk men-"reset" perhitungan debuff (akurasi kuis)
-- agar hanya menghitung aktivitas sejak season berjalan.
-- Single-row table (id selalu = 1).
CREATE TABLE league_season (
    id INTEGER PRIMARY KEY,
    started_at TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO league_season (id, started_at) VALUES (1, NOW());
