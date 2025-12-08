
package com.chang1o.controller;

import com.chang1o.model.User;
import com.chang1o.model.UserHealthData;
import com.chang1o.service.HealthDataService;
import com.chang1o.dao.DailyCheckInDao;
import com.chang1o.ui.ConsoleUI;
import com.chang1o.ui.InputValidator;
import com.chang1o.ui.MenuManager;
import java.util.Scanner;

public class HealthController extends BaseController {

    private final HealthDataService healthDataService;

    public HealthController() {
        this.healthDataService = new HealthDataService();
    }

    public void showMenu(User currentUser) {
        boolean inHealthMenu = true;

        while (inHealthMenu) {
            MenuManager.showHealthMenu();

            System.out.print("请输入您的选择：");
            String choice = sessionManager.getScanner().nextLine().trim();

            switch (choice) {
                case "1":
                    manageHealthData(currentUser);
                    break;
                case "2":
                    dailyCheckIn(currentUser);
                    break;
                case "3":
                    showHealthReport(currentUser);
                    break;
                case "4":
                    showHealthStatistics(currentUser);
                    break;
                case "0":
                    inHealthMenu = false;
                    break;
                default:
                    System.out.println("[错误] 无效的选择，请输入 0-4 之间的数字！");
                    break;
            }

            if (inHealthMenu) {
                ConsoleUI.pause(sessionManager.getScanner());
            }
        }
    }

    public void manageHealthData(User currentUser) {
        ConsoleUI.showTitleBox("管理健康数据");

        double heightNum = InputValidator.getValidHeight(sessionManager.getScanner());
        double weightNum = InputValidator.getValidWeight(sessionManager.getScanner());
        int ageNum = InputValidator.getValidAge(sessionManager.getScanner());
        String gender = InputValidator.getValidGender(sessionManager.getScanner());
        String activityLevel = InputValidator.getValidActivityLevel(sessionManager.getScanner());
        double targetWeightNum = InputValidator.getValidTargetWeight(sessionManager.getScanner(), weightNum);

        try {
            HealthDataService.HealthDataResult result = healthDataService.saveHealthData(
                currentUser.getId(), weightNum, heightNum, ageNum, gender, activityLevel, targetWeightNum);

            if (result.isSuccess()) {
                ConsoleUI.showSuccess("健康数据保存成功！");
                System.out.println("消息：" + result.getMessage());

                System.out.println("\n您的健康指标：");
                double bmi = weightNum / Math.pow(heightNum / 100, 2);
                System.out.printf("BMI指数：%.1f\n", bmi);

                double bmr;
                if (gender.equals("M")) {
                    bmr = 10 * weightNum + 6.25 * heightNum - 5 * ageNum + 5;
                } else {
                    bmr = 10 * weightNum + 6.25 * heightNum - 5 * ageNum - 161;
                }
                System.out.printf("基础代谢率：%.0f 卡路里/天\n", bmr);
            } else {
                ConsoleUI.showError("保存失败：" + result.getMessage());
            }
        } catch (Exception e) {
            ConsoleUI.showError("数据处理失败，请稍后重试");
        }
    }

    public void dailyCheckIn(User currentUser) {
        ConsoleUI.showTitleBox("每日打卡");

        String mood = InputValidator.getValidMood(sessionManager.getScanner());
        double sleepHoursNum = InputValidator.getValidSleepHours(sessionManager.getScanner());
        int waterIntakeNum = InputValidator.getValidWaterIntake(sessionManager.getScanner());
        int exerciseMinutesNum = InputValidator.getValidExerciseMinutes(sessionManager.getScanner());

        System.out.print("请输入今日备注(可选)：");
        String notes = sessionManager.getScanner().nextLine().trim();

        try {
            HealthDataService.CheckInResult result = healthDataService.saveDailyCheckIn(
                currentUser.getId(), mood, sleepHoursNum, waterIntakeNum, exerciseMinutesNum, notes);

            if (result.isSuccess()) {
                ConsoleUI.showSuccess("打卡成功！");
                System.out.println("消息：" + result.getMessage());

                int healthScore = calculateHealthScore(sleepHoursNum, waterIntakeNum, exerciseMinutesNum, mood);
                System.out.println("\n今日健康评分：" + healthScore + "/100");

                if (healthScore >= 80) {
                    System.out.println("[很好] 保持良好的生活习惯！");
                } else if (healthScore >= 60) {
                    System.out.println("[提示] 继续努力，保持健康生活方式");
                } else {
                    System.out.println("[建议] 建议改善睡眠、饮水和运动习惯");
                }
            } else {
                ConsoleUI.showError("打卡失败：" + result.getMessage());
            }
        } catch (Exception e) {
            ConsoleUI.showError("数据处理失败，请稍后重试");
        }
    }

    public void showHealthReport(User currentUser) {
        ConsoleUI.clearScreen();
        ConsoleUI.showTitleBox("健康报告");

        UserHealthData healthData = healthDataService.getLatestHealthData(currentUser.getId());

        if (healthData != null) {
            System.out.printf("   身高：%.0f cm\n", healthData.getHeight());
            System.out.printf("   体重：%.1f kg\n", healthData.getWeight());
            System.out.println("   年龄：" + healthData.getAge() + " 岁");
            System.out.println("   性别：" + ("M".equals(healthData.getGender()) ? "男" : "女"));

            double bmi = healthData.calculateBMI();
            System.out.println();
            System.out.println("【健康指标】");
            System.out.printf("   BMI指数：%.1f (%s)\n", bmi, healthData.getBMICategory());
            System.out.printf("   基础代谢率：%.0f 卡路里/天\n", healthData.calculateBMR());
            System.out.printf("   每日能量消耗：%.0f 卡路里/天\n", healthData.calculateTDEE());

            if (healthData.getTargetWeight() > 0) {
                System.out.println();
                System.out.println("【目标体重】");
                double diff = healthData.getWeightDifference();
                System.out.printf("   当前体重：%.1f kg\n", healthData.getWeight());
                System.out.printf("   目标体重：%.1f kg\n", healthData.getTargetWeight());
                System.out.printf("   还需%s：%.1f kg\n", diff > 0 ? "减重" : "增重", Math.abs(diff));
            }
        } else {
            ConsoleUI.showInfo("暂无健康数据，请先录入健康信息");
        }
    }

    public void showHealthStatistics(User currentUser) {
        ConsoleUI.showTitleBox("健康统计分析");

        System.out.print("请输入统计天数(默认30天)：");
        String daysStr = sessionManager.getScanner().nextLine().trim();

        int days = 30;
        if (!daysStr.isEmpty()) {
            try {
                days = Integer.parseInt(daysStr);
            } catch (NumberFormatException e) {
                ConsoleUI.showWarning("输入无效，使用默认30天");
            }
        }

        DailyCheckInDao.HealthStatistics stats = healthDataService.getHealthStatistics(currentUser.getId(), days);

        System.out.println("\n【过去" + days + "天的健康统计】");
        System.out.println("平均健康评分：" + String.format("%.1f", stats.getAvgHealthScore()) + "/100");
        System.out.println("连续打卡天数：" + stats.getConsecutiveDays() + "天");
        System.out.println("平均睡眠时长：" + String.format("%.1f", stats.getAvgSleepHours()) + "小时");
        System.out.println("平均饮水量：" + stats.getAvgWaterIntake() + "毫升");
        System.out.println("平均运动时长：" + stats.getAvgExerciseMinutes() + "分钟");
        System.out.println("平均心情评分：" + String.format("%.1f", stats.getAvgMoodScore()) + "/5");
    }

    private int calculateHealthScore(double sleepHours, int waterIntake, int exerciseMinutes, String mood) {
        int score = 0;

        if (sleepHours >= 7 && sleepHours <= 9) {
            score += 30;
        } else if (sleepHours >= 6 && sleepHours < 7) {
            score += 20;
        } else if (sleepHours >= 5 && sleepHours < 6) {
            score += 10;
        }

        if (waterIntake >= 2000) {
            score += 25;
        } else if (waterIntake >= 1500) {
            score += 20;
        } else if (waterIntake >= 1000) {
            score += 15;
        }

        if (exerciseMinutes >= 30) {
            score += 25;
        } else if (exerciseMinutes >= 20) {
            score += 20;
        } else if (exerciseMinutes >= 10) {
            score += 15;
        }

        switch (mood) {
            case "great": score += 20; break;
            case "good": score += 15; break;
            case "normal": score += 10; break;
            case "bad": score += 5; break;
            case "terrible": score += 0; break;
        }

        return score;
    }

    private String getMoodDescription(String mood) {
        switch (mood) {
            case "great": return "非常好";
            case "good": return "好";
            case "normal": return "一般";
            case "bad": return "差";
            case "terrible": return "非常差";
            default: return "未知";
        }
    }
}
