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
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

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
    jar_type_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    unit_price DECIMAL(10, 2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (jar_type_id) REFERENCES jar_types(id) ON DELETE RESTRICT,
    CHECK (quantity >= 0)
);

-- ============================================
-- 5. CAPS TABLE
-- ============================================
CREATE TABLE caps (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    jar_type_id INT NOT NULL, -- Must match jar type for compatibility
    color VARCHAR(50),
    quantity INT NOT NULL DEFAULT 0,
    unit_price DECIMAL(10, 2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (jar_type_id) REFERENCES jar_types(id) ON DELETE RESTRICT,
    CHECK (quantity >= 0)
);

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
    quantity INT NOT NULL,
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
    item_type ENUM('jar', 'cap') NOT NULL,
    item_id INT NOT NULL, -- TODO revisar posible cambio a varchar para ubicar más facil el item afectado
    quantity_change INT NOT NULL, -- Positive for additions, negative for subtractions
    transaction_type ENUM('sale', 'restock', 'adjustment', 'damage', 'return') NOT NULL,
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
-- INSERT DEFAULT DATA
-- ============================================

-- Insert default jar types
INSERT INTO jar_types (name, description) VALUES
('Circular', 'Round-shaped jars'),
('Square', 'Square-shaped jars'),
('Rectangular', 'Rectangular-shaped jars');

-- Insert default roles
INSERT INTO roles (name, description, permissions) VALUES
('admin', 'Administrator with full access', '["create", "read", "update", "delete", "manage_users", "view_reports"]'),
('seller', 'Sales person with limited access', '["read_inventory", "create_sales", "view_own_sales"]');

-- Insert default admin user (password: admin123 - should be hashed in production)
INSERT INTO users (username, email, password_hash, first_name, last_name, phone_number, role_id) VALUES
('admin', 'admin@envases.com', '$2b$10$example_hash_here', 'Admin', 'User', '1234567890', 1);


-- ============================================
-- INDEXES FOR PERFORMANCE
-- ============================================
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role_id);
CREATE INDEX idx_jars_type ON jars(jar_type_id);
CREATE INDEX idx_caps_type ON caps(jar_type_id);
CREATE INDEX idx_sales_user ON sales(user_id);
CREATE INDEX idx_sales_date ON sales(sale_date);
CREATE INDEX idx_sale_items_sale ON sale_items(sale_id);
CREATE INDEX idx_transactions_item ON transactions(item_type, item_id);
CREATE INDEX idx_transactions_date ON transactions(transaction_date);

-- ============================================
-- TRIGGERS FOR INVENTORY TRACKING
-- ============================================

-- Trigger to log transactions when jar quantity changes
DELIMITER //
CREATE TRIGGER jar_quantity_change_log
AFTER UPDATE ON jars
FOR EACH ROW
BEGIN
    IF OLD.quantity != NEW.quantity THEN
        INSERT INTO transactions (
            item_type, 
            item_id, 
            quantity_change, 
            transaction_type, 
            notes,
            transaction_date
        ) VALUES (
            'jar', 
            NEW.id, 
            NEW.quantity - OLD.quantity, 
            'adjustment', 
            CONCAT('Direct quantity change from ', OLD.quantity, ' to ', NEW.quantity),
            NOW()
        );
    END IF;
END//
DELIMITER ;

-- Trigger to log transactions when cap quantity changes
DELIMITER //
CREATE TRIGGER cap_quantity_change_log
AFTER UPDATE ON caps
FOR EACH ROW
BEGIN
    IF OLD.quantity != NEW.quantity THEN
        INSERT INTO transactions (
            item_type, 
            item_id, 
            quantity_change, 
            transaction_type, 
            notes,
            transaction_date
        ) VALUES (
            'cap', 
            NEW.id, 
            NEW.quantity - OLD.quantity, 
            'adjustment', 
            CONCAT('Direct quantity change from ', OLD.quantity, ' to ', NEW.quantity),
            NOW()
        );
    END IF;
END//
DELIMITER ;

-- ============================================
-- SAMPLE QUERIES FOR TESTING
-- ============================================

/*
-- Get current inventory
SELECT * FROM inventory_status;

-- Get low stock items
SELECT * FROM inventory_status WHERE stock_status IN ('Low Stock', 'Out of Stock');

-- Get compatible jars and caps
SELECT 
    j.name as jar_name,
    c.name as cap_name,
    jt.name as type_name,
    j.quantity as jar_stock,
    c.quantity as cap_stock
FROM jars j
JOIN caps c ON j.jar_type_id = c.jar_type_id
JOIN jar_types jt ON j.jar_type_id = jt.id
WHERE j.is_active = TRUE AND c.is_active = TRUE;

-- Get sales by user
SELECT 
    u.username,
    COUNT(s.id) as total_sales,
    SUM(s.total_amount) as total_revenue
FROM users u
LEFT JOIN sales s ON u.id = s.user_id
GROUP BY u.id, u.username;

-- Get transaction history for an item
SELECT 
    t.*,
    CONCAT(u.first_name, ' ', u.last_name) as performed_by_name
FROM transactions t
LEFT JOIN users u ON t.performed_by = u.id
WHERE t.item_type = 'jar' AND t.item_id = 1
ORDER BY t.transaction_date DESC;
*/
