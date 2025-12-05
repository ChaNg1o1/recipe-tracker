
package com.chang1o.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DailyCheckIn {

    private int id;
    private int userId;
    private LocalDate checkInDate;
    private String mood;
    private double sleepHours;
    private int waterIntake;
    private int exerciseMinutes;
    private String notes;
    private LocalDateTime createdAt;

    public DailyCheckIn() {
    }

    public DailyCheckIn(int userId, LocalDate checkInDate, String mood, double sleepHours, int waterIntake, int exerciseMinutes, String notes) {
        this.userId = userId;
        this.checkInDate = checkInDate;
        this.mood = mood;
        this.sleepHours = sleepHours;
        this.waterIntake = waterIntake;
        this.exerciseMinutes = exerciseMinutes;
        this.notes = notes;
    }

    public DailyCheckIn(int id, int userId, LocalDate checkInDate, String mood, double sleepHours, int waterIntake, int exerciseMinutes, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.checkInDate = checkInDate;
        this.mood = mood;
        this.sleepHours = sleepHours;
        this.waterIntake = waterIntake;
        this.exerciseMinutes = exerciseMinutes;
        this.notes = notes;
        this.createdAt = createdAt;
    }


    public String getMoodDescription() {
        switch (mood) {
            case "great": return "非常好";
            case "good": return "好";
            case "normal": return "一般";
            case "bad": return "差";
            case "terrible": return "非常差";
            default: return "未知";
        }
    }

    public int getMoodScore() {
        switch (mood) {
            case "great": return 5;
            case "good": return 4;
            case "normal": return 3;
            case "bad": return 2;
            case "terrible": return 1;
            default: return 3;
        }
    }

    public int getHealthScore() {
        int score = 0;
        
        score += getMoodScore() * 4;
        
        if (sleepHours >= 8) score += 30;
        else if (sleepHours >= 7) score += 25;
        else if (sleepHours >= 6) score += 20;
        else if (sleepHours >= 5) score += 10;
        else score += 5;
        
        if (waterIntake >= 2000) score += 25;
        else if (waterIntake >= 1500) score += 20;
        else if (waterIntake >= 1000) score += 15;
        else if (waterIntake >= 500) score += 10;
        else score += 5;
        
        if (exerciseMinutes >= 60) score += 25;
        else if (exerciseMinutes >= 30) score += 20;
        else if (exerciseMinutes >= 15) score += 15;
        else if (exerciseMinutes > 0) score += 10;
        else score += 0;
        
        return Math.min(score, 100);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public double getSleepHours() {
        return sleepHours;
    }

    public void setSleepHours(double sleepHours) {
        this.sleepHours = sleepHours;
    }

    public int getWaterIntake() {
        return waterIntake;
    }

    public void setWaterIntake(int waterIntake) {
        this.waterIntake = waterIntake;
    }

    public int getExerciseMinutes() {
        return exerciseMinutes;
    }

    public void setExerciseMinutes(int exerciseMinutes) {
        this.exerciseMinutes = exerciseMinutes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "DailyCheckIn{" +
                "id=" + id +
                ", userId=" + userId +
                ", checkInDate=" + checkInDate +
                ", mood='" + mood + '\'' +
                ", sleepHours=" + sleepHours +
                ", waterIntake=" + waterIntake +
                ", exerciseMinutes=" + exerciseMinutes +
                ", healthScore=" + getHealthScore() +
                '}';
    }
}