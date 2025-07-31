-- ===============================
-- SHOE STORE DATABASE SCHEMA
-- ===============================

-- Create schema if not exists
CREATE SCHEMA IF NOT EXISTS public;

-- Set search path
SET search_path TO public;

-- ===============================
-- USER TABLES
-- ===============================

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    version BIGINT DEFAULT 0
);

-- User addresses table
CREATE TABLE IF NOT EXISTS user_addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    address_line_1 VARCHAR(255) NOT NULL,
    address_line_2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL DEFAULT 'Israel',
    is_default BOOLEAN DEFAULT FALSE,
    label VARCHAR(50),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    delivery_instructions VARCHAR(500),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    version BIGINT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ===============================
-- PRODUCT TABLES
-- ===============================

-- Brands table
CREATE TABLE IF NOT EXISTS brands (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    logo_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    version BIGINT DEFAULT 0
);

-- Shoe categories table
CREATE TABLE IF NOT EXISTS shoe_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    version BIGINT DEFAULT 0
);

-- Shoes table (base models)
CREATE TABLE IF NOT EXISTS shoes (
    id BIGSERIAL PRIMARY KEY,
    brand_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    gender VARCHAR(20) NOT NULL CHECK (gender IN ('MEN', 'WOMEN', 'UNISEX')),
    base_price DECIMAL(10,2) NOT NULL CHECK (base_price >= 0),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    version BIGINT DEFAULT 0,
    FOREIGN KEY (brand_id) REFERENCES brands(id),
    FOREIGN KEY (category_id) REFERENCES shoe_categories(id)
);

-- Shoe models table (variants with colors, materials)
CREATE TABLE IF NOT EXISTS shoe_models (
    id BIGSERIAL PRIMARY KEY,
    shoe_id BIGINT NOT NULL,
    model_name VARCHAR(255) NOT NULL,
    color VARCHAR(50) NOT NULL,
    material VARCHAR(100),
    sku VARCHAR(100) NOT NULL UNIQUE,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    image_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    version BIGINT DEFAULT 0,
    FOREIGN KEY (shoe_id) REFERENCES shoes(id) ON DELETE CASCADE
);

-- Shoe inventory table
CREATE TABLE IF NOT EXISTS shoe_inventory (
    id BIGSERIAL PRIMARY KEY,
    shoe_model_id BIGINT NOT NULL,
    size VARCHAR(10) NOT NULL,
    quantity_available INTEGER NOT NULL DEFAULT 0 CHECK (quantity_available >= 0),
    quantity_reserved INTEGER NOT NULL DEFAULT 0 CHECK (quantity_reserved >= 0),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT 'system',
    version BIGINT DEFAULT 0,
    FOREIGN KEY (shoe_model_id) REFERENCES shoe_models(id) ON DELETE CASCADE,
    UNIQUE(shoe_model_id, size)
);

-- ===============================
-- ORDER TABLES
-- ===============================

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_number VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    shipping_address_id BIGINT,
    billing_address_id BIGINT,
    order_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    shipped_date TIMESTAMP WITHOUT TIME ZONE,
    delivered_date TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    version BIGINT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (shipping_address_id) REFERENCES user_addresses(id),
    FOREIGN KEY (billing_address_id) REFERENCES user_addresses(id)
);

-- Order items table
CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    shoe_model_id BIGINT NOT NULL,
    size VARCHAR(10) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    total_price DECIMAL(10,2) NOT NULL CHECK (total_price >= 0),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    version BIGINT DEFAULT 0,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (shoe_model_id) REFERENCES shoe_models(id)
);

-- ===============================
-- INDEXES FOR PERFORMANCE
-- ===============================

-- User indexes
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_user_addresses_user_id ON user_addresses(user_id);
CREATE INDEX IF NOT EXISTS idx_user_addresses_is_default ON user_addresses(user_id, is_default);

-- Product indexes
CREATE INDEX IF NOT EXISTS idx_shoes_brand_id ON shoes(brand_id);
CREATE INDEX IF NOT EXISTS idx_shoes_category_id ON shoes(category_id);
CREATE INDEX IF NOT EXISTS idx_shoes_is_active ON shoes(is_active);
CREATE INDEX IF NOT EXISTS idx_shoe_models_shoe_id ON shoe_models(shoe_id);
CREATE INDEX IF NOT EXISTS idx_shoe_models_sku ON shoe_models(sku);
CREATE INDEX IF NOT EXISTS idx_shoe_models_is_active ON shoe_models(is_active);
CREATE INDEX IF NOT EXISTS idx_shoe_inventory_model_size ON shoe_inventory(shoe_model_id, size);

-- Order indexes
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_order_number ON orders(order_number);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_order_date ON orders(order_date);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_shoe_model_id ON order_items(shoe_model_id);

-- ===============================
-- TRIGGERS FOR UPDATED_AT
-- ===============================

-- Function to update updated_at column
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_user_addresses_updated_at BEFORE UPDATE ON user_addresses FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_shoes_updated_at BEFORE UPDATE ON shoes FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_shoe_models_updated_at BEFORE UPDATE ON shoe_models FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_shoe_inventory_updated_at BEFORE UPDATE ON shoe_inventory FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_orders_updated_at BEFORE UPDATE ON orders FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_brands_updated_at BEFORE UPDATE ON brands FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_shoe_categories_updated_at BEFORE UPDATE ON shoe_categories FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();