-- Create tables
CREATE TABLE IF NOT EXISTS customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    country VARCHAR(100),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER REFERENCES customers(id),
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'PENDING'
);

-- Insert sample data
INSERT INTO customers (name, email, country) VALUES
('John Doe', 'john@example.com', 'USA'),
('Jane Smith', 'jane@example.com', 'UK'),
('Bob Johnson', 'bob@example.com', 'Canada');

INSERT INTO orders (customer_id, product_name, quantity, price, status) VALUES
(1, 'Laptop', 1, 999.99, 'COMPLETED'),
(1, 'Mouse', 2, 29.99, 'COMPLETED'),
(2, 'Keyboard', 1, 89.99, 'PENDING'),
(3, 'Monitor', 1, 299.99, 'COMPLETED'),
(3, 'Webcam', 1, 79.99, 'CANCELLED');