-- 1. Categories
INSERT INTO categories
(category_id, category_name, description)
VALUES
(1, 'Smartphone', 'Smartphone category'),
(2, 'Laptop', 'Laptop category'),
(3, 'Accessories', 'Accessories category')
ON DUPLICATE KEY UPDATE
category_name = VALUES(category_name),
description = VALUES(description);

-- 2. Products
INSERT INTO products
(product_id, product_name, description, price,
 stock_quantity, image_url, category_id)
VALUES
(101, 'iPhone 16', 'Apple smartphone',
 20000000, 10, 'https://example.com/iphone16.jpg', 1),

(202, 'Samsung S25', 'Samsung smartphone',
 18000000, 15, 'https://example.com/s25.jpg', 1),

(303, 'MacBook Air M4', 'Apple laptop',
 27000000, 5, 'https://example.com/macbook.jpg', 2)
ON DUPLICATE KEY UPDATE
product_name = VALUES(product_name),
price = VALUES(price);

-- 3. Roles
INSERT INTO roles (role_id, role_name)
VALUES
(1, 'USER'),
(2, 'ADMIN')
ON DUPLICATE KEY UPDATE
role_name = VALUES(role_name);
