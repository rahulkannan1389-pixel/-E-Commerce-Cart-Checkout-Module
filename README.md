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
