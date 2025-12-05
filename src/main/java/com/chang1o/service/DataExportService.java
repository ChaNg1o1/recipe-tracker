package com.chang1o.service;

import com.chang1o.dao.DailyCheckInDao;
import com.chang1o.dao.RecipeDao;
import com.chang1o.dao.UserHealthDataDao;
import com.chang1o.model.DailyCheckIn;
import com.chang1o.model.PantryItem;
import com.chang1o.model.Recipe;
import com.chang1o.model.UserHealthData;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DataExportService {
  private static final String EXPORT_DIR = "exports/";
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private UserHealthDataDao healthData;
  private DailyCheckInDao dailyCheckInDao;
  private RecipeService recipeService;
  private PantryService pantryService;

  public DataExportService {
    this.healthDataDao = new UserHealthDataDao();
    this.dailyCheckInDao = new DailyCheckInDao();
    this.recipeService = new RecipeService();
    this.pantryService = new pantryService();

    ensureExportDirectoryExists();
  }

  public String exportHealthDataReport(int userId) {
    try {
      String fileName = "health_report_" + userId + "_" +
                        LocalDate.now().format(DATE_FORMATTER);
      String filePath = EXPORT_DIR + fileName;

      try (BufferedWriter writer =
               new BufferedWriter(new FileWriter(filePath))) {
        writer.write("个人健康数据报告\n");
        writer.write("生成时间:" + LocalDate.now().format(DATE_FORMATTER) +
                     "\n");
        writer.write("用户ID:" + userId + "\n");
        writeHealthDataSection(writer, userId);
        writeCheckInStatisticsSection(writer, userId);
        writeHealthAdviceSection(writer, userId);
      }
      return filePath;
    } catch (IOException e) {
      System.err.println("导出健康数据报告发生错误:" + e.getMessage());
      return null;
    }
  }

  public String exportCheckInRecords(int userId, int days) {
    try {
      String fileName = "checkin_records_" + userId + "_" +
                        LocalDate.now().format(DATE_FORMATTER);
      String filePath = EXPORT_DIR + fileName;

      List<DailyCheckIn> checkIns =
          dailyCheckInDao.getRecentCheckIns(userId, days);

      try (BufferedWriter writer =
               new BufferedWriter(new FileWriter(filePath))) {
        writer.write("日期,心情,睡眠时长(小时),饮水量(ml),运动时长(分钟)," +
                     "健康评分,备注\n");

        for (DailyCheckIn checkIn : checkIns) {
          writer.write(String.format(
              "%s,%s,%.1f,%d,%d,%d,\"%s\"\n",
              checkIn.getCheckInDate().format(DATE_FORMATTER),
              checkIn.getMoodDescription(), checkIn.getSleepHours(),
              checkIn.getWaterIntake(), checkIn.getExerciseMinutes(),
              checkIn.getHealthScore(),
              checkIn.getNotes() != null ? checkIn.getNotes() : ""));
        }
      }
      return filePath;
    } catch (IOException e) {
      System.err.println("导出打卡记录发生错误：" + e.getMessage());
      return null;
    }
  }

  public String exportRecipeData(int userId){
    try{
        String fileName = "recipe_data_" + userId + "_" + LocalDate.now().format(DATE_FORMATTER);
        String filePath = EXPORT_DIR + fileName;

        List<Recipe> recipes = recipeService.getRecipesByUser(userId);

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){

            writer.write("个人食谱数据\n");
            writer.write("生成时间" + LocalDateTime.now().format(DATE_TIME_FORMATTER) + "\n");
            writer.write("食谱总数:" + recipes.size() + "个\n");
            
            for(int i = 0; i < recipes.size(); i++){
                Recipe recipe = recipes.get(i);
                writer.write((i+1) + ". " + recipe.getName() + "\n");
                if (recipe.getCategory() != null) {
                    writer.write("分类:" + recipe.getCategory().getName());
                }
                writer.write("制作步骤:" + recipe.getInstructions() + "\n");
                writer.write("\n");
            }
        }
        return filePath;
    }catch(IOException e){
        System.err.println("导出食谱数据发生错误:" + e.getMessage());
        return null;
    }
  }

  public String exportPantryData(int userId){
    try{
        String fileName = "pantry_data_" + userId + "_" + LocalDate.now().format(DATE_FORMATTER) + ".csv";
        String filePath = EXPORT_DIR + fileName;

        List<PantryItem> items = pantryService.getPantryItemsByUser(userId);

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
            writer.write("食材名称,数量,过期日期,状态\n");
            
            for(PantryItem item:items){
                String status = "正常"
                if (items.isExpried()) {
                    status = "已过期";
                }else if (items.getDaysUntilExpiry() <= 7) {
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

    }catch(IOException e){
        System.out.println("导出库存数据发生错误:" + e.getMessage());
        return null;
    }
  }

  
}
