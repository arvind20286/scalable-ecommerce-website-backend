-- Please adjust table and column names to match your actual database schema.
-- The values provided are for demonstration purposes.

-- Brands
INSERT INTO brands (brand_name) VALUES ('GenericBrand'), ('SuperBrand');

-- Colours
INSERT INTO colours (colour_name) VALUES ('Red'), ('Blue'), ('Green'), ('Black'), ('White');

-- Size Categories (e.g., Men's Shoes, Women's Tops)
INSERT INTO size_categories (category_name) VALUES ('Apparel'), ('Shoes');

-- Size Options
-- Assuming size_options has a foreign key to size_categories
INSERT INTO size_options (size_value, category_id) VALUES
('S', 1), ('M', 1), ('L', 1), ('XL', 1),
('8', 2), ('9', 2), ('10', 2), ('11', 2);

-- Attribute Types (e.g., Material, Style)
INSERT INTO attribute_types (type_name) VALUES ('Material'), ('Sleeve Style');

-- Attribute Options
-- Assuming attribute_options has a foreign key to attribute_types
INSERT INTO attribute_options (option_value, attribute_type_id) VALUES
('Cotton', 1), ('Polyester', 1),
('Short Sleeve', 2), ('Long Sleeve', 2);

-- Product Categories
INSERT INTO product_categories (category_name, parent_category_id) VALUES
('Electronics', NULL),
('Fashion', NULL),
('Smartphones', 1),
('Laptops', 1),
('Men''s Apparel', 2),
('Women''s Apparel', 2);

-- Products
-- Assuming products has foreign keys to brands and product_categories
INSERT INTO products (product_name, description, brand_id, category_id) VALUES
('Cool T-Shirt', 'A very cool t-shirt made from premium cotton.', 1, 5),
('Warm Jacket', 'A stylish and warm jacket for winter.', 2, 5),
('Smartphone Pro', 'The latest and greatest smartphone.', 2, 3);

-- Product Items
-- This table often represents a specific variant of a product.
-- It might have price, stock (SKU), and other details.
-- Assuming product_items has a foreign key to products
INSERT INTO product_items (product_id, sku, quantity_in_stock, original_price, sale_price) VALUES
(1, 'TSHIRT-RED-S', 100, 25.00, 19.99),
(1, 'TSHIRT-BLUE-M', 120, 25.00, 19.99),
(2, 'JACKET-BLK-L', 50, 150.00, 129.99),
(3, 'SMPH-PRO-64', 200, 999.00, 949.00);

-- Product Images
-- Assuming product_images has a foreign key to product_items to link an image to a specific variant
INSERT INTO product_images (product_item_id, image_url) VALUES
(1, 'http://example.com/images/tshirt_red.jpg'),
(2, 'http://example.com/images/tshirt_blue.jpg'),
(3, 'http://example.com/images/jacket_black.jpg'),
(4, 'http://example.com/images/smartphone_pro.jpg');

-- Product Variations
-- This is a linking table to connect a product_item with its specific options (like size and color).
-- Assuming product_variations has foreign keys to product_items and attribute_options/size_options/colour etc.
-- This structure can vary greatly. A common approach is a product_item_variations table.
-- For simplicity, let's assume a link to product_item and an option.
-- Let's use a simplified model where we link to size_options and colours.
-- This part is highly dependent on your actual schema.
-- A better model might have a `product_configurations` table.
-- Example for 'Cool T-Shirt' (product_id 1)
INSERT INTO product_variations (product_item_id, size_option_id, colour_id) VALUES
(1, 1, 1), -- T-Shirt, Size S, Color Red
(2, 2, 2); -- T-Shirt, Size M, Color Blue

-- Reviews
-- Assuming reviews has foreign keys to products and a user_id (which we'll mock)
INSERT INTO reviews (product_id, user_id, rating, comment) VALUES
(1, 101, 5, 'Excellent t-shirt, very comfortable!'),
(1, 102, 4, 'Good quality, but it shrunk a little after washing.'),
(2, 103, 5, 'Keeps me very warm, great for the price.'),
(3, 104, 5, 'This phone is amazing! Super fast and great camera.');

