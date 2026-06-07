INSERT INTO achievements (id, name, description, achievement_type, milestone)
VALUES (
  'bbbbbbbb-0000-0000-0000-000000000001',
  'Liga Legenda',
  'Clanmu mencapai Tier Diamond, divisi tertinggi liga.',
  'CLAN_REACHED_DIAMOND',
  1
)
ON CONFLICT (id) DO NOTHING;
