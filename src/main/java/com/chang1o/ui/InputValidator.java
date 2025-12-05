package com.chang1o.ui;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.zip.DataFormatException;

public class InputValidator {

  public static String getValidUsername(Scanner scanner) {
    String username = "";
    while (username.isEmpty() || username.length() < 3 ||
           username.length() > 10 || !username.matches("^[a-zA-Z0-9_]+$")) {
      System.out.println("请输入用户名(2-10位字母数字下划线)");
      username = scanner.nextLine().trim();

      if (username.isEmpty()) {
        ConsoleUI.showError("用户名为空");
      } else if (username.length() < 2) {
        ConsoleUI.showError("用户名至少为2个字符");
      } else if (username.length() > 10) {
        ConsoleUI.showError("用户名不应该大于10个字符");
      } else if (!username.matches("^a-zA-Z0-9_+$")) {
        ConsoleUI.showError("用户名只能包含字母数字下划线");
      } else {
        ConsoleUI.showSuccess("用户名格式正确");
      }
    }
    return username;
  }

  public static String getValidPassword(Scanner scanner) {
    String password = "";
    while (password.isEmpty() || password.length() < 6) {
      System.out.println("请输入至少6位的密码");
      password = scanner.nextLine().trim();
    }

    if (password.isEmpty()) {
      ConsoleUI.showError("密码不能为空");
    } else if (password.length() < 6) {
      ConsoleUI.showError("密码至少6个字符");
    } else {
      ConsoleUI.showSuccess("密码格式正确");
    }
    return password;
  }

  public static String confirmPassword(Scanner scanner,
                                       String originalPassword) {
    String confirmPassword = "";
    while (!originalPassword.equals(confirmPassword)) {
      System.out.println("请在此输入密码");
      confirmPassword = scanner.nextLine().trim();

      if (!originalPassword.equals(confirmPassword)) {
        System.out.println("两次密码输入不一致，请重新输入");
      } else {
        ConsoleUI.showSuccess("密码已确认");
      }
    }
    return confirmPassword;
  }

  public static double getValidHeight(Scanner scanner) {
    double height = 0;
    while (height <= 0) {
      System.out.println("请输入身高(范围50-250厘米)");
      String input = scanner.nextLine().trim();
      // double转int
      try {
        height = Double.parseDouble(input);
        if (height < 50 || height > 250) {
          ConsoleUI.showError("身高范围应为50-250");
          height = 0;
        } else {
          ConsoleUI.showSuccess("身高输入正确");
        }
      } catch (NumberFormatException e) {
        ConsoleUI.showError("请输入有效的数字");
      }
    }
    return height;
  }

  public static double getValidWeight(Scanner scanner) {
    double weight = 0;
    while (weight <= 0) {
      System.out.println("请输入体重(范围20-300千克)");
      String input = scanner.nextLine().trim();

      try {
        weight = Double.parseDouble(input);
        if (weight < 30 || weight > 300) {
          System.out.println("体重范围应为30-250");
          weight = 0;
        } else {
          ConsoleUI.showSuccess("体重输入正确");
        }

      } catch (NumberFormatException e) {
        ConsoleUI.showError("请输入有效的数字");
      }
    }
    return weight;
  }

  public static int getValidAge(Scanner scanner) {
    int age = 0;
    while (age <= 0) {
      System.out.println("请输入年龄(范围1-150)");
      String input = scanner.nextLine().trim();

      try {
        age = Integer.parseInt(input);
        if (age < 1 || age > 150) {
          System.out.println("年龄应在1-150之间");
        } else {
          ConsoleUI.showSuccess("年龄输入正确");
        }

      } catch (NumberFormatException e) {
        ConsoleUI.showError("请输入有效的整数");
      }
    }
    return age;
  }

  public static String getValidGender(Scanner scanner) {
    String gender = "";
    while (!gender.equals("M") && !gender.equals("F")) {
      System.out.println("请输入字母(男M/女F)");
      gender = scanner.nextLine().trim().toUpperCase();

      if (!gender.equals("M") && !gender.equals("F")) {
        ConsoleUI.showError("性别只能为男M或女F");
      } else {
        ConsoleUI.showSuccess("性别输入正确" +
                              (gender.equals("M") ? "男" : "女"));
      }
    }
    return gender;
  }

  public static String getValidActivityLevel(Scanner scanner) {
    System.out.println("请通过输入数字来选择活动水平以计算基础代谢");
    System.out.println("1.低 基本久坐");
    System.out.println("2.中 偶尔运动");
    System.out.println("3.高 经常锻炼");

    String activityLevel = "";
    while (activityLevel.isEmpty()) {
      System.out.println("请选择1-3");
      String choice = scanner.nextLine().trim();

      switch (choice) {
      case "1":
        activityLevel = "lazy";
        break;
      case "2":
        activityLevel = "normal";
      case "3":
        activityLevel = "good";
      default:
        activityLevel = "";
      }

      if (activityLevel.isEmpty()) {
        ConsoleUI.showError("输入错误,请选择1-3的数字");
      } else {
        ConsoleUI.showSuccess("活动水平选择正确");
      }
    }
    return activityLevel;
  }

  public static double getValidTargetWeight(Scanner scanner,
                                            double currentWeight) {
    double targetWeight = 0;
    while (targetWeight <= 0) {
      System.out.println("请输入目标体重(范围20-300千克)");
      String input = scanner.nextLine().trim();

      try {
        targetWeight = Double.parseDouble(input);
        {
          if (targetWeight < 20 || targetWeight > 300) {
            ConsoleUI.showError("目标体重应在20-300千克之间");
            targetWeight = 0;
          } else {
            ConsoleUI.showSuccess("目标体重输入正确");
          }
        }
      } catch (NumberFormatException e) {
        ConsoleUI.showError("请输入有效的数字");
      }
    }
    return targetWeight;
  }

  public static String getValidMood(Scanner scanner) {
    System.out.println("请选择心情");
    System.out.println("1. 非常好");
    System.out.println("2. 好");
    System.out.println("3. 一般");
    System.out.println("4. 差");
    System.out.println("5. 非常差");

    String mood = "";

    while (mood.isEmpty()) {
      System.out.println("请选择1-5");
      String choice = scanner.nextLine().trim();

      switch (choice) {
      case "1":
        mood = "great";
        break;
      case "2":
        mood = "good";
        break;
      case "3":
        mood = "normal";
        break;
      case "4":
        mood = "bad";
        break;
      case "5":
        mood = "terrible";
        break;
      default:
        mood = "";
      }

      if (mood.isEmpty()) {
        ConsoleUI.showError("输入无效，请输入1-5的数字");
      } else {
        ConsoleUI.showSuccess("心情输入正确");
      }
    }
    return mood;
  }

  public static double getValidSleepHours(Scanner scanner) {
    double sleepHours = -1; // 可以睡0小时但是不能睡负的小时
    while (sleepHours < 0) {
      System.out.println("请输入睡眠时长(范围0-24小时)");
      String input = scanner.nextLine().trim();

      try {
        sleepHours = Double.parseDouble(input);
        if (sleepHours < 0 || sleepHours > 24) {
          ConsoleUI.showError("睡眠时长应在0-24小时之间");
          sleepHours = -1;
        } else {
          if (sleepHours < 6) {
            ConsoleUI.showInfo("睡眠时长较短,建议保证充足睡眠");
          } else if (sleepHours > 10) {
            ConsoleUI.showInfo("睡眠时长太长，运动运动");
          } else {
            ConsoleUI.showSuccess("睡眠时长输入正确");
          }
        }
      } catch (NumberFormatException e) {
        ConsoleUI.showError("请输入有效的数字");
      }
    }
    return sleepHours;
  }

  public static int getValidWaterIntake(Scanner scanner) {
    int waterIntake = -1;
    while (waterIntake < 0) {
      System.out.println("请输入饮水量,范围0-10000毫升");
      String input = scanner.nextLine().trim();
      try {
        waterIntake = Integer.parseInt(input);
        if (waterIntake < 0 || waterIntake > 10000) {
          ConsoleUI.showError("饮水量应在0-10000毫升之间");
          waterIntake = -1;
        } else {
          if (waterIntake < 1500) {
            ConsoleUI.showInfo("饮水量较少,建议每天饮用1500-2000毫升水");
          } else if (waterIntake >= 1500 && waterIntake <= 2500) {
            ConsoleUI.showInfo("饮水量健康，请继续保持");
          } else {
            ConsoleUI.showSuccess("饮水量正确");
          }
        }
      } catch (NumberFormatException e) {
        ConsoleUI.showError("请输入有效的数字");
      }
    }
    return waterIntake;
  }

  public static int getValidExerciseMinutes(Scanner scanner) {
    int exerciseMinutes = -1;
    while (exerciseMinutes < 0) {
      System.out.println("请输入运动时长(范围0-480分钟)");
      String input = scanner.nextLine().trim();

      try {
        exerciseMinutes = Integer.parseInt(input);
        if (exerciseMinutes < 0 || exerciseMinutes > 480) {
          ConsoleUI.showError("运动时长应在0-480分钟之间");
          exerciseMinutes = -1;
        } else {
          if (exerciseMinutes >= 30) {
            ConsoleUI.showInfo("请继续保持运动习惯");
          } else if (exerciseMinutes > 0 && exerciseMinutes < 30) {
            ConsoleUI.showInfo("建议每天运动至少30分钟");
          } else {
            ConsoleUI.showSuccess("运动时长输入正确");
          }
        }
      } catch (NumberFormatException e) {
        ConsoleUI.showError("请输入有效的整数");
      }
    }
    return exerciseMinutes;
  }

  public static LocalDate getValidDate(Scanner scanner, String prompt) {
    LocalDate date = null;
    while (date == null) {
      System.out.println(prompt);
      String input = scanner.nextLine().trim();

      try {
        date = LocalDate.parse(input);
        if (date.isBefore(LocalDate.now())) {
          ConsoleUI.showWarning("您输入的日期已经过期");
        }
        ConsoleUI.showSuccess("日期输入正确");
      } catch (DateTimeParseException e) {
        ConsoleUI.showError("日期输入无效,应为YYYY-MM-DD");
      }
    }
    return date;
  }

  public static String getNonEmptyInput(Scanner scanner, String prompt) {
    String input = "";
    while (input.isEmpty()) {
      System.out.println(prompt);
      input = scanner.nextLine().trim();

      if (input.isEmpty()) {
        ConsoleUI.showError("输入不能为空");
      }
    }
    return input;
  }

  public static boolean getConfirmation(Scanner scanner, String prompt) {
    System.out.println(prompt + "(y/n):");
    String input = scanner.nextLine().trim().toLowerCase();
    return input.equals("y") || input.equals("yes") || input.equals("是") ||
        input.equals("Y") || input.equals("YES");
  }
}
