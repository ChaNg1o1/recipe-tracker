package com.chang1o.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

@DisplayName("UserHealthData模型测试")
class UserHealthDataTest {

    private UserHealthData userHealthData;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        userHealthData = new UserHealthData();
        now = LocalDateTime.now();
    }

    @Test
    @DisplayName("测试默认构造函数")
    void testDefaultConstructor() {
        UserHealthData defaultHealthData = new UserHealthData();
        assertThat(defaultHealthData).isNotNull();
        assertThat(defaultHealthData.getId()).isEqualTo(0);
        assertThat(defaultHealthData.getUserId()).isEqualTo(0);
        assertThat(defaultHealthData.getWeight()).isEqualTo(0.0);
        assertThat(defaultHealthData.getHeight()).isEqualTo(0.0);
        assertThat(defaultHealthData.getAge()).isEqualTo(0);
        assertThat(defaultHealthData.getGender()).isNull();
        assertThat(defaultHealthData.getActivityLevel()).isNull();
        assertThat(defaultHealthData.getTargetWeight()).isEqualTo(0.0);
        assertThat(defaultHealthData.getCreatedAt()).isNull();
        assertThat(defaultHealthData.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("测试带参数的构造函数")
    void testConstructorWithParameters() {
        int userId = 123;
        double weight = 70.5;
        double height = 175.0;
        int age = 30;
        String gender = "M";
        String activityLevel = "moderate";
        double targetWeight = 68.0;

        UserHealthData healthDataWithParams = new UserHealthData(userId, weight, height, age, gender, activityLevel, targetWeight);

        assertThat(healthDataWithParams).isNotNull();
        assertThat(healthDataWithParams.getUserId()).isEqualTo(userId);
        assertThat(healthDataWithParams.getWeight()).isEqualTo(weight);
        assertThat(healthDataWithParams.getHeight()).isEqualTo(height);
        assertThat(healthDataWithParams.getAge()).isEqualTo(age);
        assertThat(healthDataWithParams.getGender()).isEqualTo(gender);
        assertThat(healthDataWithParams.getActivityLevel()).isEqualTo(activityLevel);
        assertThat(healthDataWithParams.getTargetWeight()).isEqualTo(targetWeight);
        assertThat(healthDataWithParams.getId()).isEqualTo(0);
    }

    @Test
    @DisplayName("测试完整构造函数")
    void testConstructorWithAllParameters() {
        int id = 1;
        int userId = 123;
        double weight = 65.0;
        double height = 170.0;
        int age = 25;
        String gender = "F";
        String activityLevel = "active";
        double targetWeight = 62.0;

        UserHealthData fullHealthData = new UserHealthData(id, userId, weight, height, age, gender, activityLevel, targetWeight, now, now);

        assertThat(fullHealthData).isNotNull();
        assertThat(fullHealthData.getId()).isEqualTo(id);
        assertThat(fullHealthData.getUserId()).isEqualTo(userId);
        assertThat(fullHealthData.getWeight()).isEqualTo(weight);
        assertThat(fullHealthData.getHeight()).isEqualTo(height);
        assertThat(fullHealthData.getAge()).isEqualTo(age);
        assertThat(fullHealthData.getGender()).isEqualTo(gender);
        assertThat(fullHealthData.getActivityLevel()).isEqualTo(activityLevel);
        assertThat(fullHealthData.getTargetWeight()).isEqualTo(targetWeight);
        assertThat(fullHealthData.getCreatedAt()).isEqualTo(now);
        assertThat(fullHealthData.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("测试set和get方法")
    void testSetAndGetMethods() {
        int id = 10;
        int userId = 100;
        double weight = 75.0;
        double height = 180.0;
        int age = 35;
        String gender = "M";
        String activityLevel = "light";
        double targetWeight = 72.0;

        userHealthData.setId(id);
        userHealthData.setUserId(userId);
        userHealthData.setWeight(weight);
        userHealthData.setHeight(height);
        userHealthData.setAge(age);
        userHealthData.setGender(gender);
        userHealthData.setActivityLevel(activityLevel);
        userHealthData.setTargetWeight(targetWeight);
        userHealthData.setCreatedAt(now);
        userHealthData.setUpdatedAt(now);

        assertThat(userHealthData.getId()).isEqualTo(id);
        assertThat(userHealthData.getUserId()).isEqualTo(userId);
        assertThat(userHealthData.getWeight()).isEqualTo(weight);
        assertThat(userHealthData.getHeight()).isEqualTo(height);
        assertThat(userHealthData.getAge()).isEqualTo(age);
        assertThat(userHealthData.getGender()).isEqualTo(gender);
        assertThat(userHealthData.getActivityLevel()).isEqualTo(activityLevel);
        assertThat(userHealthData.getTargetWeight()).isEqualTo(targetWeight);
        assertThat(userHealthData.getCreatedAt()).isEqualTo(now);
        assertThat(userHealthData.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("测试calculateBMI方法")
    void testCalculateBMI() {
        userHealthData.setWeight(70.0);
        userHealthData.setHeight(175.0);
        double bmi = userHealthData.calculateBMI();
        double expectedBMI = 70.0 / (1.75 * 1.75);
        assertThat(bmi).isEqualTo(expectedBMI);

        userHealthData.setWeight(0.0);
        userHealthData.setHeight(175.0);
        assertThat(userHealthData.calculateBMI()).isEqualTo(0.0);

        userHealthData.setWeight(70.0);
        userHealthData.setHeight(0.0);
        assertThat(userHealthData.calculateBMI()).isEqualTo(0.0);

        userHealthData.setWeight(0.0);
        userHealthData.setHeight(0.0);
        assertThat(userHealthData.calculateBMI()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("测试getBMICategory方法")
    void testGetBMICategory() {
        userHealthData.setHeight(170.0);

        userHealthData.setWeight(45.0); // BMI ≈ 15.6
        assertThat(userHealthData.getBMICategory()).isEqualTo("偏瘦");

        userHealthData.setWeight(55.0); // BMI ≈ 19.0
        assertThat(userHealthData.getBMICategory()).isEqualTo("正常");

        userHealthData.setWeight(75.0); // BMI ≈ 25.9
        assertThat(userHealthData.getBMICategory()).isEqualTo("超重");

        userHealthData.setWeight(85.0); // BMI ≈ 29.4
        assertThat(userHealthData.getBMICategory()).isEqualTo("肥胖");

        userHealthData.setWeight(0.0);
        assertThat(userHealthData.getBMICategory()).isEqualTo("偏瘦");
    }

    @Test
    @DisplayName("测试calculateBMR方法")
    void testCalculateBMR() {
        userHealthData.setWeight(70.0);
        userHealthData.setHeight(175.0);
        userHealthData.setAge(30);

        userHealthData.setGender("M");
        double bmrMale = userHealthData.calculateBMR();
        double expectedBMRMale = 10 * 70.0 + 6.25 * 175.0 - 5 * 30 + 5;
        assertThat(bmrMale).isEqualTo(expectedBMRMale);

        userHealthData.setGender("F");
        double bmrFemale = userHealthData.calculateBMR();
        double expectedBMRFemale = 10 * 70.0 + 6.25 * 175.0 - 5 * 30 - 161;
        assertThat(bmrFemale).isEqualTo(expectedBMRFemale);

        userHealthData.setGender("Other");
        assertThat(userHealthData.calculateBMR()).isEqualTo(0.0);

        userHealthData.setGender(null);
        assertThat(userHealthData.calculateBMR()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("测试calculateTDEE方法")
    void testCalculateTDEE() {
        userHealthData.setWeight(70.0);
        userHealthData.setHeight(175.0);
        userHealthData.setAge(30);
        userHealthData.setGender("M");

        userHealthData.setActivityLevel("sedentary");
        double tdeeSedentary = userHealthData.calculateTDEE();
        assertThat(tdeeSedentary).isGreaterThan(0);

        userHealthData.setActivityLevel("moderate");
        double tdeeModerate = userHealthData.calculateTDEE();
        assertThat(tdeeModerate).isGreaterThan(tdeeSedentary);

        userHealthData.setActivityLevel("very_active");
        double tdeeVeryActive = userHealthData.calculateTDEE();
        assertThat(tdeeVeryActive).isGreaterThan(tdeeModerate);

        userHealthData.setActivityLevel("unknown");
        double tdeeUnknown = userHealthData.calculateTDEE();
        assertThat(tdeeUnknown).isEqualTo(tdeeSedentary); // defaults to sedentary
    }

    @Test
    @DisplayName("测试getIdealWeightRange方法")
    void testGetIdealWeightRange() {
        userHealthData.setHeight(170.0);
        String range = userHealthData.getIdealWeightRange();
        assertThat(range).contains("kg - ");
        assertThat(range).contains(".1kg");

        userHealthData.setHeight(0.0);
        String zeroRange = userHealthData.getIdealWeightRange();
        assertThat(zeroRange).isEqualTo("0.0kg - 0.0kg");
    }

    @Test
    @DisplayName("测试getWeightDifference方法")
    void testGetWeightDifference() {
        userHealthData.setWeight(75.0);
        userHealthData.setTargetWeight(70.0);
        assertThat(userHealthData.getWeightDifference()).isEqualTo(5.0);

        userHealthData.setWeight(65.0);
        userHealthData.setTargetWeight(70.0);
        assertThat(userHealthData.getWeightDifference()).isEqualTo(-5.0);

        userHealthData.setWeight(70.0);
        userHealthData.setTargetWeight(70.0);
        assertThat(userHealthData.getWeightDifference()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("测试toString方法")
    void testToString() {
        userHealthData.setId(1);
        userHealthData.setUserId(100);
        userHealthData.setWeight(70.0);
        userHealthData.setHeight(175.0);
        userHealthData.setAge(30);
        userHealthData.setGender("M");
        userHealthData.setActivityLevel("moderate");
        userHealthData.setTargetWeight(68.0);

        String result = userHealthData.toString();

        assertThat(result).contains("UserHealthData{");
        assertThat(result).contains("id=1");
        assertThat(result).contains("userId=100");
        assertThat(result).contains("weight=70.0");
        assertThat(result).contains("height=175.0");
        assertThat(result).contains("age=30");
        assertThat(result).contains("gender='M'");
        assertThat(result).contains("activityLevel='moderate'");
        assertThat(result).contains("targetWeight=68.0");
        assertThat(result).contains("bmi=");
        assertThat(result).contains("bmr=");
        assertThat(result).contains("tdee=");
    }

    @Test
    @DisplayName("测试toString方法包含null值")
    void testToStringWithNullValues() {
        // 先设置一个非null的活动水平再设为null，避免NPE
        userHealthData.setActivityLevel("moderate");
        userHealthData.setActivityLevel(null);

        String result = userHealthData.toString();

        assertThat(result).contains("UserHealthData{");
        assertThat(result).contains("id=0");
        assertThat(result).contains("userId=0");
        assertThat(result).contains("weight=0.0");
        assertThat(result).contains("height=0.0");
        assertThat(result).contains("age=0");
        assertThat(result).contains("gender='null'");
        assertThat(result).contains("activityLevel='null'");
        assertThat(result).contains("targetWeight=0.0");
        assertThat(result).contains("bmi=0.0");
        assertThat(result).contains("bmr=0");
        assertThat(result).contains("tdee=0");
    }

    @Test
    @DisplayName("测试边界值")
    void testBoundaryValues() {
        userHealthData.setId(Integer.MIN_VALUE);
        assertThat(userHealthData.getId()).isEqualTo(Integer.MIN_VALUE);

        userHealthData.setId(Integer.MAX_VALUE);
        assertThat(userHealthData.getId()).isEqualTo(Integer.MAX_VALUE);

        userHealthData.setUserId(Integer.MIN_VALUE);
        assertThat(userHealthData.getUserId()).isEqualTo(Integer.MIN_VALUE);

        userHealthData.setAge(Integer.MIN_VALUE);
        assertThat(userHealthData.getAge()).isEqualTo(Integer.MIN_VALUE);

        userHealthData.setAge(Integer.MAX_VALUE);
        assertThat(userHealthData.getAge()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    @DisplayName("测试数值字段的边界值")
    void testNumericFieldBoundaryValues() {
        userHealthData.setWeight(Double.MIN_VALUE);
        assertThat(userHealthData.getWeight()).isEqualTo(Double.MIN_VALUE);

        userHealthData.setWeight(Double.MAX_VALUE);
        assertThat(userHealthData.getWeight()).isEqualTo(Double.MAX_VALUE);

        userHealthData.setHeight(Double.MIN_VALUE);
        assertThat(userHealthData.getHeight()).isEqualTo(Double.MIN_VALUE);

        userHealthData.setHeight(Double.MAX_VALUE);
        assertThat(userHealthData.getHeight()).isEqualTo(Double.MAX_VALUE);

        userHealthData.setTargetWeight(0.0);
        assertThat(userHealthData.getTargetWeight()).isEqualTo(0.0);

        userHealthData.setTargetWeight(-10.0);
        assertThat(userHealthData.getTargetWeight()).isEqualTo(-10.0);
    }

    @Test
    @DisplayName("测试字符串字段的边界值")
    void testStringFieldBoundaryValues() {
        userHealthData.setGender("");
        assertThat(userHealthData.getGender()).isEmpty();

        String longGender = "a".repeat(100);
        userHealthData.setGender(longGender);
        assertThat(userHealthData.getGender()).isEqualTo(longGender);

        userHealthData.setGender(null);
        assertThat(userHealthData.getGender()).isNull();

        userHealthData.setActivityLevel("");
        assertThat(userHealthData.getActivityLevel()).isEmpty();

        String longActivityLevel = "b".repeat(500);
        userHealthData.setActivityLevel(longActivityLevel);
        assertThat(userHealthData.getActivityLevel()).isEqualTo(longActivityLevel);

        userHealthData.setActivityLevel(null);
        assertThat(userHealthData.getActivityLevel()).isNull();
    }

    @Test
    @DisplayName("测试活动水平倍数")
    void testActivityMultiplier() {
        userHealthData.setWeight(70.0);
        userHealthData.setHeight(175.0);
        userHealthData.setAge(30);
        userHealthData.setGender("M");

        double baseBMR = 10 * 70.0 + 6.25 * 175.0 - 5 * 30 + 5;

        userHealthData.setActivityLevel("sedentary");
        assertThat(userHealthData.calculateTDEE()).isEqualTo(baseBMR * 1.2);

        userHealthData.setActivityLevel("light");
        assertThat(userHealthData.calculateTDEE()).isEqualTo(baseBMR * 1.375);

        userHealthData.setActivityLevel("moderate");
        assertThat(userHealthData.calculateTDEE()).isEqualTo(baseBMR * 1.55);

        userHealthData.setActivityLevel("active");
        assertThat(userHealthData.calculateTDEE()).isEqualTo(baseBMR * 1.725);

        userHealthData.setActivityLevel("very_active");
        assertThat(userHealthData.calculateTDEE()).isEqualTo(baseBMR * 1.9);
    }

    @Test
    @DisplayName("测试完整的健康数据状态")
    void testCompleteHealthDataState() {
        UserHealthData completeData = new UserHealthData(
            1, // id
            100, // userId
            70.0, // weight
            175.0, // height
            30, // age
            "M", // gender
            "moderate", // activityLevel
            68.0, // targetWeight
            now, // createdAt
            now // updatedAt
        );

        assertThat(completeData.getId()).isEqualTo(1);
        assertThat(completeData.getUserId()).isEqualTo(100);
        assertThat(completeData.getWeight()).isEqualTo(70.0);
        assertThat(completeData.getHeight()).isEqualTo(175.0);
        assertThat(completeData.getAge()).isEqualTo(30);
        assertThat(completeData.getGender()).isEqualTo("M");
        assertThat(completeData.getActivityLevel()).isEqualTo("moderate");
        assertThat(completeData.getTargetWeight()).isEqualTo(68.0);

        assertThat(completeData.calculateBMI()).isGreaterThan(0);
        assertThat(completeData.calculateBMR()).isGreaterThan(0);
        assertThat(completeData.calculateTDEE()).isGreaterThan(0);
        assertThat(completeData.getWeightDifference()).isEqualTo(2.0);
    }
}