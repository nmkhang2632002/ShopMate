🛒 Android Product Sale App – Development Context
📱 Overview
This is an Android application that allows users to:

Browse and search for products

Add them to a shopping cart

Proceed to checkout and payment

Interact via real-time chat and map integration

It uses a RESTful API backend (language-agnostic) and a MySQL or SQL Server database. Data is exchanged using JSON.

🔐 Features Breakdown (with Progress Weights)
Authentication (10%)

Sign Up / Login

Passwords hashed securely

Roles: customer, admin

Product Listing (15%)

Fetch via REST API

Supports sorting & filtering

Display product preview

Product Details (15%)

Full specs, description, multiple images

Add to cart with quantity selection

Shopping Cart (15%)

View, update, and remove items

Live cart total calculations

Billing & Checkout (10%)

Payment gateway (VNPay, ZaloPay, PayPal)

Billing & shipping form

Order confirmation screen

Cart Badge Notification (15%)

Show item count on app icon (even when closed)

Use NotificationCompat or similar

Map Integration (10%)

Google Maps showing store location

Route/direction feature using current location

Real-time Chat (10%)

Firebase or custom chat API

Real-time messaging between customer and store

📊 Entities & Relationships
🧑 User
UserID, Username, PasswordHash, Email, PhoneNumber, Address, Role

One-to-many: Cart, Notification, ChatMessage

📦 Product
ProductID, ProductName, BriefDescription, FullDescription, TechnicalSpecifications, Price, ImageURL, CategoryID

One-to-many: CartItem

🏷 Category
CategoryID, CategoryName

One-to-many: Product

🛒 Cart
CartID, UserID, TotalPrice, Status

One-to-many: CartItem

One-to-one: Order

📥 CartItem
CartItemID, CartID, ProductID, Quantity, Price

📦 Order
OrderID, CartID, UserID, PaymentMethod, BillingAddress, OrderStatus, OrderDate

One-to-one: Payment

💳 Payment
PaymentID, OrderID, Amount, PaymentDate, PaymentStatus

🔔 Notification
NotificationID, UserID, Message, IsRead, CreatedAt

💬 ChatMessage
ChatMessageID, UserID, Message, SentAt

📍 StoreLocation
LocationID, Latitude, Longitude, Address

🔄 Relationships (ER Summary)
User 1:N Cart, User 1:N Notification, User 1:N ChatMessage

Cart 1:N CartItem

Product 1:N CartItem

Cart 1:1 Order

Order 1:1 Payment

Category 1:N Product

StoreLocation 1:N Order
