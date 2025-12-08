package com.chang1o.ui;

import com.chang1o.model.User;

public class MenuManager {

  public static void showMainMenu(User currentUser) {
    System.out.println();
    System.out.println("RecipeTracker 主菜单");
    System.out.println();

    if (currentUser == null) {
      System.out.println("  [用户管理]");
      System.out.println("    1. 用户注册");
      System.out.println("    2. 用户登录");
      System.out.println();
      System.out.println("  [系统信息]");
      System.out.println("    9. 关于系统");
      System.out.println("    0. 退出程序");
    } else {
      ConsoleUI.showUserStatus(currentUser);
      System.out.println("  [用户管理]");
      System.out.println("    3. 用户登出");
      System.out.println();
      System.out.println("  [内容管理]");
      System.out.println("    4. 食谱管理");
      System.out.println("    5. 库存管理");
      System.out.println();
      System.out.println("  [健康管理]");
      System.out.println("    6. 健康管理");
      System.out.println();
      System.out.println("  [智能功能]");
      System.out.println("    7. AI推荐");
      System.out.println();
      System.out.println("  [数据管理]");
      System.out.println("    8. 数据导出");
      System.out.println();
      System.out.println("  [系统信息]");
      System.out.println("    9. 关于系统");
      System.out.println("    0. 退出程序");
    }

    System.out.println();
    ConsoleUI.showSeparator();
  }

  public static void showRecipeMenu() {
    System.out.println();
    System.out.println("食谱管理");
    System.out.println();
    System.out.println("  1. 添加新食谱");
    System.out.println("  2. 查看我的食谱");
    System.out.println("  3. 搜索食谱");
    System.out.println("  4. 按分类浏览食谱");
    System.out.println("  5. 编辑食谱");
    System.out.println("  6. 删除食谱");
    System.out.println("  7. 查看所有食谱");
    System.out.println("  0. 返回主菜单");
    System.out.println();
    ConsoleUI.showSeparator();
  }

  public static void showPantryMenu() {
    System.out.println();
    System.out.println("库存管理");
    System.out.println();
    System.out.println("  1. 添加食品到库存");
    System.out.println("  2. 查看当前库存");
    System.out.println("  3. 更新食品信息");
    System.out.println("  4. 删除库存食品");
    System.out.println("  5. 检查即将过期食品");
    System.out.println("  6. 查看已过期食品");
    System.out.println("  0. 返回主菜单");
    System.out.println();
    ConsoleUI.showSeparator();
  }

  public static void showHealthMenu() {
    ConsoleUI.clearScreen();
    System.out.println("健康管理");
    System.out.println();
    System.out.println("  [健康数据管理]");
    System.out.println("    1. 管理健康数据");
    System.out.println("    2. 每日打卡");
    System.out.println();
    System.out.println("  [健康报告]");
    System.out.println("    3. 查看健康报告");
    System.out.println("    4. 健康统计分析");
    System.out.println();
    System.out.println("    0. 返回主菜单");
    System.out.println();
    ConsoleUI.showSeparator();
  }

  public static void showAIMenu() {
    System.out.println();
    System.out.println("AI推荐");
    System.out.println();
    System.out.println("请选择推荐类型：");
    System.out.println("  1. 个性化健康建议");
    System.out.println("  2. 智能食谱推荐");
    System.out.println("  3. 智能购物清单");
    System.out.println("  4. 营养分析报告");
    System.out.println("  0. 返回主菜单");
    System.out.println();
    ConsoleUI.showSeparator();
  }
}
