-- -- Step 1: Create the database if it doesn't exist
-- CREATE DATABASE IF NOT EXISTS inventory_db;

-- -- Step 2: Select the database to use
-- USE inventory_db;

-- -- Step 3: Create tables

-- -- Create role table
-- CREATE TABLE IF NOT EXISTS role (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     name VARCHAR(255) NOT NULL UNIQUE
-- );

-- -- Create users table
-- CREATE TABLE IF NOT EXISTS users (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     username VARCHAR(255) NOT NULL UNIQUE,
--     password VARCHAR(255) NOT NULL,
--     email VARCHAR(255),
--     status VARCHAR(50),
--     first_name VARCHAR(255),
--     last_name VARCHAR(255),
--     phone VARCHAR(20)
-- );

-- -- Create company table
-- CREATE TABLE IF NOT EXISTS company (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     name VARCHAR(255) NOT NULL,
--     tax_id VARCHAR(50),
--     phone VARCHAR(50),
--     mobile VARCHAR(50),
--     email VARCHAR(255),
--     state VARCHAR(255),
--     city VARCHAR(255),
--     address VARCHAR(255),
--     zip VARCHAR(20),
--     add_purchased_items_to_favorites TINYINT(1),
--     logo VARCHAR(255),
--     allowed_invoice_deviation INT,
--     export_delivery_notes_as_bills TINYINT(1)
-- );

-- -- Create user_roles table (many-to-many)
-- CREATE TABLE IF NOT EXISTS user_roles (
--     user_id INT,
--     role_id INT,
--     PRIMARY KEY (user_id, role_id),
--     FOREIGN KEY (user_id) REFERENCES users(id),
--     FOREIGN KEY (role_id) REFERENCES role(id)
-- );

-- -- Create company_user table (many-to-many)
-- CREATE TABLE IF NOT EXISTS company_user (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     company_id INT,
--     users_id INT,
--     FOREIGN KEY (company_id) REFERENCES company(id),
--     FOREIGN KEY (users_id) REFERENCES users(id)
-- );

-- -- Create location table
-- CREATE TABLE IF NOT EXISTS location (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     name VARCHAR(255),
--     code VARCHAR(50),
--     address VARCHAR(255),
--     city VARCHAR(255),
--     state VARCHAR(255),
--     zip VARCHAR(20),
--     phone VARCHAR(20),
--     company_id INT,
--     FOREIGN KEY (company_id) REFERENCES company(id)
-- );

-- -- Create location_user table (many-to-many)
-- CREATE TABLE IF NOT EXISTS location_user (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     location_id INT,
--     user_id INT,
--     FOREIGN KEY (location_id) REFERENCES location(id),
--     FOREIGN KEY (user_id) REFERENCES users(id)
-- );
