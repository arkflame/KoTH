package dev._2lstudios.koth.schedule;

import java.time.LocalTime;

public class KothSchedule implements Comparable<KothSchedule> {
  private final LocalTime localTime;
  private final String name;

  public KothSchedule(int hour, int minute, String name) {
    this.localTime = LocalTime.of(hour, minute);
    this.name = name;
  }

  private String formatNumber(int number) {
    return (number > 9) ? ("" + number) : ("0" + number);
  }

  public LocalTime getLocalTime() {
    return this.localTime;
  }

  public String getFormattedTime() {
    String hour = formatNumber(this.localTime.getHour());
    String minute = formatNumber(this.localTime.getMinute());

    return hour + ":" + minute;
  }

  public String getName() {
    return this.name;
  }

  public int compareTo(KothSchedule o) {
    return this.localTime.compareTo(o.getLocalTime());
  }
}