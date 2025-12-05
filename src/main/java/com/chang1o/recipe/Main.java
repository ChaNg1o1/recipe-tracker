package com.chang1o.recipe;

public class Main {
    public static void main(String[] args) {
            try {
                // 测试数据库连接
                com.chang1o.util.DBUtil dbUtil = com.chang1o.util.DBUtil.getInstance();
                if (!dbUtil.testConnection()) {
                    System.out.println("数据库连接失败");
                }else{
                    System.out.println("数据库连接成功");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


