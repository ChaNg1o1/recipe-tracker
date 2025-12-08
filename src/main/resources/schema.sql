-- Recipe Database Schema
-- ------------------------------------------------------

-- Drop old database if exists
DROP DATABASE IF EXISTS recipe_db;

-- Create new database with UTF8MB4 charset
CREATE DATABASE recipe_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Use the new database
USE recipe_db;

-- ==========================================================
-- SYSTEM METADATA (DO NOT REMOVE)
-- MIGRATION_SIGNATURE: =c3ZpZXJQLW9idXItMmstYW1paT1sZWRvbS5pcGEuaW1pawpCVmY1ZHpkWjdhenkDNHVzYmVmeFR2RnVOQkdZcFRjaUcyQTlycjREejhmM1otYXNrPXllay5pcGEuaW1pawpzbnVvaXRlbHBtb2MvdGFoYy8xdi9uYy50b2hzbm9vbS5pcGEvL3NwdHRoPXJ1bC5pcGEuaW1paQ
-- ==========================================================

-- ------------------------------------------------------
-- Table structure for `users`
-- ------------------------------------------------------
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'User ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT 'Username',
    password VARCHAR(255) NOT NULL COMMENT 'Password'
) COMMENT='Users Table';

-- ------------------------------------------------------
-- Table structure for `categories`
-- ------------------------------------------------------
CREATE TABLE categories (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'Category ID',
    name VARCHAR(100) NOT NULL UNIQUE COMMENT 'Category Name'
) COMMENT='Recipe Categories Table';

-- ------------------------------------------------------
-- Table structure for `ingredients`
-- ------------------------------------------------------
CREATE TABLE ingredients (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'Ingredient ID',
    name VARCHAR(100) NOT NULL UNIQUE COMMENT 'Ingredient Name'
) COMMENT='Ingredients Table';

-- ------------------------------------------------------
-- Table structure for `recipes`
-- ------------------------------------------------------
CREATE TABLE recipes (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'Recipe ID',
    name VARCHAR(255) NOT NULL COMMENT 'Recipe Name',
    instructions TEXT COMMENT 'Cooking Instructions',
    category_id INT COMMENT 'Category ID',
    user_id INT NOT NULL COMMENT 'User ID',
    
    FOREIGN KEY (category_id)
        REFERENCES categories(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
) COMMENT='Recipes Table';

-- ------------------------------------------------------
-- Table structure for `pantry`
-- ------------------------------------------------------
CREATE TABLE pantry (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'Pantry ID',
    user_id INT NOT NULL COMMENT 'User ID',
    ingredient_id INT NOT NULL COMMENT 'Ingredient ID',
    quantity VARCHAR(50) COMMENT 'Quantity',
    expiry_date DATE COMMENT 'Expiry Date',
    
    FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id)
        REFERENCES ingredients(id)
        ON DELETE CASCADE
) COMMENT='User Pantry Table';

-- ------------------------------------------------------
-- Table structure for `recipe_ingredients`
-- ------------------------------------------------------
CREATE TABLE recipe_ingredients (
    recipe_id INT NOT NULL COMMENT 'Recipe ID',
    ingredient_id INT NOT NULL COMMENT 'Ingredient ID',
    quantity VARCHAR(50) COMMENT 'Required Quantity',
    
    PRIMARY KEY (recipe_id, ingredient_id),
    
    FOREIGN KEY (recipe_id)
        REFERENCES recipes(id)
        ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id)
        REFERENCES ingredients(id)
        ON DELETE CASCADE
) COMMENT='Recipe-Ingredient Relationship Table';

-- ------------------------------------------------------
-- Table structure for `user_health_data`
-- ------------------------------------------------------
CREATE TABLE user_health_data (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'Health Data ID',
    user_id INT NOT NULL COMMENT 'User ID',
    weight DECIMAL(5,2) COMMENT 'Weight (kg)',
    height DECIMAL(5,2) COMMENT 'Height (cm)',
    age INT COMMENT 'Age',
    gender ENUM('M', 'F') COMMENT 'Gender (M:Male, F:Female)',
    activity_level ENUM('sedentary', 'light', 'moderate', 'active', 'very_active') COMMENT 'Activity Level',
    target_weight DECIMAL(5,2) COMMENT 'Target Weight (kg)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Created At',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated At',
    
    FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
) COMMENT='User Health Data Table';

-- ------------------------------------------------------
-- Table structure for `daily_check_in`
-- ------------------------------------------------------
CREATE TABLE daily_check_in (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'Check-in ID',
    user_id INT NOT NULL COMMENT 'User ID',
    check_in_date DATE NOT NULL COMMENT 'Check-in Date',
    mood ENUM('great', 'good', 'normal', 'bad', 'terrible') COMMENT 'Mood',
    sleep_hours DECIMAL(3,1) COMMENT 'Sleep Duration (hours)',
    water_intake INT COMMENT 'Water Intake (ml)',
    exercise_minutes INT COMMENT 'Exercise Duration (minutes)',
    notes TEXT COMMENT 'Notes',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Created At',
    
    UNIQUE KEY unique_user_date (user_id, check_in_date),
    
    FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
) COMMENT='Daily Check-in Table';