-- Insert test users
-- Password: "password123" (BCrypt hashed)
INSERT INTO users (email, password, name, is_active, created_at, updated_at) VALUES
('admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Admin User', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('user@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Regular User', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('test@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Test User', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert user roles
INSERT INTO user_roles (user_id, role) VALUES
(1, 'ADMIN'),
(1, 'USER'),
(2, 'USER'),
(3, 'USER');