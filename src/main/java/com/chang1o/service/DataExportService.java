package com.chang1o.service;

import com.chang1o.model.UserHealthData;
import com.chang1o.model.DailyCheckIn;
import com.chang1o.model.Recipe;
import com.chang1o.model.PantryItem;
import com.chang1o.dao.UserHealthDataDao;
import com.chang1o.dao.DailyCheckInDao;
import com.chang1o.service.RecipeService;
import com.chang1o.service.PantryService;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class DataExportService {

    private static final String EXPORT_DIR = "exports/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private UserHealthDataDao healthDataDao;
    private DailyCheckInDao dailyCheckInDao;
    private RecipeService recipeService;
    private PantryService pantryService;

    public DataExportService() {
        this.healthDataDao = new UserHealthDataDao();
        this.dailyCheckInDao = new DailyCheckInDao();
        this.recipeService = new RecipeService();
        this.pantryService = new PantryService();
        
        ensureExportDirectoryExists();
    }

    public String exportHealthDataReport(int userId) {
        try {
            String fileName = "health_report_" + userId + "_" + LocalDate.now().format(DATE_FORMATTER) + ".txt";
            String filePath = EXPORT_DIR + fileName;
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write("=== 个人健康数据报告 ===\n");
                writer.write("生成时间: " + LocalDateTime.now().format(DATETIME_FORMATTER) + "\n");
                writer.write("用户ID: " + userId + "\n");
                writer.write("=".repeat(50) + "\n\n");
                
                writeHealthDataSection(writer, userId);
                
                writeCheckInStatisticsSection(writer, userId);
                
                writeHealthAdviceSection(writer, userId);
                
                writer.write("\n" + "=".repeat(50) + "\n");
                writer.write("报告生成完成 - RecipeTracker系统\n");
            }
            
            return filePath;
            
        } catch (IOException e) {
            System.err.println("导出健康数据报告时发生错误: " + e.getMessage());
            return null;
        }
    }

    public String exportCheckInRecords(int userId, int days) {
        try {
            String fileName = "checkin_records_" + userId + "_" + LocalDate.now().format(DATE_FORMATTER) + ".csv";
            String filePath = EXPORT_DIR + fileName;
            
            List<DailyCheckIn> checkIns = dailyCheckInDao.getRecentCheckIns(userId, days);
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write("日期,心情,睡眠时长(小时),饮水量(ml),运动时长(分钟),健康评分,备注\n");
                
                for (DailyCheckIn checkIn : checkIns) {
                    writer.write(String.format("%s,%s,%.1f,%d,%d,%d,\"%s\"\n",
                        checkIn.getCheckInDate().format(DATE_FORMATTER),
                        checkIn.getMoodDescription(),
                        checkIn.getSleepHours(),
                        checkIn.getWaterIntake(),
                        checkIn.getExerciseMinutes(),
                        checkIn.getHealthScore(),
                        checkIn.getNotes() != null ? checkIn.getNotes() : ""
                    ));
                }
            }
            
            return filePath;
            
        } catch (IOException e) {
            System.err.println("导出打卡记录时发生错误: " + e.getMessage());
            return null;
        }
    }

    public String exportRecipeData(int userId) {
        try {
            String fileName = "recipe_data_" + userId + "_" + LocalDate.now().format(DATE_FORMATTER) + ".txt";
            String filePath = EXPORT_DIR + fileName;
            
            List<Recipe> recipes = recipeService.getRecipesByUser(userId);
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write("=== 个人食谱数据 ===\n");
                writer.write("生成时间: " + LocalDateTime.now().format(DATETIME_FORMATTER) + "\n");
                writer.write("食谱总数: " + recipes.size() + "个\n");
                writer.write("=".repeat(50) + "\n\n");
                
                for (int i = 0; i < recipes.size(); i++) {
                    Recipe recipe = recipes.get(i);
                    writer.write((i + 1) + ". " + recipe.getName() + "\n");
                    if (recipe.getCategory() != null) {
                        writer.write("   分类: " + recipe.getCategory().getName() + "\n");
                    }
                    writer.write("   制作步骤: " + recipe.getInstructions() + "\n");
                    writer.write("\n");
                }
            }
            
            return filePath;
            
        } catch (IOException e) {
            System.err.println("导出食谱数据时发生错误: " + e.getMessage());
            return null;
        }
    }

    public String exportPantryData(int userId) {
        try {
            String fileName = "pantry_data_" + userId + "_" + LocalDate.now().format(DATE_FORMATTER) + ".csv";
            String filePath = EXPORT_DIR + fileName;
            
            List<PantryItem> items = pantryService.getPantryItemsByUser(userId);
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write("食材名称,数量,过期日期,状态\n");
                
                for (PantryItem item : items) {
                    String status = "正常";
                    if (item.isExpired()) {
                        status = "已过期";
                    } else if (item.getDaysUntilExpiry() <= 7) {
                        status = "即将过期";
                    }
                    
                    writer.write(String.format("%s,\"%s\",%s,%s\n",
                        item.getIngredient().getName(),
                        item.getQuantity(),
                        item.getExpiryDate() != null ? item.getExpiryDate().format(DATE_FORMATTER) : "无",
                        status
                    ));
                }
            }
            
            return filePath;
            
        } catch (IOException e) {
            System.err.println("导出库存数据时发生错误: " + e.getMessage());
            return null;
        }
    }

    public String exportComprehensiveHealthReport(int userId) {
        try {
            String fileName = "comprehensive_report_" + userId + "_" + LocalDate.now().format(DATE_FORMATTER) + ".html";
            String filePath = EXPORT_DIR + fileName;
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write("<!DOCTYPE html>\n");
                writer.write("<html lang=\"zh-CN\">\n");
                writer.write("<head>\n");
                writer.write("    <meta charset=\"UTF-8\">\n");
                writer.write("    <title>个人健康综合报告</title>\n");
                writer.write("    <style>\n");
                writer.write("        body { font-family: Arial, sans-serif; margin: 20px; }\n");
                writer.write("        .header { background-color: #f0f0f0; padding: 20px; border-radius: 5px; }\n");
                writer.write("        .section { margin: 20px 0; padding: 15px; border: 1px solid #ddd; border-radius: 5px; }\n");
                writer.write("        .metric { display: inline-block; margin: 10px; padding: 10px; background-color: #e8f4f8; border-radius: 3px; }\n");
                writer.write("        table { width: 100%; border-collapse: collapse; margin: 10px 0; }\n");
                writer.write("        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
                writer.write("        th { background-color: #f2f2f2; }\n");
                writer.write("    </style>\n");
                writer.write("</head>\n");
                writer.write("<body>\n");
                
                writer.write("<div class=\"header\">\n");
                writer.write("    <h1>个人健康综合报告</h1>\n");
                writer.write("    <p>生成时间: " + LocalDateTime.now().format(DATETIME_FORMATTER) + "</p>\n");
                writer.write("    <p>用户ID: " + userId + "</p>\n");
                writer.write("</div>\n");
                
                writeHtmlHealthDataSection(writer, userId);
                
                writeHtmlCheckInSection(writer, userId);
                
                writeHtmlRecipeSection(writer, userId);
                
                writeHtmlPantrySection(writer, userId);
                
                writer.write("</body>\n");
                writer.write("</html>\n");
            }
            
            return filePath;
            
        } catch (IOException e) {
            System.err.println("导出综合健康报告时发生错误: " + e.getMessage());
            return null;
        }
    }

    private void writeHealthDataSection(BufferedWriter writer, int userId) throws IOException {
        UserHealthData healthData = healthDataDao.getLatestHealthDataByUserId(userId);
        
        if (healthData != null) {
            writer.write("健康数据:\n");
            writer.write("- 身高: " + healthData.getHeight() + "cm\n");
            writer.write("- 体重: " + healthData.getWeight() + "kg\n");
            writer.write("- BMI: " + String.format("%.1f", healthData.calculateBMI()) + " (" + healthData.getBMICategory() + ")\n");
            writer.write("- 基础代谢率: " + String.format("%.0f", healthData.calculateBMR()) + " 卡路里/天\n");
            writer.write("- 每日总能量消耗: " + String.format("%.0f", healthData.calculateTDEE()) + " 卡路里/天\n");
            writer.write("\n");
        } else {
            writer.write("暂无健康数据\n\n");
        }
    }

    private void writeCheckInStatisticsSection(BufferedWriter writer, int userId) throws IOException {
        DailyCheckInDao.HealthStatistics stats = dailyCheckInDao.getHealthStatistics(userId, 30);
        
        writer.write("打卡统计 (最近30天):\n");
        writer.write("- 平均健康评分: " + String.format("%.1f", stats.getAvgHealthScore()) + "/100\n");
        writer.write("- 平均睡眠时长: " + String.format("%.1f", stats.getAvgSleepHours()) + "小时\n");
        writer.write("- 平均饮水量: " + String.format("%.0f", stats.getAvgWaterIntake()) + "ml\n");
        writer.write("- 平均运动时长: " + String.format("%.0f", stats.getAvgExerciseMinutes()) + "分钟\n");
        writer.write("- 连续打卡天数: " + stats.getConsecutiveDays() + "天\n");
        writer.write("\n");
    }

    private void writeHealthAdviceSection(BufferedWriter writer, int userId) throws IOException {
        writer.write("健康建议:\n");
        
        DailyCheckInDao.HealthStatistics stats = dailyCheckInDao.getHealthStatistics(userId, 7);
        
        if (stats.getAvgHealthScore() < 70) {
            writer.write("- 您的健康评分偏低，建议改善生活习惯\n");
        }
        
        if (stats.getAvgSleepHours() < 7) {
            writer.write("- 睡眠时长不足，建议保证每天7-8小时睡眠\n");
        }
        
        if (stats.getAvgWaterIntake() < 1500) {
            writer.write("- 饮水量偏少，建议每天饮水1500-2000ml\n");
        }
        
        if (stats.getAvgExerciseMinutes() < 30) {
            writer.write("- 运动量不足，建议每天运动30分钟以上\n");
        }
        
        writer.write("- 保持规律的作息时间\n");
        writer.write("- 均衡饮食，多吃蔬菜水果\n");
        writer.write("- 定期监测健康指标\n");
        writer.write("\n");
    }

    private void writeHtmlHealthDataSection(BufferedWriter writer, int userId) throws IOException {
        UserHealthData healthData = healthDataDao.getLatestHealthDataByUserId(userId);
        
        writer.write("<div class=\"section\">\n");
        writer.write("    <h2>健康数据</h2>\n");
        
        if (healthData != null) {
            writer.write("    <div class=\"metric\">身高: " + healthData.getHeight() + "cm</div>\n");
            writer.write("    <div class=\"metric\">体重: " + healthData.getWeight() + "kg</div>\n");
            writer.write("    <div class=\"metric\">BMI: " + String.format("%.1f", healthData.calculateBMI()) + " (" + healthData.getBMICategory() + ")</div>\n");
            writer.write("    <div class=\"metric\">基础代谢率: " + String.format("%.0f", healthData.calculateBMR()) + " 卡路里/天</div>\n");
            writer.write("    <div class=\"metric\">每日总能量消耗: " + String.format("%.0f", healthData.calculateTDEE()) + " 卡路里/天</div>\n");
        } else {
            writer.write("    <p>暂无健康数据</p>\n");
        }
        
        writer.write("</div>\n");
    }

    private void writeHtmlCheckInSection(BufferedWriter writer, int userId) throws IOException {
        DailyCheckInDao.HealthStatistics stats = dailyCheckInDao.getHealthStatistics(userId, 30);
        
        writer.write("<div class=\"section\">\n");
        writer.write("    <h2>打卡统计 (最近30天)</h2>\n");
        writer.write("    <div class=\"metric\">平均健康评分: " + String.format("%.1f", stats.getAvgHealthScore()) + "/100</div>\n");
        writer.write("    <div class=\"metric\">平均睡眠时长: " + String.format("%.1f", stats.getAvgSleepHours()) + "小时</div>\n");
        writer.write("    <div class=\"metric\">平均饮水量: " + String.format("%.0f", stats.getAvgWaterIntake()) + "ml</div>\n");
        writer.write("    <div class=\"metric\">平均运动时长: " + String.format("%.0f", stats.getAvgExerciseMinutes()) + "分钟</div>\n");
        writer.write("    <div class=\"metric\">连续打卡天数: " + stats.getConsecutiveDays() + "天</div>\n");
        writer.write("</div>\n");
    }

    private void writeHtmlRecipeSection(BufferedWriter writer, int userId) throws IOException {
        List<Recipe> recipes = recipeService.getRecipesByUser(userId);
        
        writer.write("<div class=\"section\">\n");
        writer.write("    <h2>我的食谱</h2>\n");
        writer.write("    <p>食谱总数: " + recipes.size() + "个</p>\n");
        
        if (!recipes.isEmpty()) {
            writer.write("    <table>\n");
            writer.write("        <tr><th>名称</th><th>分类</th><th>制作步骤</th></tr>\n");
            
            for (Recipe recipe : recipes) {
                writer.write("        <tr>\n");
                writer.write("            <td>" + recipe.getName() + "</td>\n");
                writer.write("            <td>" + (recipe.getCategory() != null ? recipe.getCategory().getName() : "未分类") + "</td>\n");
                writer.write("            <td>" + recipe.getInstructions().substring(0, Math.min(100, recipe.getInstructions().length())) + "...</td>\n");
                writer.write("        </tr>\n");
            }
            
            writer.write("    </table>\n");
        }
        
        writer.write("</div>\n");
    }

    private void writeHtmlPantrySection(BufferedWriter writer, int userId) throws IOException {
        List<PantryItem> items = pantryService.getPantryItemsByUser(userId);
        
        writer.write("<div class=\"section\">\n");
        writer.write("    <h2>食品库存</h2>\n");
        writer.write("    <p>库存总数: " + items.size() + "种</p>\n");
        
        if (!items.isEmpty()) {
            writer.write("    <table>\n");
            writer.write("        <tr><th>食材名称</th><th>数量</th><th>过期日期</th><th>状态</th></tr>\n");
            
            for (PantryItem item : items) {
                String status = "正常";
                if (item.isExpired()) {
                    status = "已过期";
                } else if (item.getDaysUntilExpiry() <= 7) {
                    status = "即将过期";
                }
                
                writer.write("        <tr>\n");
                writer.write("            <td>" + item.getIngredient().getName() + "</td>\n");
                writer.write("            <td>" + item.getQuantity() + "</td>\n");
                writer.write("            <td>" + (item.getExpiryDate() != null ? item.getExpiryDate().format(DATE_FORMATTER) : "无") + "</td>\n");
                writer.write("            <td>" + status + "</td>\n");
                writer.write("        </tr>\n");
            }
            
            writer.write("    </table>\n");
        }
        
        writer.write("</div>\n");
    }

    private void ensureExportDirectoryExists() {
        File dir = new File(EXPORT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public List<String> getExportFiles() {
        List<String> files = new ArrayList<>();
        File dir = new File(EXPORT_DIR);
        
        if (dir.exists() && dir.isDirectory()) {
            File[] fileList = dir.listFiles();
            if (fileList != null) {
                for (File file : fileList) {
                    if (file.isFile()) {
                        files.add(file.getName());
                    }
                }
            }
        }
        
        return files;
    }

    public boolean deleteExportFile(String fileName) {
        File file = new File(EXPORT_DIR + fileName);
        return file.delete();
    }

}