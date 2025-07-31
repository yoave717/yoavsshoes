-- ===============================
-- INITIAL DATA FOR SHOE STORE
-- ===============================

-- ===============================
-- ADMIN USER
-- ===============================
-- Insert admin user (password: admin123)
-- BCrypt hash for 'admin123'
INSERT INTO users (email, password_hash, first_name, last_name, is_admin)
VALUES ('admin@shoestore.com', '$2a$12$VXUIxd0f7/NpU/ZS3nLF7.k.dJQ8w1BzGMQ8QP2gGJhZQb6aGVe5K', 'Admin', 'User', true)
ON CONFLICT (email) DO NOTHING;

-- Insert regular test user (password: user123)
-- BCrypt hash for 'user123'
INSERT INTO users (email, password_hash, first_name, last_name, is_admin)
VALUES ('user@example.com', '$2a$12$VXUIxd0f7/NpU/ZS3nLF7.k.dJQ8w1BzGMQ8QP2gGJhZQb6aGVe5K', 'John', 'Doe', false)
ON CONFLICT (email) DO NOTHING;

-- ===============================
-- BRANDS - Adding logo URLs
-- ===============================
INSERT INTO brands (name, description, logo_url) VALUES
('Nike', 'Just Do It - Leading athletic footwear and apparel brand', 'https://logos-world.net/wp-content/uploads/2020/04/Nike-Logo.png'),
('Adidas', 'Impossible is Nothing - German multinational corporation', 'https://logos-world.net/wp-content/uploads/2020/04/Adidas-Logo.png'),
('Puma', 'Forever Faster - German multinational corporation', 'https://logos-world.net/wp-content/uploads/2020/05/Puma-Logo.png'),
('New Balance', 'Endorsed by No One - American multinational corporation', 'https://logos-world.net/wp-content/uploads/2020/09/New-Balance-Logo.png'),
('Vans', 'Off The Wall - American manufacturer of skateboarding shoes', 'https://logos-world.net/wp-content/uploads/2020/05/Vans-Logo.png'),
('Converse', 'American shoe company known for Chuck Taylor All-Stars', 'https://logos-world.net/wp-content/uploads/2020/06/Converse-Logo.png'),
('Reebok', 'Be More Human - American-inspired global brand', 'https://logos-world.net/wp-content/uploads/2020/06/Reebok-Logo.png'),
('ASICS', 'Japanese multinational corporation which produces footwear', 'https://logos-world.net/wp-content/uploads/2020/09/ASICS-Logo.png')
ON CONFLICT (name) DO NOTHING;

-- ===============================
-- CATEGORIES
-- ===============================
INSERT INTO shoe_categories (name, description) VALUES
('Running', 'Athletic shoes designed for running and jogging'),
('Basketball', 'High-performance shoes designed for basketball'),
('Casual', 'Everyday comfortable shoes for daily wear'),
('Formal', 'Dress shoes for formal occasions and business'),
('Sneakers', 'Casual athletic shoes for everyday wear'),
('Boots', 'High-ankle shoes for various occasions'),
('Sandals', 'Open-toe shoes for summer and casual wear'),
('Training', 'Cross-training shoes for gym and fitness activities')
ON CONFLICT (name) DO NOTHING;

-- ===============================
-- SHOES (Base Models)
-- ===============================
INSERT INTO shoes (brand_id, category_id, name, description, gender, base_price) VALUES
-- Nike Products
((SELECT id FROM brands WHERE name = 'Nike'), (SELECT id FROM shoe_categories WHERE name = 'Running'), 'Air Max 270', 'Nike Air Max 270 features the largest heel Air unit yet and delivers incredible all-day comfort', 'UNISEX', 150.00),
((SELECT id FROM brands WHERE name = 'Nike'), (SELECT id FROM shoe_categories WHERE name = 'Basketball'), 'Air Jordan 1', 'The Air Jordan 1 High remakes the classic sneaker, giving you a fresh take on what you know', 'UNISEX', 170.00),
((SELECT id FROM brands WHERE name = 'Nike'), (SELECT id FROM shoe_categories WHERE name = 'Casual'), 'Air Force 1', 'The radiance lives on in the Nike Air Force 1, the basketball original that puts a fresh spin on what you know best', 'UNISEX', 110.00),

-- Adidas Products
((SELECT id FROM brands WHERE name = 'Adidas'), (SELECT id FROM shoe_categories WHERE name = 'Running'), 'Ultraboost 22', 'More energy, more cushioning, more responsiveness. This is Ultraboost 22', 'UNISEX', 180.00),
((SELECT id FROM brands WHERE name = 'Adidas'), (SELECT id FROM shoe_categories WHERE name = 'Casual'), 'Stan Smith', 'Clean and simple, the Stan Smith shoes are a versatile staple', 'UNISEX', 80.00),
((SELECT id FROM brands WHERE name = 'Adidas'), (SELECT id FROM shoe_categories WHERE name = 'Sneakers'), 'Gazelle', 'A timeless design rooted in football culture and street style', 'UNISEX', 90.00),

-- Puma Products
((SELECT id FROM brands WHERE name = 'Puma'), (SELECT id FROM shoe_categories WHERE name = 'Running'), 'RS-X', 'The ultimate expression of fresh street style meets sport-inspired design', 'UNISEX', 110.00),
((SELECT id FROM brands WHERE name = 'Puma'), (SELECT id FROM shoe_categories WHERE name = 'Casual'), 'Suede Classic', 'An icon of street culture and basketball courts', 'UNISEX', 70.00),

-- New Balance Products
((SELECT id FROM brands WHERE name = 'New Balance'), (SELECT id FROM shoe_categories WHERE name = 'Running'), '990v5', 'The quintessential American running shoe', 'UNISEX', 175.00),
((SELECT id FROM brands WHERE name = 'New Balance'), (SELECT id FROM shoe_categories WHERE name = 'Casual'), '327', 'Inspired by the no-nonsense design philosophy of the 1970s', 'UNISEX', 90.00),

-- Vans Products
((SELECT id FROM brands WHERE name = 'Vans'), (SELECT id FROM shoe_categories WHERE name = 'Casual'), 'Old Skool', 'The classic side stripe shoe', 'UNISEX', 60.00),
((SELECT id FROM brands WHERE name = 'Vans'), (SELECT id FROM shoe_categories WHERE name = 'Sneakers'), 'Authentic', 'The original and now iconic Vans style', 'UNISEX', 50.00)
ON CONFLICT DO NOTHING;

-- ===============================
-- SHOE MODELS (Variants) - Using online images
-- ===============================
INSERT INTO shoe_models (shoe_id, model_name, color, material, sku, price, image_url) VALUES
-- Nike Air Max 270 variants
((SELECT id FROM shoes WHERE name = 'Air Max 270' AND brand_id = (SELECT id FROM brands WHERE name = 'Nike')), 'Air Max 270 Black/White', 'Black', 'Mesh/Synthetic', 'NIKE-AM270-BW-001', 150.00, 'https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/awjogtdnqxniqqk0wpgf/air-max-270-shoes-KkLcGR.png'),
((SELECT id FROM shoes WHERE name = 'Air Max 270' AND brand_id = (SELECT id FROM brands WHERE name = 'Nike')), 'Air Max 270 White/Blue', 'White', 'Mesh/Synthetic', 'NIKE-AM270-WB-002', 150.00, 'https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/191b8ca2-8224-406c-9168-0a72e0d97d7a/air-max-270-shoes-KkLcGR.png'),

-- Nike Air Jordan 1 variants
((SELECT id FROM shoes WHERE name = 'Air Jordan 1' AND brand_id = (SELECT id FROM brands WHERE name = 'Nike')), 'Air Jordan 1 Bred', 'Black/Red', 'Leather', 'NIKE-AJ1-BR-001', 170.00, 'https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/b7d9211c-26e7-431a-ac24-b0540fb3c00f/air-jordan-1-retro-high-og-shoes-Mh3Vq7.png'),
((SELECT id FROM shoes WHERE name = 'Air Jordan 1' AND brand_id = (SELECT id FROM brands WHERE name = 'Nike')), 'Air Jordan 1 Royal', 'Blue/White', 'Leather', 'NIKE-AJ1-RY-002', 170.00, 'https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/00375837-849f-4f17-ba24-d201d27be33b/air-jordan-1-retro-high-og-shoes-6gdVwd.png'),

-- Nike Air Force 1 variants
((SELECT id FROM shoes WHERE name = 'Air Force 1' AND brand_id = (SELECT id FROM brands WHERE name = 'Nike')), 'Air Force 1 Triple White', 'White', 'Leather', 'NIKE-AF1-TW-001', 110.00, 'https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/b7d9211c-26e7-431a-ac24-b0540fb3c00f/air-force-1-07-shoes-WrLlWX.png'),
((SELECT id FROM shoes WHERE name = 'Air Force 1' AND brand_id = (SELECT id FROM brands WHERE name = 'Nike')), 'Air Force 1 Triple Black', 'Black', 'Leather', 'NIKE-AF1-TB-002', 110.00, 'https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/00375837-849f-4f17-ba24-d201d27be33b/air-force-1-07-shoes-WrLlWX.png'),

-- Adidas Ultraboost 22 variants
((SELECT id FROM shoes WHERE name = 'Ultraboost 22' AND brand_id = (SELECT id FROM brands WHERE name = 'Adidas')), 'Ultraboost 22 Core Black', 'Black', 'Primeknit', 'ADIDAS-UB22-CB-001', 180.00, 'https://assets.adidas.com/images/h_840,f_auto,q_auto,fl_lossy,c_fill,g_auto/fbaf991a02684e72b0d1ae8800fd380e_9366/Ultraboost_22_Shoes_Black_GZ0127_01_standard.jpg'),
((SELECT id FROM shoes WHERE name = 'Ultraboost 22' AND brand_id = (SELECT id FROM brands WHERE name = 'Adidas')), 'Ultraboost 22 Cloud White', 'White', 'Primeknit', 'ADIDAS-UB22-CW-002', 180.00, 'https://assets.adidas.com/images/h_840,f_auto,q_auto,fl_lossy,c_fill,g_auto/c21a78fbd6ba4bb5b4e1ad2900ffa8c6_9366/Ultraboost_22_Shoes_White_GZ0127_01_standard.jpg'),

-- Adidas Stan Smith variants
((SELECT id FROM shoes WHERE name = 'Stan Smith' AND brand_id = (SELECT id FROM brands WHERE name = 'Adidas')), 'Stan Smith White/Green', 'White/Green', 'Leather', 'ADIDAS-SS-WG-001', 80.00, 'https://assets.adidas.com/images/h_840,f_auto,q_auto,fl_lossy,c_fill,g_auto/fb4cc2c63f8b4b5fb30faee3008cd960_9366/Stan_Smith_Shoes_White_FX5500_01_standard.jpg'),
((SELECT id FROM shoes WHERE name = 'Stan Smith' AND brand_id = (SELECT id FROM brands WHERE name = 'Adidas')), 'Stan Smith White/Navy', 'White/Navy', 'Leather', 'ADIDAS-SS-WN-002', 80.00, 'https://assets.adidas.com/images/h_840,f_auto,q_auto,fl_lossy,c_fill,g_auto/962b9fb6fbcb4c1ea3b6aef5008aa93b_9366/Stan_Smith_Shoes_White_FX5501_01_standard.jpg'),

-- Vans Old Skool variants
((SELECT id FROM shoes WHERE name = 'Old Skool' AND brand_id = (SELECT id FROM brands WHERE name = 'Vans')), 'Old Skool Black/White', 'Black', 'Canvas/Suede', 'VANS-OS-BW-001', 60.00, 'https://images.vans.com/is/image/Vans/VN000D3HY28-HERO?$583x583')
ON CONFLICT (sku) DO NOTHING;

-- ===============================
-- INVENTORY DATA
-- ===============================
-- Add inventory for each shoe model with various sizes
INSERT INTO shoe_inventory (shoe_model_id, size, quantity_available)
SELECT
    sm.id,
    sizes.size,
    FLOOR(RANDOM() * 20) + 5 as quantity_available
FROM shoe_models sm
CROSS JOIN (
    VALUES ('6'), ('6.5'), ('7'), ('7.5'), ('8'), ('8.5'), ('9'), ('9.5'), ('10'), ('10.5'), ('11'), ('11.5'), ('12')
) AS sizes(size)
ON CONFLICT (shoe_model_id, size) DO NOTHING;

-- ===============================
-- SAMPLE ADDRESSES
-- ===============================
INSERT INTO user_addresses (user_id, address_line_1, city, state, postal_code, country, is_default) VALUES
((SELECT id FROM users WHERE email = 'user@example.com'), 'Dizengoff Street 50', 'Tel Aviv', 'Tel Aviv', '6436123', 'Israel', true),
((SELECT id FROM users WHERE email = 'admin@shoestore.com'), 'Rothschild Boulevard 10', 'Tel Aviv', 'Tel Aviv', '6434012', 'Israel', true)
ON CONFLICT DO NOTHING;



