-- ===============================
-- INITIAL DATA FOR SHOE STORE
-- ===============================

-- ===============================
-- ADMIN USER
-- ===============================
-- Insert admin user (password: admin123)
-- BCrypt hash for 'admin123'
INSERT INTO users (email, password_hash, first_name, last_name, is_admin, created_at)
VALUES ('admin@shoestore.com', '$2a$12$hJSRYWV/lXYjIbwsji/9zeivqrmqq9lZ0dVdDWZc6vQqYLaObyIqG', 'Admin', 'User', true, NOW())
ON CONFLICT (email) DO NOTHING;

-- Insert regular test user (password: user123)
-- BCrypt hash for 'user123'
INSERT INTO users (email, password_hash, first_name, last_name, is_admin, created_at)
VALUES ('user@example.com', '$2a$12$VXUIxd0f7/NpU/ZS3nLF7.k.dJQ8w1BzGMQ8QP2gGJhZQb6aGVe5K', 'John', 'Doe', false, NOW())
ON CONFLICT (email) DO NOTHING;

-- ===============================
-- BRANDS - Adding logo URLs
-- ===============================
INSERT INTO brands (name, description, logo_url, is_active, created_at) VALUES
('Nike', 'Just Do It - Leading athletic footwear and apparel brand', 'https://logos-world.net/wp-content/uploads/2020/04/Nike-Logo.png', true, NOW()),
('Adidas', 'Impossible is Nothing - German multinational corporation', 'https://logos-world.net/wp-content/uploads/2020/04/Adidas-Logo.png', true, NOW()),
('Puma', 'Forever Faster - German multinational corporation', 'https://logos-world.net/wp-content/uploads/2020/05/Puma-Logo.png', true, NOW()),
('New Balance', 'Endorsed by No One - American multinational corporation', 'https://logos-world.net/wp-content/uploads/2020/09/New-Balance-Logo.png', true, NOW()),
('Vans', 'Off The Wall - American manufacturer of skateboarding shoes', 'https://logos-world.net/wp-content/uploads/2020/05/Vans-Logo.png', true, NOW()),
('Converse', 'American shoe company known for Chuck Taylor All-Stars', 'https://logos-world.net/wp-content/uploads/2020/06/Converse-Logo.png', true, NOW()),
('Reebok', 'Be More Human - American-inspired global brand', 'https://logos-world.net/wp-content/uploads/2020/06/Reebok-Logo.png', true, NOW()),
('ASICS', 'Japanese multinational corporation which produces footwear', 'https://logos-world.net/wp-content/uploads/2020/09/ASICS-Logo.png', true, NOW())
ON CONFLICT (name) DO NOTHING;

-- ===============================
-- CATEGORIES
-- ===============================
INSERT INTO shoe_categories (name, description, is_active, created_at) VALUES
('Running', 'Athletic shoes designed for running and jogging', true, NOW()),
('Basketball', 'High-performance shoes designed for basketball', true, NOW()),
('Casual', 'Everyday comfortable shoes for daily wear', true, NOW()),
('Formal', 'Dress shoes for formal occasions and business', true, NOW()),
('Sneakers', 'Casual athletic shoes for everyday wear', true, NOW()),
('Hiking', 'Durable shoes designed for hiking and outdoor activities', true, NOW()),
('Boots', 'High-ankle shoes for various occasions', true, NOW()),
('Sandals', 'Open-toe shoes for summer and casual wear', true, NOW()),
('Training', 'Cross-training shoes for gym and fitness activities', true, NOW())
ON CONFLICT (name) DO NOTHING;

-- ===============================
-- SHOES (Base Models)
-- ===============================
INSERT INTO shoes (brand_id, category_id, name, description, gender, base_price, is_active, created_at) VALUES
-- Nike Products
((SELECT id FROM brands WHERE name = 'Nike'), (SELECT id FROM shoe_categories WHERE name = 'Running'), 'Air Max 270', 'Nike Air Max 270 features the largest heel Air unit yet and delivers incredible all-day comfort', 'UNISEX', 150.00, true, NOW()),
((SELECT id FROM brands WHERE name = 'Nike'), (SELECT id FROM shoe_categories WHERE name = 'Basketball'), 'Air Jordan 1', 'The Air Jordan 1 High remakes the classic sneaker, giving you a fresh take on what you know', 'UNISEX', 170.00, true, NOW()),
((SELECT id FROM brands WHERE name = 'Nike'), (SELECT id FROM shoe_categories WHERE name = 'Casual'), 'Air Force 1', 'The radiance lives on in the Nike Air Force 1, the basketball original that puts a fresh spin on what you know best', 'UNISEX', 110.00, true, NOW()),

-- Adidas Products
((SELECT id FROM brands WHERE name = 'Adidas'), (SELECT id FROM shoe_categories WHERE name = 'Running'), 'Ultraboost 22', 'More energy, more cushioning, more responsiveness. This is Ultraboost 22', 'UNISEX', 180.00, true, NOW()),
((SELECT id FROM brands WHERE name = 'Adidas'), (SELECT id FROM shoe_categories WHERE name = 'Casual'), 'Stan Smith', 'Clean and simple, the Stan Smith shoes are a versatile staple', 'UNISEX', 80.00, true, NOW()),
((SELECT id FROM brands WHERE name = 'Adidas'), (SELECT id FROM shoe_categories WHERE name = 'Sneakers'), 'Gazelle', 'A timeless design rooted in football culture and street style', 'UNISEX', 90.00, true, NOW()),

-- Puma Products
((SELECT id FROM brands WHERE name = 'Puma'), (SELECT id FROM shoe_categories WHERE name = 'Running'), 'RS-X', 'The ultimate expression of fresh street style meets sport-inspired design', 'UNISEX', 110.00, true, NOW()),
((SELECT id FROM brands WHERE name = 'Puma'), (SELECT id FROM shoe_categories WHERE name = 'Casual'), 'Suede Classic', 'An icon of street culture and basketball courts', 'UNISEX', 70.00, true, NOW()),

-- New Balance Products
((SELECT id FROM brands WHERE name = 'New Balance'), (SELECT id FROM shoe_categories WHERE name = 'Running'), '990v5', 'The quintessential American running shoe', 'UNISEX', 175.00, true, NOW()),
((SELECT id FROM brands WHERE name = 'New Balance'), (SELECT id FROM shoe_categories WHERE name = 'Casual'), '327', 'Inspired by the no-nonsense design philosophy of the 1970s', 'UNISEX', 90.00, true, NOW()),

-- Vans Products
((SELECT id FROM brands WHERE name = 'Vans'), (SELECT id FROM shoe_categories WHERE name = 'Casual'), 'Old Skool', 'The classic side stripe shoe', 'UNISEX', 60.00, true, NOW()),
((SELECT id FROM brands WHERE name = 'Vans'), (SELECT id FROM shoe_categories WHERE name = 'Sneakers'), 'Authentic', 'The original and now iconic Vans style', 'UNISEX', 50.00, true, NOW())
ON CONFLICT DO NOTHING;

-- ===============================
-- SHOE MODELS (Variants) - Using online images
-- ===============================
INSERT INTO shoe_models (shoe_id, model_name, color, material, sku, price, image_url, is_active, created_at) VALUES
-- Nike Air Max 270 variants
((SELECT id FROM shoes WHERE name = 'Air Max 270' AND brand_id = (SELECT id FROM brands WHERE name = 'Nike')), 'Air Max 270 Black/White', 'Black', 'Mesh/Synthetic', 'NIKE-AM270-BW-001', 150.00, 'https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/skwgyqrbfzhu6uyeh0gg/AIR+MAX+270.png', true, NOW()),
((SELECT id FROM shoes WHERE name = 'Air Max 270' AND brand_id = (SELECT id FROM brands WHERE name = 'Nike')), 'Air Max 270 White/Blue', 'White', 'Mesh/Synthetic', 'NIKE-AM270-WB-002', 150.00, 'https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/45743c09-2f1d-4118-b24c-04eb3f5d37ad/AIR+MAX+270.png', true, NOW()),

-- Nike Air Jordan 1 variants
((SELECT id FROM shoes WHERE name = 'Air Jordan 1' AND brand_id = (SELECT id FROM brands WHERE name = 'Nike')), 'Air Jordan 1 Bred', 'Black/Red', 'Leather', 'NIKE-AJ1-BR-001', 170.00, 'https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco,u_126ab356-44d8-4a06-89b4-fcdcc8df0245,c_scale,fl_relative,w_1.0,h_1.0,fl_layer_apply/4863c242-dc07-4fa6-992d-cb20da58189d/WMNS+AIR+JORDAN+1+RETRO+HI+OG.png', true, NOW()),
((SELECT id FROM shoes WHERE name = 'Air Jordan 1' AND brand_id = (SELECT id FROM brands WHERE name = 'Nike')), 'Air Jordan 1 Royal', 'Blue/White', 'Leather', 'NIKE-AJ1-RY-002', 170.00, 'https://static.nike.com/a/images/c_limit,w_592,f_auto/t_product_v1/u_126ab356-44d8-4a06-89b4-fcdcc8df0245,c_scale,fl_relative,w_1.0,h_1.0,fl_layer_apply/a529d9f4-c5e8-42df-8c0c-cb8f53efee6d/AIR+JORDAN+1+MID+SE+%28GS%29.png', true, NOW()),

-- Nike Air Force 1 variants
((SELECT id FROM shoes WHERE name = 'Air Force 1' AND brand_id = (SELECT id FROM brands WHERE name = 'Nike')), 'Air Force 1 Triple White', 'White', 'Leather', 'NIKE-AF1-TW-001', 110.00, 'https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/b7d9211c-26e7-431a-ac24-b0540fb3c00f/AIR+FORCE+1+%2707.png', true, NOW()),
((SELECT id FROM shoes WHERE name = 'Air Force 1' AND brand_id = (SELECT id FROM brands WHERE name = 'Nike')), 'Air Force 1 Triple Black', 'Black', 'Leather', 'NIKE-AF1-TB-002', 110.00, 'https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/fc4622c4-2769-4665-aa6e-42c974a7705e/AIR+FORCE+1+%2707.png', true, NOW()),

-- Adidas Ultraboost 22 variants
((SELECT id FROM shoes WHERE name = 'Ultraboost 22' AND brand_id = (SELECT id FROM brands WHERE name = 'Adidas')), 'Ultraboost 22 Core Black', 'Black', 'Primeknit', 'ADIDAS-UB22-CB-001', 180.00, 'https://assets.adidas.com/images/w_450,f_auto,q_auto/c99ea425d0484c0c9bca3d451d9e1579_9366/IH2637_HM1.jpg', true, NOW()),
((SELECT id FROM shoes WHERE name = 'Ultraboost 22' AND brand_id = (SELECT id FROM brands WHERE name = 'Adidas')), 'Ultraboost 22 Cloud White', 'White', 'Primeknit', 'ADIDAS-UB22-CW-002', 180.00, 'https://assets.adidas.com/images/w_450,f_auto,q_auto/efd28e028baf4d6e934c9e258d5993fc_9366/ID8813_HM1.jpg', true, NOW()),

-- Adidas Stan Smith variants
((SELECT id FROM shoes WHERE name = 'Stan Smith' AND brand_id = (SELECT id FROM brands WHERE name = 'Adidas')), 'Stan Smith White/Green', 'White/Green', 'Leather', 'ADIDAS-SS-WG-001', 80.00, 'https://assets.adidas.com/images/w_600,f_auto,q_auto/69721f2e7c934d909168a80e00818569_9366/Stan_Smith_Shoes_White_M20324_01_standard.jpg', true, NOW()),
((SELECT id FROM shoes WHERE name = 'Stan Smith' AND brand_id = (SELECT id FROM brands WHERE name = 'Adidas')), 'Stan Smith White/Navy', 'White/Navy', 'Leather', 'ADIDAS-SS-WN-002', 80.00, 'https://assets.adidas.com/images/w_600,f_auto,q_auto/4edaa6d5b65a40d19f20a7fa00ea641f_9366/Stan_Smith_Shoes_White_M20325_01_standard.jpg', true, NOW()),

-- Vans Old Skool variants
((SELECT id FROM shoes WHERE name = 'Old Skool' AND brand_id = (SELECT id FROM brands WHERE name = 'Vans')), 'Old Skool Black/White', 'Black', 'Canvas/Suede', 'VANS-OS-BW-001', 60.00, 'https://assets.vans.com/images/t_img/c_fill,g_center,f_auto,h_573,w_458,e_unsharp_mask:100/dpr_2.0/v1747942442/VN000D3HY28-HERO/Old-Skool-Shoe.png', true, NOW()),

-- Puma RS-X variants
((SELECT id FROM shoes WHERE name = 'RS-X' AND brand_id = (SELECT id FROM brands WHERE name = 'Puma')), 'RS-X³ Puzzle', 'Multi', 'Mesh/Synthetic', 'PUMA-RSX-MP-001', 110.00, 'https://images.puma.com/image/upload/f_auto,q_auto,b_rgb:fafafa,w_2000,h_2000/global/371570/01/sv01/fnd/PNA/fmt/png/RS-X%C2%B3-Puzzle-Men''s-Sneakers.avif', true, NOW()),
((SELECT id FROM shoes WHERE name = 'RS-X' AND brand_id = (SELECT id FROM brands WHERE name = 'Puma')), 'RS-X Core', 'Black/White', 'Mesh/Synthetic', 'PUMA-RSX-BW-002', 110.00, 'https://images.puma.com/image/upload/f_auto,q_auto,b_rgb:fafafa,w_600,h_600/global/369666/01/sv01/fnd/PNA/fmt/png/RS-X-Core-Men''s-Sneakers.jpg', true, NOW()),

-- Puma Suede variants
((SELECT id FROM shoes WHERE name = 'Suede Classic' AND brand_id = (SELECT id FROM brands WHERE name = 'Puma')), 'Suede Classic XXI', 'Peacoat', 'Suede', 'PUMA-SC-PC-001', 70.00, 'https://images.puma.com/image/upload/f_auto,q_auto,b_rgb:fafafa,w_2000,h_2000/global/374915/25/sv01/fnd/PNA/fmt/png/Suede-Classic-XXI-Sneakers', true, NOW()),
((SELECT id FROM shoes WHERE name = 'Suede Classic' AND brand_id = (SELECT id FROM brands WHERE name = 'Puma')), 'Suede Classic Team Gold', 'Team Gold', 'Suede', 'PUMA-SC-TG-002', 70.00, 'https://images.puma.com/image/upload/f_auto,q_auto,b_rgb:fafafa,w_2000,h_2000/global/365347/02/sv01/fnd/PNA/fmt/png/Suede-Classic-Sneakers', true, NOW()),

-- New Balance 990v5 variants
((SELECT id FROM shoes WHERE name = '990v5' AND brand_id = (SELECT id FROM brands WHERE name = 'New Balance')), '990v5 Grey', 'Grey', 'Mesh/Suede', 'NB-990V5-GR-001', 175.00, 'https://nb.scene7.com/is/image/NB/m990gl5_nb_02_i?$pdpflexf2$&qlt=80&fmt=webp&wid=440&hei=440', true, NOW()),
((SELECT id FROM shoes WHERE name = '990v5' AND brand_id = (SELECT id FROM brands WHERE name = 'New Balance')), '990v5 Navy', 'Navy', 'Mesh/Suede', 'NB-990V5-NV-002', 175.00, 'https://nb.scene7.com/is/image/NB/m990nv5_nb_02_i?$pdpflexf2$&qlt=80&fmt=webp&wid=440&hei=440', true, NOW()),

-- New Balance 327 variants
((SELECT id FROM shoes WHERE name = '327' AND brand_id = (SELECT id FROM brands WHERE name = 'New Balance')), '327 Moonbeam', 'Moonbeam', 'Nylon/Suede', 'NB-327-MB-001', 90.00, 'https://nb.scene7.com/is/image/NB/ms327cla_nb_02_i?$pdpflexf2$&qlt=80&fmt=webp&wid=440&hei=440', true, NOW()),
((SELECT id FROM shoes WHERE name = '327' AND brand_id = (SELECT id FROM brands WHERE name = 'New Balance')), '327 Primary Pack Blue', 'Blue', 'Nylon/Suede', 'NB-327-BL-002', 90.00, 'https://nb.scene7.com/is/image/NB/ms327fe_nb_02_i?$pdpflexf2$&qlt=80&fmt=webp&wid=440&hei=440', true, NOW()),

-- Vans Authentic variants
((SELECT id FROM shoes WHERE name = 'Authentic' AND brand_id = (SELECT id FROM brands WHERE name = 'Vans')), 'Authentic Black', 'Black', 'Canvas', 'VANS-AUTH-BK-001', 50.00, 'https://assets.vans.com/images/t_img/c_fill,g_center,f_auto,h_573,w_458,e_unsharp_mask:100/dpr_2.0/v1742247852/VN000EE3BKA-HERO/Authentic-Shoe.png', true, NOW()),
((SELECT id FROM shoes WHERE name = 'Authentic' AND brand_id = (SELECT id FROM brands WHERE name = 'Vans')), 'Authentic True White', 'White', 'Canvas', 'VANS-AUTH-WH-002', 50.00, 'https://assets.vans.com/images/t_img/c_fill,g_center,f_auto,h_573,w_458,e_unsharp_mask:100/dpr_2.0/v1740713845/VN000EE3W00-HERO/Authentic-Shoe.png', true, NOW()),

-- Adidas Gazelle variants
((SELECT id FROM shoes WHERE name = 'Gazelle' AND brand_id = (SELECT id FROM brands WHERE name = 'Adidas')), 'Gazelle Core Black', 'Black', 'Suede', 'ADIDAS-GAZ-CB-001', 90.00, 'https://assets.adidas.com/images/w_1880,f_auto,q_auto/97f86eede1374615a058a81700a27444_9366/CQ2809_01_standard.jpg', true, NOW()),
((SELECT id FROM shoes WHERE name = 'Gazelle' AND brand_id = (SELECT id FROM brands WHERE name = 'Adidas')), 'Gazelle Collegiate Navy', 'Navy', 'Suede', 'ADIDAS-GAZ-CN-002', 90.00, 'https://assets.adidas.com/images/w_1880,f_auto,q_auto/698e41ae0196408eb16aa7fb008046ad_9366/BB5478_01_standard.jpg', true, NOW())

ON CONFLICT (sku) DO NOTHING;


-- ===============================
-- INVENTORY DATA
-- ===============================
-- Add inventory for each shoe model with various sizes
INSERT INTO shoe_inventory (shoe_model_id, size, quantity_available, quantity_reserved, created_at)
SELECT
    sm.id,
    sizes.size,
    FLOOR(RANDOM() * 20) + 5 as quantity_available,
    0 as quantity_reserved,
    NOW() as created_at
FROM shoe_models sm
CROSS JOIN (
    VALUES ('6'), ('6.5'), ('7'), ('7.5'), ('8'), ('8.5'), ('9'), ('9.5'), ('10'), ('10.5'), ('11'), ('11.5'), ('12')
) AS sizes(size)
ON CONFLICT (shoe_model_id, size) DO NOTHING;

-- ===============================
-- SAMPLE ADDRESSES
-- ===============================
INSERT INTO user_addresses (user_id, address_line_1, city, state, postal_code, country, phone_number, first_name, last_name, email, is_default, created_at) VALUES
((SELECT id FROM users WHERE email = 'user@example.com'), 'Dizengoff Street 50', 'Tel Aviv', 'Tel Aviv', '6436123', 'Israel', '050-1234567', 'John', 'Doe', 'user@example.com', true, NOW()),
((SELECT id FROM users WHERE email = 'admin@shoestore.com'), 'Rothschild Boulevard 10', 'Tel Aviv', 'Tel Aviv', '6434012', 'Israel', '050-7654321', 'Admin', 'User', 'admin@shoestore.com', true, NOW())
ON CONFLICT DO NOTHING;


-- ===============================
-- SETTING VERSION TO 0 FOR NULL VALUES
-- ===============================
-- This is to ensure that all version fields are initialized properly
UPDATE users SET version = 0 WHERE version IS NULL;
UPDATE brands SET version = 0 WHERE version IS NULL;
UPDATE shoe_categories SET version = 0 WHERE version IS NULL;
UPDATE shoes SET version = 0 WHERE version IS NULL;
UPDATE shoe_models SET version = 0 WHERE version IS NULL;
UPDATE shoe_inventory SET version = 0 WHERE version IS NULL;
UPDATE user_addresses SET version = 0 WHERE version IS NULL;