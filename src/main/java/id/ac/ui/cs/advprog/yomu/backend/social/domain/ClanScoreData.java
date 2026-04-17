package id.ac.ui.cs.advprog.yomu.backend.social.domain;

import java.util.UUID;

public record ClanScoreData(
    UUID clanId, String clanName, Tier tier, long score, long memberCount) {}