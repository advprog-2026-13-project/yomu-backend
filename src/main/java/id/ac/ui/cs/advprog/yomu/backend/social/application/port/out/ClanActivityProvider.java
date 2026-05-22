package id.ac.ui.cs.advprog.yomu.backend.social.application.port.out;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.ClanActivitySnapshot;
import java.util.UUID;

public interface ClanActivityProvider {
  ClanActivitySnapshot getActivity(UUID clanId);
}
