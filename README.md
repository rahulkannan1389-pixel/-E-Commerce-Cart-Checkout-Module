# E-Commerce Cart & Checkout System

##  Features
- Add, update, remove cart items
- View cart with total price calculation
- Apply coupon (percentage / flat)
- Validate stock before checkout
- Order creation from cart
- Payment simulation (SUCCESS / FAILED)
- Inventory update
- Order history with pagination
##  API Endpoints

### Cart
- POST /api/cart
- GET /api/cart/{userId}
- PUT /api/cart
- DELETE /api/cart/item/{id}
- DELETE /api/cart/clear/{id}

### Checkout
- POST /api/checkout

### Orders
- GET /api/orders/user/{userId}
- GET /api/orders/{orderId}

### Coupon
- GET /api/coupons/{code}

##  Database Schema

### User Table
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    email VARCHAR(100)
);
### Product Table

CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    price DECIMAL(10,2),
    stock_quantity INT
);
### Cart

CREATE TABLE carts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
### Cart item

CREATE TABLE cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id BIGINT,
    product_id BIGINT,
    quantity INT,
    FOREIGN KEY (cart_id) REFERENCES carts(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

### Order

CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    total_amount DECIMAL(10,2),
    status VARCHAR(20),
    created_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
### Order item

CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT,
    product_id BIGINT,
    quantity INT,
    price DECIMAL(10,2),
    FOREIGN KEY (order_id) REFERENCES orders(id)
);
### Coupon

CREATE TABLE coupons (
    code VARCHAR(50) PRIMARY KEY,
    discount_type VARCHAR(20),
    discount_value DECIMAL(10,2),
    expiry_date TIMESTAMP
);
