package dev._2lstudios.koth.schedule;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;

public class KothScheduleManager {
  private final List<KothSchedule> schedules = new ArrayList<>();
  private final String dataFolderPath;
  private KothSchedule current = null;
  private KothSchedule nextKothSchedule = null;

  public KothScheduleManager(String dataFolderPath) {
    this.dataFolderPath = dataFolderPath;
  }

  public void load() {
    YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(this.dataFolderPath + "/schedules.yml"));

    for (String key : config.getKeys(false)) {
      String[] time = key.split(":");

      try {
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);
        String name = config.getString(key);

        add(hour, minute, name);
      } catch (NumberFormatException numberFormatException) {
      }
    }
  }

  public void save() {
    YamlConfiguration config = new YamlConfiguration();

    for (KothSchedule kothSchedule : this.schedules) {
      LocalTime scheduleTime = kothSchedule.getLocalTime();
      int hour = scheduleTime.getHour();
      int minute = scheduleTime.getMinute();
      String name = kothSchedule.getName();

      config.set(hour + ":" + minute, name);
    }

    try {
      config.save(this.dataFolderPath + "/schedules.yml");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Collection<KothSchedule> getSchedules() {
    return this.schedules;
  }

  public KothSchedule get(int hour, int minute) {
    Iterator<KothSchedule> scheduleIterator = this.schedules.iterator();

    while (scheduleIterator.hasNext()) {
      KothSchedule kothSchedule = scheduleIterator.next();
      LocalTime localTime = kothSchedule.getLocalTime();

      if (localTime.getHour() == hour && localTime.getMinute() == minute) {
        return kothSchedule;
      }
    }

    return null;
  }

  public boolean remove(int hour, int minute) {
    Iterator<KothSchedule> scheduleIterator = this.schedules.iterator();

    while (scheduleIterator.hasNext()) {
      KothSchedule kothSchedule = scheduleIterator.next();
      LocalTime localTime = kothSchedule.getLocalTime();

      if (localTime.getHour() == hour && localTime.getMinute() == minute) {
        scheduleIterator.remove();

        updateNext();

        return true;
      }
    }

    return false;
  }

  public boolean add(int hour, int minute, String name) {
    remove(hour, minute);

    this.schedules.add(new KothSchedule(hour, minute, name));
    Collections.sort(this.schedules);

    updateNext();

    return true;
  }

  public boolean isAfter(LocalTime a, LocalTime b) {
    return (a.getHour() > b.getHour() || (a.getHour() == b.getHour() && a.getMinute() > b.getMinute()));
  }

  public void updateNext() {
    LocalTime localTime = LocalTime.now();
    KothSchedule kothSchedule = null;
    LocalTime lastKothTime = null;

    for (KothSchedule kothSchedule1 : this.schedules) {
      LocalTime kothLocalTime = kothSchedule1.getLocalTime();

      if (isAfter(kothLocalTime, localTime) && (lastKothTime == null || !isAfter(kothLocalTime, lastKothTime))) {
        kothSchedule = kothSchedule1;
        lastKothTime = kothSchedule.getLocalTime();
      }
    }

    this.nextKothSchedule = kothSchedule;
  }

  public KothSchedule next() {
    return this.nextKothSchedule;
  }

  public void setCurrent(KothSchedule kothSchedule) {
    this.current = kothSchedule;
  }

  public KothSchedule getCurrent() {
    return this.current;
  }
}