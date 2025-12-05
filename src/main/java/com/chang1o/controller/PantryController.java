
package com.chang1o.controller;

import com.chang1o.model.User;
import com.chang1o.model.PantryItem;
import com.chang1o.service.PantryService;
import com.chang1o.ui.ConsoleUI;
import com.chang1o.ui.MenuManager;
import java.util.Scanner;
import java.util.List;
import java.time.LocalDate;

public class PantryController extends BaseController {

    private final PantryService pantryService;

    public PantryController() {
        this.pantryService = new PantryService();
    }

    public void showMenu(User currentUser) {
        boolean inPantryMenu = true;

        while (inPantryMenu) {
            MenuManager.showPantryMenu();

            System.out.print("请输入您的选择：");
            String choice = sessionManager.getScanner().nextLine().trim();

            switch (choice) {
                case "1":
                    addPantryItem(currentUser);
                    break;
                case "2":
                    viewPantry(currentUser);
                    break;
                case "3":
                    updatePantryItem(currentUser);
                    break;
                case "4":
                    deletePantryItem(currentUser);
                    break;
                case "5":
                    checkExpiringItems(currentUser);
                    break;
                case "6":
                    viewExpiredItems(currentUser);
                    break;
                case "0":
                    inPantryMenu = false;
                    break;
                default:
                    System.out.println("无效的选择，请重新输入！");
                    break;
            }

            if (inPantryMenu) {
                ConsoleUI.pause(sessionManager.getScanner());
            }
        }
    }

    private void addPantryItem(User currentUser) {
        System.out.println("\n添加食品到库存");
        System.out.println("-".repeat(30));

        System.out.print("请输入食品名称：");
        String name = sessionManager.getScanner().nextLine().trim();

        System.out.print("请输入数量：");
        String quantity = sessionManager.getScanner().nextLine().trim();

        System.out.print("请输入保质期(YYYY-MM-DD)：");
        String expiryDateStr = sessionManager.getScanner().nextLine().trim();

        try {
            LocalDate expiryDate = LocalDate.parse(expiryDateStr);

            PantryService.PantryResult result = pantryService.addPantryItem(
                currentUser.getId(), name, quantity, expiryDate);

            if (result.isSuccess()) {
                System.out.println("食品添加成功！");
                System.out.println("消息：" + result.getMessage());
            } else {
                System.out.println("添加失败：" + result.getMessage());
            }
        } catch (Exception e) {
            System.out.println("输入格式有误，请检查！");
        }
    }

    private void viewPantry(User currentUser) {
        System.out.println();
        System.out.println("┌──────────────────────────────────────────────────┐");
        System.out.println("│                   当前库存                       │");
        System.out.println("└──────────────────────────────────────────────────┘");
        System.out.println();

        List<PantryItem> items = pantryService.getPantryItemsByUser(currentUser.getId());

        if (items.isEmpty()) {
            System.out.println("[提示] 您的库存为空！");
            System.out.println("[建议] 您可以添加食品到库存中");
        } else {
            System.out.println("您的库存共有 " + items.size() + " 件物品：");
            System.out.println();
            for (int i = 0; i < items.size(); i++) {
                PantryItem item = items.get(i);
                System.out.println((i + 1) + ". " +
                    (item.getIngredient() != null ? item.getIngredient().getName() : "未知食材") +
                    " (ID: " + item.getId() + ")");
                System.out.println("   数量：" + item.getQuantity());
                if (item.getExpiryDate() != null) {
                    System.out.println("   保质期：" + item.getExpiryDate());
                    long daysLeft = item.getExpiryDate().toEpochDay() - LocalDate.now().toEpochDay();
                    if (daysLeft < 0) {
                        System.out.println("   [警告] 已过期 " + Math.abs(daysLeft) + " 天");
                    } else if (daysLeft <= 7) {
                        System.out.println("   [警告] 剩余 " + daysLeft + " 天过期");
                    } else {
                        System.out.println("   [正常] 剩余 " + daysLeft + " 天");
                    }
                } else {
                    System.out.println("   保质期：无");
                }
                System.out.println("   " + "─".repeat(40));
            }
        }
    }

    private void updatePantryItem(User currentUser) {
        System.out.println("\n更新食品信息");
        System.out.println("-".repeat(30));

        List<PantryItem> userPantryItems = pantryService.getPantryItemsByUser(currentUser.getId());

        if (userPantryItems.isEmpty()) {
            System.out.println("您的库存为空，无法更新！");
            return;
        }

        System.out.println("您的库存列表：");
        for (int i = 0; i < userPantryItems.size(); i++) {
            PantryItem item = userPantryItems.get(i);
            System.out.println((i + 1) + ". " +
                (item.getIngredient() != null ? item.getIngredient().getName() : "未知食材") +
                " (ID: " + item.getId() + ")");
            System.out.println("   数量：" + item.getQuantity());
            if (item.getExpiryDate() != null) {
                System.out.println("   保质期：" + item.getExpiryDate());
            }
        }
        System.out.println("-".repeat(30));

        System.out.print("请选择要更新的库存编号（1-" + userPantryItems.size() + "）：");
        String choiceStr = sessionManager.getScanner().nextLine().trim();

        try {
            int choice = Integer.parseInt(choiceStr);

            if (choice < 1 || choice > userPantryItems.size()) {
                System.out.println("无效的选择，请输入1-" + userPantryItems.size() + "之间的数字！");
                return;
            }

            PantryItem item = userPantryItems.get(choice - 1);
            int itemId = item.getId();

            System.out.println("\n当前库存信息：");
            System.out.println("食材：" + (item.getIngredient() != null ? item.getIngredient().getName() : "未知"));
            System.out.println("数量：" + item.getQuantity());
            System.out.println("保质期：" + (item.getExpiryDate() != null ? item.getExpiryDate() : "未设置"));

            System.out.println("\n请输入新的信息（直接回车保持不变）：");

            System.out.print("新数量：");
            String newQuantity = sessionManager.getScanner().nextLine().trim();
            if (!newQuantity.isEmpty()) {
                item.setQuantity(newQuantity);
            }

            System.out.print("新保质期(YYYY-MM-DD)：");
            String newExpiryStr = sessionManager.getScanner().nextLine().trim();
            if (!newExpiryStr.isEmpty()) {
                try {
                    LocalDate newExpiryDate = LocalDate.parse(newExpiryStr);
                    if (newExpiryDate.isBefore(LocalDate.now())) {
                        System.out.println("[警告] 您输入的日期已经过期");
                    }
                    item.setExpiryDate(newExpiryDate);
                } catch (Exception e) {
                    System.out.println("[错误] 日期格式无效，保持原日期。正确格式：YYYY-MM-DD");
                }
            }

            PantryService.PantryResult result = pantryService.updatePantryItem(
                itemId, currentUser.getId(), item.getQuantity(), item.getExpiryDate());

            if (result.isSuccess()) {
                System.out.println("[成功] 库存信息更新成功！");
            } else {
                System.out.println("[错误] 更新失败：" + result.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("[错误] 请输入有效的数字！");
        }
    }

    private void deletePantryItem(User currentUser) {
        System.out.println("\n删除库存食品");
        System.out.println("-".repeat(30));

        List<PantryItem> userPantryItems = pantryService.getPantryItemsByUser(currentUser.getId());

        if (userPantryItems.isEmpty()) {
            System.out.println("您的库存为空，无法删除！");
            return;
        }

        System.out.println("您的库存列表：");
        for (int i = 0; i < userPantryItems.size(); i++) {
            PantryItem item = userPantryItems.get(i);
            System.out.println((i + 1) + ". " +
                (item.getIngredient() != null ? item.getIngredient().getName() : "未知食材") +
                " (ID: " + item.getId() + ")");
            System.out.println("   数量：" + item.getQuantity());
            if (item.getExpiryDate() != null) {
                long daysLeft = item.getExpiryDate().toEpochDay() - LocalDate.now().toEpochDay();
                if (daysLeft < 0) {
                    System.out.println("   [警告] 已过期 " + Math.abs(daysLeft) + " 天");
                } else {
                    System.out.println("   保质期：" + item.getExpiryDate());
                }
            }
        }
        System.out.println("─".repeat(50));

        System.out.print("请选择要删除的库存编号（1-" + userPantryItems.size() + "）：");
        String choiceStr = sessionManager.getScanner().nextLine().trim();

        try {
            int choice = Integer.parseInt(choiceStr);

            if (choice < 1 || choice > userPantryItems.size()) {
                System.out.println("无效的选择，请输入1-" + userPantryItems.size() + "之间的数字！");
                return;
            }

            PantryItem selectedItem = userPantryItems.get(choice - 1);
            int itemId = selectedItem.getId();
            String itemName = selectedItem.getIngredient() != null ? selectedItem.getIngredient().getName() : "未知食材";

            System.out.print("确定要删除《" + itemName + "》吗？(y/n)：");
            String confirm = sessionManager.getScanner().nextLine().trim().toLowerCase();

            if (confirm.equals("y") || confirm.equals("yes") || confirm.equals("是")) {
                PantryService.PantryResult result = pantryService.deletePantryItem(itemId, currentUser.getId());

                if (result.isSuccess()) {
                    System.out.println("[成功] 食品删除成功！");
                } else {
                    System.out.println("[错误] 删除失败：" + result.getMessage());
                }
            } else {
                System.out.println("已取消删除操作");
            }
        } catch (NumberFormatException e) {
            System.out.println("[错误] 请输入有效的数字！");
        }
    }

    private void checkExpiringItems(User currentUser) {
        System.out.println();
        System.out.println("┌──────────────────────────────────────────────────┐");
        System.out.println("│                即将过期食品                      │");
        System.out.println("└──────────────────────────────────────────────────┘");
        System.out.println();

        List<PantryItem> items = pantryService.getExpiringItems(currentUser.getId(), 7);

        if (items.isEmpty()) {
            System.out.println("[正常] 近期没有即将过期的食品！");
        } else {
            System.out.println("[警告] 以下食品即将过期（7天内）：");
            System.out.println();
            for (int i = 0; i < items.size(); i++) {
                PantryItem item = items.get(i);
                long daysLeft = item.getExpiryDate().toEpochDay() - LocalDate.now().toEpochDay();
                System.out.println((i + 1) + ". " +
                    (item.getIngredient() != null ? item.getIngredient().getName() : "未知食材") +
                    " - 剩余 " + daysLeft + " 天 (保质期：" + item.getExpiryDate() + ")");
                System.out.println("   数量：" + item.getQuantity());
            }
            System.out.println();
            System.out.println("[建议] 建议优先使用这些即将过期的食品！");
        }
    }

    private void viewExpiredItems(User currentUser) {
        System.out.println();
        System.out.println("┌──────────────────────────────────────────────────┐");
        System.out.println("│                 已过期食品                       │");
        System.out.println("└──────────────────────────────────────────────────┘");
        System.out.println();

        List<PantryItem> items = pantryService.getExpiredItems(currentUser.getId());

        if (items.isEmpty()) {
            System.out.println("[正常] 没有已过期的食品！");
        } else {
            System.out.println("[警告] 以下食品已过期，建议清理：");
            System.out.println();
            for (int i = 0; i < items.size(); i++) {
                PantryItem item = items.get(i);
                long daysExpired = LocalDate.now().toEpochDay() - item.getExpiryDate().toEpochDay();
                System.out.println((i + 1) + ". " +
                    (item.getIngredient() != null ? item.getIngredient().getName() : "未知食材") +
                    " - 已过期 " + daysExpired + " 天 (保质期：" + item.getExpiryDate() + ")");
                System.out.println("   数量：" + item.getQuantity());
            }
            System.out.println();
            System.out.println("[建议] 建议及时清理已过期的食品，确保食品安全！");
        }
    }
}
