-- ============================================
-- Envases Inventory Management System
-- Database Schema
-- Created: June 25, 2025
-- ============================================

-- Create database
CREATE DATABASE IF NOT EXISTS envases_inventory;
USE envases_inventory;

-- ============================================
-- 1. JAR TYPES TABLE
-- ============================================
CREATE TABLE jar_types (
    diameter VARCHAR(50) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE jar_types CHANGE id diameter VARCHAR(50) NOT NULL;

-- ============================================
-- 2. ROLES TABLE
-- ============================================
CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    permissions JSON, -- Store permissions as JSON for flexibility
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================
-- 3. USERS TABLE
-- ============================================
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    role_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT
);

-- ============================================
-- 4. JARS TABLE
-- ============================================
CREATE TABLE jars (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    cap_diameter VARCHAR(50),
    quantity INT NOT NULL DEFAULT 0, -- Stock quantity
    unit_price DECIMAL(10, 2) DEFAULT 0.00,
    docena_price DECIMAL(10, 2) DEFAULT 0.00, -- Price for a dozen jars
    cien_price DECIMAL(10, 2) DEFAULT 0.00, -- Price for a hundred jars
    paca_price DECIMAL(10, 2) DEFAULT 0.00, -- Price for a pack of jars
    units_in_paca INT DEFAULT 0, -- Number of jars in a pack
    volume DECIMAL(10, 2) DEFAULT 0.00, -- Volume in liters or milliliters
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cap_diameter) REFERENCES jar_types(diameter) ON DELETE RESTRICT
);

ALTER TABLE jars ADD COLUMN docena_price DECIMAL(10, 2) DEFAULT 0.00; -- Price for a dozen jars
ALTER TABLE jars ADD COLUMN cien_price DECIMAL(10, 2) DEFAULT 0.00; -- Price for a hundred jars
ALTER TABLE jars ADD COLUMN paca_price DECIMAL(10, 2) DEFAULT 0.00; -- Price for a pack of jars
ALTER TABLE jars ADD COLUMN units_in_paca INT DEFAULT 0; -- Number of jars in a pack   
ALTER TABLE jars ADD COLUMN volume DECIMAL(10, 2) DEFAULT 0.00; -- Volume in liters or milliliters
ALTER TABLE jars CHANGE diameter diameter VARCHAR(50); -- Change jar_type_id to diameter

ALTER TABLE jars ADD CONSTRAINT fk_jars_cap_diameter FOREIGN KEY (cap_diameter) REFERENCES jar_types(diameter) ON DELETE RESTRICT;
-- ============================================
-- 5. CAPS TABLE
-- ============================================
CREATE TABLE caps (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    diameter VARCHAR(50) NOT NULL, -- Must match jar type for compatibility
    color VARCHAR(50),
    quantity INT NOT NULL DEFAULT 0,-- Stock quantity
    unit_price DECIMAL(10, 2) DEFAULT 0.00,
    docena_price DECIMAL(10, 2) DEFAULT 0.00, -- Price for a dozen jars
    cien_price DECIMAL(10, 2) DEFAULT 0.00, -- Price for a hundred jars
    paca_price DECIMAL(10, 2) DEFAULT 0.00, -- Price for a pack of jars
    units_in_paca INT DEFAULT 0, -- Number of caps in a pack
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (diameter) REFERENCES jar_types(diameter) ON DELETE RESTRICT
);

ALTER TABLE caps ADD COLUMN docena_price DECIMAL(10, 2) DEFAULT 0.00; -- Price for a dozen caps
ALTER TABLE caps ADD COLUMN cien_price DECIMAL(10, 2) DEFAULT 0.00; -- Price for a hundred caps
ALTER TABLE caps ADD COLUMN paca_price DECIMAL(10, 2) DEFAULT 0.00; -- Price for a pack of caps
ALTER TABLE caps ADD COLUMN units_in_paca INT DEFAULT 0; -- Number of caps in a pack

ALTER TABLE caps CHANGE jar_type_id diameter VARCHAR(50) NOT NULL; -- Change jar_type_id to diameter

ALTER TABLE caps ADD CONSTRAINT fk_caps_diameter FOREIGN KEY (diameter) REFERENCES jar_types(diameter) ON DELETE RESTRICT;

-- ============================================
-- 6. SALES TABLE
-- ============================================
CREATE TABLE sales (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL, -- Who made the sale
    client_name VARCHAR(100),
    client_phone VARCHAR(20),
    client_email VARCHAR(100),
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('cash', 'card', 'transfer', 'other') DEFAULT 'cash',
    notes TEXT,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CHECK (total_amount >= 0)
);

-- ============================================
-- 7. SALE ITEMS TABLE (Items sold in each sale)
-- ============================================
CREATE TABLE sale_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    sale_id INT NOT NULL,
    item_type ENUM('jar', 'cap', 'combo') NOT NULL,
    jar_id INT NULL, -- If selling jar or combo
    cap_id INT NULL, -- If selling cap or combo
    quantity_jar INT NOT NULL,
    quantity_cap INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE,
    FOREIGN KEY (jar_id) REFERENCES jars(id) ON DELETE RESTRICT,
    FOREIGN KEY (cap_id) REFERENCES caps(id) ON DELETE RESTRICT,
    CHECK (quantity > 0),
    CHECK (unit_price >= 0),
    CHECK (subtotal >= 0),
    -- Ensure at least one item is specified based on type
    CHECK (
        (item_type = 'jar' AND jar_id IS NOT NULL AND cap_id IS NULL) OR
        (item_type = 'cap' AND cap_id IS NOT NULL AND jar_id IS NULL) OR
        (item_type = 'combo' AND jar_id IS NOT NULL AND cap_id IS NOT NULL)
    )
);

-- ============================================
-- 8. TRANSACTIONS TABLE (Inventory movements)
-- ============================================
CREATE TABLE transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    item_type ENUM('jar', 'cap', 'combo') NOT NULL,
    item_id INT NOT NULL, -- TODO revisar posible cambio a varchar para ubicar más facil el item afectado
    quantity_change INT NOT NULL, -- Positive for additions, negative for subtractions
    transaction_type ENUM('sale', 'restock', 'adjustment', 'damage', 'return') NOT NULL, -- TODO revisar si cambiar los enums
    reference_id INT NULL, -- Links to sale_id, purchase_order_id, etc.
    reference_type VARCHAR(50), -- 'sale', 'purchase_order', 'manual_adjustment', etc.
    notes TEXT,
    performed_by INT, -- User who performed the transaction
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (performed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_item_lookup (item_type, item_id),
    INDEX idx_transaction_date (transaction_date)
);

-- ============================================
-- 9. COMBOS TABLE (table for jar and cap sale combinations)
-- ============================================

CREATE TABLE combos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    jar_id INT NOT NULL, -- Array of jar IDs in the combo
    cap_id INT NOT NULL, -- Array of cap IDs in the combo
    unit_price DECIMAL(10, 2) DEFAULT 0.00,
    docena_price DECIMAL(10, 2) DEFAULT 0.00, -- Price for a dozen combos
    cien_price DECIMAL(10, 2) DEFAULT 0.00, -- Price for a hundred combos
    paca_price DECIMAL(10, 2) DEFAULT 0.00, -- Price for a pack of combos
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (jar_id) REFERENCES jars(id) ON DELETE RESTRICT,
    FOREIGN KEY (cap_id) REFERENCES caps(id) ON DELETE RESTRICT
);

-- ============================================
-- 10. IS compatible (table for jar and cap sale combinations)
-- ============================================
CREATE TABLE jar_cap_compatibility (
    id INT PRIMARY KEY AUTO_INCREMENT,
    jar_id INT NOT NULL,
    cap_id INT NOT NULL,
    is_compatible BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (jar_id) REFERENCES jars(id) ON DELETE CASCADE,
    FOREIGN KEY (cap_id) REFERENCES caps(id) ON DELETE CASCADE,
    UNIQUE (jar_id, cap_id) -- Ensure no duplicate compatibility entries
);

-- ============================================
-- 11. quimicos
-- ============================================

CREATE TABLE quimicos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    quantity INT NOT NULL DEFAULT 0, -- Stock quantity
    unit_price DECIMAL(10, 2) DEFAULT 0.00
    is_active BOOLEAN DEFAULT TRUE
);


-- ============================================
-- 11. Extractos
-- ============================================

CREATE TABLE extractos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    quantity INT NOT NULL DEFAULT 0, -- Stock quantity
    price22ml DECIMAL(10, 2) DEFAULT 0.00 NOT NULL,
    price60ml DECIMAL(10, 2) DEFAULT 0.00,
    price125ml DECIMAL(10, 2) DEFAULT 0.00,
    price250ml DECIMAL(10, 2) DEFAULT 0.00,
    price500ml DECIMAL(10, 2) DEFAULT 0.00,
    price1000ml DECIMAL(10, 2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE
);



-- ============================================
-- INSERT DEFAULT DATA
-- ============================================

-- Insert default roles
INSERT INTO roles (name, description, permissions) VALUES
('admin', 'Administrator with full access', '["create", "read", "update", "delete", "sales"]'),
('seller', 'Sales person with limited access', '["read", "sales", "view_own_sales"]');

-- Insert default admin user (password: admin123 - should be hashed in production)

-- ============================================
-- INDEXES FOR PERFORMANCE
-- ============================================
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role_id);
CREATE INDEX idx_jars_type ON jars(jar_diameter);
CREATE INDEX idx_caps_type ON caps(cap_diameter);
CREATE INDEX idx_sales_user ON sales(user_id);
CREATE INDEX idx_sales_date ON sales(sale_date);
CREATE INDEX idx_sale_items_sale ON sale_items(sale_id);
CREATE INDEX idx_transactions_item ON transactions(item_type, item_id);
CREATE INDEX idx_transactions_date ON transactions(transaction_date);

