
package com.chang1o.model;

import java.time.LocalDateTime;

public class UserHealthData {

    private int id;
    private int userId;
    private double weight;
    private double height;
    private int age;
    private String gender;
    private String activityLevel;
    private double targetWeight;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserHealthData() {
    }

    public UserHealthData(int userId, double weight, double height, int age, String gender, String activityLevel, double targetWeight) {
        this.userId = userId;
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.gender = gender;
        this.activityLevel = activityLevel;
        this.targetWeight = targetWeight;
    }

    public UserHealthData(int id, int userId, double weight, double height, int age, String gender, String activityLevel, double targetWeight, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.gender = gender;
        this.activityLevel = activityLevel;
        this.targetWeight = targetWeight;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public double calculateBMI() {
        if (height <= 0) return 0;
        double heightInMeters = height / 100.0; // 转换为米
        return weight / (heightInMeters * heightInMeters);
    }

    public String getBMICategory() {
        double bmi = calculateBMI();
        if (bmi < 18.5) return "偏瘦";
        if (bmi < 24) return "正常";
        if (bmi < 28) return "超重";
        return "肥胖";
    }

    public double calculateBMR() {
        if ("M".equalsIgnoreCase(gender)) {
            return 10 * weight + 6.25 * height - 5 * age + 5;
        } else if ("F".equalsIgnoreCase(gender)) {
            return 10 * weight + 6.25 * height - 5 * age - 161;
        }
        return 0;
    }

    public double calculateTDEE() {
        double bmr = calculateBMR();
        double activityMultiplier = getActivityMultiplier();
        return bmr * activityMultiplier;
    }

    private double getActivityMultiplier() {
        if (activityLevel == null) return 1.2;
        switch (activityLevel) {
            case "sedentary": return 1.2;      // 久坐不动
            case "light": return 1.375;        // 轻度活动
            case "moderate": return 1.55;      // 中度活动
            case "active": return 1.725;       // 高度活动
            case "very_active": return 1.9;    // 极高活动
            default: return 1.2;
        }
    }

    public String getIdealWeightRange() {
        double heightInMeters = height / 100.0;
        if (heightInMeters <= 0) return "0.0kg - 0.0kg";
        double minIdealWeight = 18.5 * heightInMeters * heightInMeters;
        double maxIdealWeight = 24 * heightInMeters * heightInMeters;
        // Use String.format to ensure consistent decimal representation
        return String.format("%.1fkg - %.1fkg", minIdealWeight, maxIdealWeight);
    }

    public double getWeightDifference() {
        return weight - targetWeight;
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public double getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(double targetWeight) {
        this.targetWeight = targetWeight;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "UserHealthData{" +
                "id=" + id +
                ", userId=" + userId +
                ", weight=" + weight +
                ", height=" + height +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", activityLevel='" + activityLevel + '\'' +
                ", targetWeight=" + targetWeight +
                ", bmi=" + String.format("%.1f", calculateBMI()) +
                ", bmr=" + String.format("%.0f", calculateBMR()) +
                ", tdee=" + String.format("%.0f", calculateTDEE()) +
                '}';
    }
}