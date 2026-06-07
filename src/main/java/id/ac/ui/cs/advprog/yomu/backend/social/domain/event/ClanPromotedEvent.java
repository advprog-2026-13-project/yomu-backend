package id.ac.ui.cs.advprog.yomu.backend.social.domain.event;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import java.util.List;
import java.util.UUID;

/**
 * Diterbitkan saat sebuah Clan dipromosikan ke Tier yang lebih tinggi pada akhir musim. Modul lain
 * (mis. Achievements) dapat men-subscribe event ini untuk membuka pencapaian khusus — misalnya saat
 * {@code newTier == Tier.DIAMOND} (Tier tertinggi).
 *
 * <p>{@code memberUserIds} memuat seluruh anggota Clan pada saat promosi, agar consumer (mis.
 * Achievements yang per-user) bisa memberi pencapaian ke semua anggota tanpa perlu query repository
 * modul Social.
 *
 * <p>Sengaja berupa POJO murni (tanpa {@code extends ApplicationEvent}) agar domain layer tetap
 * bebas dependensi Spring; Spring otomatis membungkus objek biasa saat dipublish via {@code
 * ApplicationEventPublisher}.
 */
public record ClanPromotedEvent(
    UUID clanId, String clanName, Tier newTier, UUID leaderId, List<UUID> memberUserIds) {}
