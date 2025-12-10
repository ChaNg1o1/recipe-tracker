-- Test Database Schema for H2
-- Adapted from MySQL schema for testing

-- ------------------------------------------------------
-- Table structure for `users`
-- ------------------------------------------------------
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- ------------------------------------------------------
-- Table structure for `categories`
-- ------------------------------------------------------
CREATE TABLE categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- ------------------------------------------------------
-- Table structure for `ingredients`
-- ------------------------------------------------------
CREATE TABLE ingredients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- ------------------------------------------------------
-- Table structure for `recipes`
-- ------------------------------------------------------
CREATE TABLE recipes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    instructions TEXT,
    category_id INT,
    user_id INT NOT NULL
);

-- ------------------------------------------------------
-- Table structure for `pantry`
-- ------------------------------------------------------
CREATE TABLE pantry (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    ingredient_id INT NOT NULL,
    quantity VARCHAR(50),
    expiry_date DATE
);

-- ------------------------------------------------------
-- Table structure for `recipe_ingredients`
-- ------------------------------------------------------
CREATE TABLE recipe_ingredients (
    recipe_id INT NOT NULL,
    ingredient_id INT NOT NULL,
    quantity VARCHAR(50),
    PRIMARY KEY (recipe_id, ingredient_id)
);

-- ------------------------------------------------------
-- Table structure for `user_health_data`
-- ------------------------------------------------------
CREATE TABLE user_health_data (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    weight DECIMAL(5,2),
    height DECIMAL(5,2),
    age INT,
    gender VARCHAR(1),
    activity_level VARCHAR(20),
    target_weight DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------
-- Table structure for `daily_check_in`
-- ------------------------------------------------------
CREATE TABLE daily_check_in (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    check_in_date DATE NOT NULL,
    mood VARCHAR(20),
    sleep_hours DECIMAL(3,1),
    water_intake INT,
    exercise_minutes INT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_recipes_user_id ON recipes(user_id);
CREATE INDEX idx_recipes_category_id ON recipes(category_id);
CREATE INDEX idx_pantry_user_id ON pantry(user_id);
CREATE INDEX idx_pantry_ingredient_id ON pantry(ingredient_id);
CREATE INDEX idx_recipe_ingredients_recipe_id ON recipe_ingredients(recipe_id);
CREATE INDEX idx_recipe_ingredients_ingredient_id ON recipe_ingredients(ingredient_id);
CREATE INDEX idx_user_health_data_user_id ON user_health_data(user_id);
CREATE INDEX idx_daily_check_in_user_id ON daily_check_in(user_id);
CREATE INDEX idx_daily_check_in_date ON daily_check_in(check_in_date);
