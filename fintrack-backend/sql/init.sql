-- Create database
CREATE DATABASE IF NOT EXISTS finanzas_db;
USE finanzas_db;

-- Users table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    current_balance DECIMAL(12, 2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Categories table
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_category_per_user (name, user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Goals table
CREATE TABLE goals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    target DECIMAL(12, 2) NOT NULL,
    saved_amount DECIMAL(12, 2) DEFAULT 0,
    description VARCHAR(255),
    status ENUM('IN_PROGRESS', 'ACHIEVED', 'ARCHIVED') DEFAULT 'IN_PROGRESS',
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Transactions table
CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    description VARCHAR(255),
    type ENUM('INCOME', 'EXPENSE') NOT NULL,
    category_id INT NOT NULL,
    goal_id INT,
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    FOREIGN KEY (goal_id) REFERENCES goals(id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 📊 Monthly summary table
CREATE TABLE monthly_summary (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    month_year CHAR(7) NOT NULL,
    total_income DECIMAL(12, 2) DEFAULT 0,
    total_expense DECIMAL(12, 2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_summary_per_month (user_id, month_year),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create indexes for optimized searches
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_transaction_user ON transactions(user_id);
CREATE INDEX idx_transaction_date ON transactions(date);
CREATE INDEX idx_goal_user ON goals(user_id);
CREATE INDEX idx_category_user ON categories(user_id);
CREATE INDEX idx_user_month ON monthly_summary(user_id, month_year);

-- 🧪 Insert sample data into users
INSERT INTO users (name, email, password, current_balance)
VALUES
('Juan Pérez', 'juan.perez@example.com', 'hashed_password_123', 0.00),
('María López', 'maria.lopez@example.com', 'hashed_password_456', 0.00),
('Carlos Ruiz', 'carlos.ruiz@example.com', 'hashed_password_789', 0.00);

-- 🗂️ Insert sample data into categories (disponibles para todos los usuarios)
INSERT INTO categories (name, user_id)
VALUES
('Salario', 1),
('Freelance', 1),
('Transporte', 1),
('Alimentación', 1),
('Entretenimiento', 1),
('Servicios Básicos', 1),
('Educación', 1),
('Salud', 1),

('Salario', 2),
('Freelance', 2),
('Transporte', 2),
('Alimentación', 2),
('Entretenimiento', 2),
('Servicios Básicos', 2),
('Educación', 2),
('Salud', 2),

('Salario', 3),
('Freelance', 3),
('Transporte', 3),
('Alimentación', 3),
('Entretenimiento', 3),
('Servicios Básicos', 3),
('Educación', 3),
('Salud', 3);

-- 🎯 Insert sample goals
INSERT INTO goals (name, target, saved_amount, description, status, user_id)
VALUES
('Fondo de emergencia', 2000.00, 500.00, 'Ahorro para imprevistos', 'IN_PROGRESS', 1),
('Viaje familiar', 3500.00, 0.00, 'Vacaciones de fin de año', 'IN_PROGRESS', 1),
('Curso de especialización', 1500.00, 600.00, 'Certificación profesional', 'IN_PROGRESS', 2);
