package dev._2lstudios.koth.koth;

import java.util.UUID;

public class KothPlayer {
  private final UUID uuid;
  private final String name;
  private KothEvent kothEvent = null;
  private int secondsCaptured = 0;

  KothPlayer(final UUID uuid, final String name) {
    this.uuid = uuid;
    this.name = name;
  }

  public int getSecondsCaptured() {
    return this.secondsCaptured;
  }

  public void setSecondsCaptured(int secondsCaptured) {
    this.secondsCaptured = secondsCaptured;
  }

  public KothEvent getKothEvent() {
    return this.kothEvent;
  }

  public void setKothEvent(KothEvent kothEvent) {
    if (this.kothEvent != kothEvent) {
      this.secondsCaptured = 0;
      this.kothEvent = kothEvent;
    }
  }

  public UUID getUUID() {
    return this.uuid;
  }

  public String getName() {
    return name;
  }
}