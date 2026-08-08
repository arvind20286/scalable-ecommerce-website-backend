-- =====================================================================
-- Seed data for product_service_api
-- 5 brands, 20 products, product items/variations, images, attributes,
-- categories (with parent/sub hierarchy), colours, sizes, reviews.
--
-- Table/column names below follow Spring Boot's default snake_case
-- naming strategy applied to the entities you shared. If your actual
-- schema differs (e.g. a custom naming strategy), adjust accordingly.
-- =====================================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- 1. BRAND
-- ---------------------------------------------------------------------
INSERT INTO brand (id, brand_name, brand_description) VALUES
(1, 'Nike', 'Global sports and lifestyle brand'),
(2, 'Adidas', 'Sportswear and footwear brand'),
(3, 'Puma', 'Athletic and casual footwear brand'),
(4, 'Zara', 'Fast fashion clothing brand'),
(5, 'H&M', 'Affordable fashion retailer');

-- ---------------------------------------------------------------------
-- 2. COLOUR
-- ---------------------------------------------------------------------
INSERT INTO colour (id, colour_name) VALUES
(1, 'Black'),
(2, 'White'),
(3, 'Red'),
(4, 'Blue'),
(5, 'Grey');

-- ---------------------------------------------------------------------
-- 3. SIZE_CATEGORY
-- ---------------------------------------------------------------------
INSERT INTO size_category (id, category_name) VALUES
(1, 'Clothing'),
(2, 'Footwear');

-- ---------------------------------------------------------------------
-- 4. SIZE_OPTION
-- ---------------------------------------------------------------------
INSERT INTO size_option (id, size_name, size_order, size_category_id) VALUES
(1, 'S', 1, 1),
(2, 'M', 2, 1),
(3, 'L', 3, 1),
(4, 'XL', 4, 1),
(5, '7', 1, 2),
(6, '8', 2, 2),
(7, '9', 3, 2),
(8, '10', 4, 2),
(9, '11', 5, 2);

-- ---------------------------------------------------------------------
-- 5. ATTRIBUTE_TYPE
-- ---------------------------------------------------------------------
INSERT INTO attribute_type (id, attribute_name) VALUES
(1, 'Material'),
(2, 'Fit');

-- ---------------------------------------------------------------------
-- 6. ATTRIBUTE_OPTION
-- ---------------------------------------------------------------------
INSERT INTO attribute_option (id, name, attribute_type_id) VALUES
(1, 'Cotton', 1),
(2, 'Polyester', 1),
(3, 'Leather', 1),
(4, 'Slim Fit', 2),
(5, 'Regular Fit', 2),
(6, 'Loose Fit', 2);

-- ---------------------------------------------------------------------
-- 7. PRODUCT_CATEGORY (parents first, then sub-categories)
-- ---------------------------------------------------------------------
INSERT INTO product_category (id, category, size_category_id, category_description, parent_category_id) VALUES
(1, 'Men', NULL, 'Men''s fashion', NULL),
(2, 'Women', NULL, 'Women''s fashion', NULL);

INSERT INTO product_category (id, category, size_category_id, category_description, parent_category_id) VALUES
(3, 'Men T-Shirts', 1, 'Men''s casual and sports T-shirts', 1),
(4, 'Men Shoes', 2, 'Men''s footwear', 1),
(5, 'Women Dresses', 1, 'Women''s dresses', 2),
(6, 'Women Shoes', 2, 'Women''s footwear', 2);

-- ---------------------------------------------------------------------
-- 8. PRODUCTS (20)
-- ---------------------------------------------------------------------
INSERT INTO products (id, name, description, brand_id, product_category_id) VALUES
(1,  'Nike Dri-FIT Running Tee',        'Lightweight breathable running t-shirt', 1, 3),
(2,  'Nike Air Max 90',                 'Iconic cushioned lifestyle sneaker',      1, 4),
(3,  'Nike Revolution 6',               'Everyday comfort running shoe',           1, 4),
(4,  'Nike Essential Cotton Tee',       'Soft cotton crew neck tee',               1, 3),
(5,  'Adidas Ultraboost 22',            'Responsive boost cushioning running shoe',2, 4),
(6,  'Adidas Originals Trefoil Tee',    'Classic trefoil logo tee',                2, 3),
(7,  'Adidas Stan Smith',               'Timeless leather tennis sneaker',         2, 4),
(8,  'Adidas Tiro Track Tee',           'Sporty training t-shirt',                 2, 3),
(9,  'Puma RS-X Sneakers',              'Bold retro-inspired chunky sneaker',      3, 4),
(10, 'Puma Essentials Logo Tee',        'Everyday logo tee',                       3, 3),
(11, 'Puma Suede Classic',              'Iconic suede casual sneaker',             3, 4),
(12, 'Puma Active Tee',                 'Stretch training tee',                    3, 3),
(13, 'Zara Floral Summer Dress',        'Lightweight floral print dress',          4, 5),
(14, 'Zara Wrap Midi Dress',            'Elegant wrap-style midi dress',           4, 5),
(15, 'Zara Chunky Sole Sneakers',       'Trend chunky-sole sneaker',               4, 6),
(16, 'Zara Basic Slip Dress',           'Minimalist satin slip dress',             4, 5),
(17, 'H&M Cotton Shift Dress',          'Relaxed fit cotton shift dress',          5, 5),
(18, 'H&M Puff Sleeve Dress',           'Statement puff sleeve dress',             5, 5),
(19, 'H&M Canvas Sneakers',             'Casual everyday canvas sneaker',          5, 6),
(20, 'H&M Denim Wrap Dress',            'Denim wrap dress with tie waist',         5, 5);

-- ---------------------------------------------------------------------
-- 9. PRODUCT_ITEM (25 items - some products have 2 colour variants)
-- ---------------------------------------------------------------------
INSERT INTO product_item (id, original_price, sale_price, product_code, product_id, colour_id) VALUES
(1,  799.00,  699.00, 'NK-DFT-BLK-001', 1,  1),
(2,  8999.00, 7999.00,'NK-AM90-WHT-002',2,  2),
(3,  8999.00, 7999.00,'NK-AM90-BLK-003',2,  1),
(4,  4499.00, 3999.00,'NK-REV6-RED-004',3,  3),
(5,  699.00,  599.00, 'NK-ECT-WHT-005', 4,  2),
(6,  15999.00,13999.00,'AD-UB22-BLU-006',5, 4),
(7,  15999.00,13999.00,'AD-UB22-BLK-007',5, 1),
(8,  899.00,  799.00, 'AD-TRF-BLK-008', 6,  1),
(9,  7999.00, 6999.00,'AD-STS-WHT-009', 7,  2),
(10, 1299.00, 1099.00,'AD-TIR-GRY-010', 8,  5),
(11, 8999.00, 7499.00,'PM-RSX-RED-011', 9,  3),
(12, 8999.00, 7499.00,'PM-RSX-GRY-012', 9,  5),
(13, 599.00,  499.00, 'PM-ELT-BLK-013', 10, 1),
(14, 6499.00, 5499.00,'PM-SUD-WHT-014', 11, 2),
(15, 699.00,  599.00, 'PM-ACT-BLU-015', 12, 4),
(16, 2999.00, 2499.00,'ZA-FSD-RED-016', 13, 3),
(17, 2999.00, 2499.00,'ZA-FSD-BLU-017', 13, 4),
(18, 3499.00, 2999.00,'ZA-WMD-BLK-018', 14, 1),
(19, 5999.00, 4999.00,'ZA-CSS-WHT-019', 15, 2),
(20, 1999.00, 1699.00,'ZA-BSD-BLK-020', 16, 1),
(21, 1799.00, 1499.00,'HM-CSD-GRY-021', 17, 5),
(22, 1799.00, 1499.00,'HM-CSD-BLK-022', 17, 1),
(23, 2199.00, 1899.00,'HM-PSD-RED-023', 18, 3),
(24, 2999.00, 2499.00,'HM-CVS-WHT-024', 19, 2),
(25, 2499.00, 2099.00,'HM-DWD-BLU-025', 20, 4);

-- ---------------------------------------------------------------------
-- 10. PRODUCT_VARIATION (size + stock per item)
-- Clothing sizes: 1=S 2=M 3=L 4=XL | Footwear sizes: 5=7 6=8 7=9 8=10 9=11
-- ---------------------------------------------------------------------
INSERT INTO product_variation (product_item_id, size_option_id, stock) VALUES
-- item 1 (tee)
(1, 1, 20), (1, 2, 25), (1, 3, 15),
-- item 2 (shoe)
(2, 6, 10), (2, 7, 12), (2, 8, 8),
-- item 3 (shoe alt colour)
(3, 6, 5),  (3, 7, 9),  (3, 8, 7),
-- item 4 (shoe)
(4, 5, 14), (4, 6, 16), (4, 7, 10),
-- item 5 (tee)
(5, 1, 30), (5, 2, 28), (5, 3, 20),
-- item 6 (shoe)
(6, 6, 9),  (6, 7, 11), (6, 8, 6),
-- item 7 (shoe alt colour)
(7, 6, 6),  (7, 7, 8),  (7, 8, 5),
-- item 8 (tee)
(8, 2, 18), (8, 3, 20), (8, 4, 12),
-- item 9 (shoe)
(9, 5, 20), (9, 6, 22), (9, 7, 15),
-- item 10 (tee)
(10, 1, 25),(10, 2, 22),(10, 3, 18),
-- item 11 (shoe)
(11, 6, 11),(11, 7, 13),(11, 8, 9),
-- item 12 (shoe alt colour)
(12, 6, 7), (12, 7, 10),(12, 8, 6),
-- item 13 (tee)
(13, 1, 40),(13, 2, 35),(13, 3, 25),
-- item 14 (shoe)
(14, 5, 12),(14, 6, 14),(14, 7, 10),
-- item 15 (tee)
(15, 2, 20),(15, 3, 18),(15, 4, 14),
-- item 16 (dress)
(16, 1, 15),(16, 2, 18),(16, 3, 10),
-- item 17 (dress alt colour)
(17, 1, 10),(17, 2, 12),(17, 3, 8),
-- item 18 (dress)
(18, 1, 14),(18, 2, 16),(18, 3, 9),
-- item 19 (shoe)
(19, 5, 10),(19, 6, 12),(19, 7, 8),
-- item 20 (dress)
(20, 1, 20),(20, 2, 17),
-- item 21 (dress)
(21, 2, 16),(21, 3, 14),(21, 4, 10),
-- item 22 (dress alt colour)
(22, 2, 12),(22, 3, 10),(22, 4, 7),
-- item 23 (dress)
(23, 1, 18),(23, 2, 15),(23, 3, 11),
-- item 24 (shoe)
(24, 5, 9), (24, 6, 11),(24, 7, 7),
-- item 25 (dress)
(25, 1, 13),(25, 2, 15),(25, 3, 9);

-- ---------------------------------------------------------------------
-- 11. PRODUCT_IMAGES (2 images per item)
-- ---------------------------------------------------------------------
INSERT INTO product_images (product_item_id, url, preference)
SELECT id, CONCAT('https://cdn.example.com/products/', product_code, '/main.jpg'), 1 FROM product_item;

INSERT INTO product_images (product_item_id, url, preference)
SELECT id, CONCAT('https://cdn.example.com/products/', product_code, '/alt.jpg'), 2 FROM product_item;

-- ---------------------------------------------------------------------
-- 12. PRODUCT_ATTRIBUTE (material / fit per product)
-- ---------------------------------------------------------------------
INSERT INTO product_attribute (product_id, attribute_option_id) VALUES
(1, 1), (1, 5),
(2, 3),
(3, 3),
(4, 1),
(5, 3),
(6, 1), (6, 4),
(7, 3),
(8, 2), (8, 5),
(9, 3),
(10, 1),
(11, 3),
(12, 1), (12, 6),
(13, 2),
(14, 2),
(15, 3),
(16, 1),
(17, 1),
(18, 2),
(19, 3),
(20, 2);

-- ---------------------------------------------------------------------
-- 13. REVIEWS
-- ---------------------------------------------------------------------
INSERT INTO reviews (user_id, rating, comment, product_id, posted_at) VALUES
(101, 5, 'Super comfortable, great for daily runs.', 1, NOW()),
(102, 4, 'Good fit but runs slightly small.', 1, NOW()),
(103, 5, 'Classic sneaker, worth every penny.', 2, NOW()),
(104, 4, 'Stylish and comfortable for casual wear.', 3, NOW()),
(105, 5, 'Soft fabric, true to size.', 4, NOW()),
(106, 5, 'Best running shoes I have owned.', 5, NOW()),
(107, 4, 'Great cushioning, a bit pricey.', 5, NOW()),
(108, 3, 'Logo faded after a few washes.', 6, NOW()),
(109, 5, 'Timeless design, goes with everything.', 7, NOW()),
(110, 4, 'Good for training sessions.', 8, NOW()),
(111, 5, 'Eye-catching design, very comfortable.', 9, NOW()),
(112, 4, 'Nice everyday tee.', 10, NOW()),
(113, 5, 'Suede quality is excellent.', 11, NOW()),
(114, 3, 'Fabric feels a bit thin.', 12, NOW()),
(115, 5, 'Beautiful print, perfect for summer.', 13, NOW()),
(116, 4, 'Elegant fit, true to size.', 14, NOW()),
(117, 4, 'Trendy and comfortable to walk in.', 15, NOW()),
(118, 5, 'Simple and elegant, love it.', 16, NOW()),
(119, 4, 'Comfortable everyday dress.', 17, NOW()),
(120, 5, 'Sleeves add a nice statement look.', 18, NOW()),
(121, 4, 'Good casual sneakers for the price.', 19, NOW()),
(122, 5, 'Denim quality is great, fits well.', 20, NOW());

SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- Reset AUTO_INCREMENT counters so future Hibernate-generated inserts
-- don't collide with the explicit IDs used above.
-- ---------------------------------------------------------------------
ALTER TABLE brand AUTO_INCREMENT = 6;
ALTER TABLE colour AUTO_INCREMENT = 6;
ALTER TABLE size_category AUTO_INCREMENT = 3;
ALTER TABLE size_option AUTO_INCREMENT = 10;
ALTER TABLE attribute_type AUTO_INCREMENT = 3;
ALTER TABLE attribute_option AUTO_INCREMENT = 7;
ALTER TABLE product_category AUTO_INCREMENT = 7;
ALTER TABLE products AUTO_INCREMENT = 21;
ALTER TABLE product_item AUTO_INCREMENT = 26;
