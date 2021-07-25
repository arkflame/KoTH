package dev._2lstudios.koth.koth;

import java.util.UUID;

public class KothPlayer {
  private final UUID uuid;
  private KothEvent kothEvent = null;
  private int secondsInside = 0;
  
  KothPlayer(UUID uuid) {
    this.uuid = uuid;
  }
  
  public int getSecondsInside() {
    return this.secondsInside;
  }
  
  public void setSecondsInside(int secondsInside) {
    this.secondsInside = secondsInside;
  }
  
  public KothEvent getKothEvent() {
    return this.kothEvent;
  }
  
  public void setKothEvent(KothEvent kothEvent) {
    if (this.kothEvent != kothEvent) {
      this.secondsInside = 0;
      this.kothEvent = kothEvent;
    } 
  }
  
  UUID getUUID() {
    return this.uuid;
  }
}


/* Location:              C:\Users\LinsaFTW\Desktop\KoTH.jar!\dev\_2lstudios\koth\koth\KothPlayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */