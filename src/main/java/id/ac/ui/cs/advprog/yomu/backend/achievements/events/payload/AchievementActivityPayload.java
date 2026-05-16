package id.ac.ui.cs.advprog.yomu.backend.achievements.events.payload;

import java.util.UUID;

/**
 * Common contract for all achievement event payloads. Every payload that triggers achievement
 * progress must implement this interface, providing the userId of the user who performed the
 * activity.
 *
 * <p>This eliminates the need for {@code instanceof} checks in the event listener, making the
 * achievement system scalable: adding a new activity type only requires creating a new payload class
 * that implements this interface — zero changes to the listener.
 */
public interface AchievementActivityPayload {
  UUID getUserId();
}
