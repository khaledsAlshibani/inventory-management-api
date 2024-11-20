-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 19, 2024 at 09:33 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `inventory_management`
--

-- --------------------------------------------------------

--
-- Table structure for table `inventory`
--

CREATE TABLE `inventory` (
  `id` bigint(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `status` enum('ACTIVE','INACTIVE') NOT NULL,
  `inventory_type` enum('WAREHOUSE','STORE','ONLINE') NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `area` decimal(10,2) NOT NULL,
  `available_area` decimal(10,2) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `inventory`
--

INSERT INTO `inventory` (`id`, `name`, `description`, `user_id`, `status`, `inventory_type`, `address`, `area`, `available_area`, `created_at`, `updated_at`) VALUES
(17, 'Warehouse A', 'Some text', 4, 'ACTIVE', 'WAREHOUSE', 'Jamal Abdulnasir', 490.00, 461.00, '2024-11-18 03:06:36', '2024-11-19 04:37:13'),
(20, 'Store A', 'I am Store ', 4, 'ACTIVE', 'WAREHOUSE', 'Gamal Street, Taizz', 500000.00, 500000.00, '2024-11-18 19:47:31', '2024-11-18 19:47:31'),
(21, 'Store BBA', 'My Store', 4, 'INACTIVE', 'STORE', 'Gamal Street, Taizz', 50000.00, 50000.00, '2024-11-18 19:51:51', '2024-11-18 20:15:46'),
(22, 'Warehouse CCSS', '', 4, 'INACTIVE', 'STORE', 'AAAA', 520.00, 520.00, '2024-11-18 20:08:25', '2024-11-18 20:08:25'),
(23, 'My Main Inventory', 'jjkjk', 4, 'ACTIVE', 'WAREHOUSE', 'Jamal Street', 5000.00, 5000.00, '2024-11-19 05:44:13', '2024-11-19 05:44:13');

--
-- Triggers `inventory`
--
DELIMITER $$
CREATE TRIGGER `set_available_area_default` BEFORE INSERT ON `inventory` FOR EACH ROW BEGIN
    IF NEW.available_area IS NULL THEN
        SET NEW.available_area = NEW.area;
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
  `id` bigint(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `sku` varchar(50) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 0,
  `initial_quantity` int(11) NOT NULL DEFAULT 0,
  `area` decimal(10,2) DEFAULT NULL,
  `status` enum('AVAILABLE','UNAVAILABLE') NOT NULL,
  `inventory_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `expiration_date` date DEFAULT NULL,
  `production_date` date DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`id`, `name`, `description`, `sku`, `price`, `quantity`, `initial_quantity`, `area`, `status`, `inventory_id`, `user_id`, `expiration_date`, `production_date`, `created_at`, `updated_at`) VALUES
(6, 'Updated Product Name', 'This is the updated description of the product.', 'UPDATED-SKU-12345', 150.75, 30, 50, 12.50, 'UNAVAILABLE', NULL, 4, '2024-12-31', '2024-01-01', '2024-11-18 21:45:32', '2024-11-19 05:47:46'),
(7, 'Sample Product 4', 'This is a sample product description.', 'PSROAsD-12345', 100.50, 50, 50, 10.50, 'AVAILABLE', NULL, 4, '2024-12-31', '2024-01-01', '2024-11-18 21:46:05', '2024-11-18 21:46:05'),
(8, 'Sample Product 5', 'This is a sample product description.', 'PSROAsD-123456', 100.50, 50, 50, 10.50, 'AVAILABLE', NULL, 4, '2024-12-31', '2024-01-01', '2024-11-18 21:48:41', '2024-11-18 21:48:41'),
(10, 'Sample Product 7', 'This is a sample product description.', 'PSROAsD-12s3456', 100.50, 50, 50, 10.50, 'AVAILABLE', NULL, 4, '2024-12-31', '2024-01-01', '2024-11-18 21:49:29', '2024-11-18 21:49:29'),
(11, 'Sample Product 8', 'This is a sample product description.', 'PSROAsD-12s34566', 100.50, 50, 50, 10.50, 'AVAILABLE', 17, 4, '2024-12-31', '2024-01-01', '2024-11-18 21:54:11', '2024-11-18 21:54:11'),
(12, 'Product Example', 'some description', 'PRODUCT-100', 22.00, 10, 5, 6.00, 'AVAILABLE', 17, 4, '2024-11-07', NULL, '2024-11-19 04:33:19', '2024-11-19 05:48:13'),
(15, 'Updated Product Name', 'This is the updated description of the product.', 'UPDATED2-SKU-12345', 150.75, 30, 50, 12.50, 'AVAILABLE', 17, 4, '2024-12-31', '2024-01-01', '2024-11-19 04:37:13', '2024-11-19 04:37:13');

--
-- Triggers `product`
--
DELIMITER $$
CREATE TRIGGER `after_product_delete` AFTER DELETE ON `product` FOR EACH ROW BEGIN
    IF OLD.area IS NOT NULL THEN
        UPDATE inventory
        SET available_area = IFNULL(area, 0) - (SELECT SUM(IFNULL(area, 0)) FROM product WHERE inventory_id = OLD.inventory_id)
        WHERE id = OLD.inventory_id;
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `after_product_insert` AFTER INSERT ON `product` FOR EACH ROW BEGIN
    IF NEW.area IS NOT NULL THEN
        UPDATE inventory
        SET available_area = IFNULL(area, 0) - (SELECT SUM(IFNULL(area, 0)) FROM product WHERE inventory_id = NEW.inventory_id)
        WHERE id = NEW.inventory_id;
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `after_product_update` AFTER UPDATE ON `product` FOR EACH ROW BEGIN
    IF NEW.area IS NOT NULL OR OLD.area IS NOT NULL THEN
        UPDATE inventory
        SET available_area = IFNULL(area, 0) - (SELECT SUM(IFNULL(area, 0)) FROM product WHERE inventory_id = NEW.inventory_id)
        WHERE id = NEW.inventory_id;
        
        -- If the product's inventory_id was changed, update the old inventory as well
        IF NEW.inventory_id <> OLD.inventory_id THEN
            UPDATE inventory
            SET available_area = IFNULL(area, 0) - (SELECT SUM(IFNULL(area, 0)) FROM product WHERE inventory_id = OLD.inventory_id)
            WHERE id = OLD.inventory_id;
        END IF;
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL,
  `username` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `photo_path` varchar(255) DEFAULT NULL,
  `birthdate` date DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `username`, `email`, `name`, `password`, `photo_path`, `birthdate`, `created_at`, `updated_at`) VALUES
(2, 'khaled', 'khaled@gmail.com', 'Khaled', '$2a$10$Z1ARuOylgywvNPvV//S0/.hxDybxAzH1EwdFsao0yW.NOYljnFCbO', 'http://localhost:8082/images/user-photos/default-user.webp', NULL, '2024-11-16 12:21:34', '2024-11-18 14:20:14'),
(3, 'alaa', 'alaa@gmail.com', 'Alaa', '$2a$10$urGeqnt5iB/vf/49Vm4Lg.a51x4az/7.E3w.kMXpc9W6lZq/EPN3K', 'http://localhost:8082/images/user-photos/default-user.webp', NULL, '2024-11-16 12:32:42', '2024-11-18 14:20:16'),
(4, 'khaleds', 'khaleds@gmail.com', 'Khaled Alshibani', '$2a$10$T2G4wkTQm/A5.n1nRGp68uZjuBnipfYDreU1.46wXnMOy3Y7/Ukha', 'http://localhost:8082/images/user-photos/1731995114865-dbf4b864-176d-4052-b772-1c4119d621a7.jpg', '2000-01-01', '2024-11-16 12:34:00', '2024-11-19 05:45:14'),
(7, 'Nebras', 'nebras@gmail.com', 'Nebras Ali Qaid', '$2a$10$2pEWQkKWEDYanS42Bv5jLeutgVCG6RDgD/tiRc9lzH.0Eh0aVNXxa', 'http://localhost:8082/images/user-photos/default-user.webp', NULL, '2024-11-17 05:47:52', '2024-11-18 14:20:17'),
(8, 'naji', 'naji@inveit.com', 'Naji a', '$2a$10$7gPy04D5ZZZ9yFBBL9UIxexXjSPgQyVfAv2hsP5LzWLObbaaRwDzC', 'http://localhost:8082/images/user-photos/default-user.webp', NULL, '2024-11-18 21:00:37', '2024-11-18 21:01:01'),
(10, 'hamza', 'hamza@gmail.com', 'hamza', '$2a$10$kOQ/HNG94blBUwwjbntirum1JG21Yq7BFnsQ1TaPSqK1SaExfI.bO', 'http://localhost:8082/images/user-photos/default-user.webp', NULL, '2024-11-19 04:29:59', '2024-11-19 04:29:59'),
(11, 'hamdi', 'hamdi@gmail.com', 'Hamdi Amin', '$2a$10$WKH6R70AYaOmJHBoaRdRquAPe3/DEdFCsEBV9gjM4kMlBt6o1fnmq', 'http://localhost:8082/images/user-photos/default-user.webp', NULL, '2024-11-19 06:56:47', '2024-11-19 06:56:47'),
(12, 'khaled1', 'khaled1@gmail.com', 'Khaled Alshibani', '$2a$10$C5kuXJUy5e4uWOcNAxbJrO4YfXj.52Fxo.ffBHFsr19hoDIEtRpXK', 'http://localhost:8082/images/user-photos/default-user.webp', NULL, '2024-11-19 08:12:25', '2024-11-19 08:12:25');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `inventory`
--
ALTER TABLE `inventory`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_sku` (`sku`),
  ADD UNIQUE KEY `UKq1mafxn973ldq80m1irp3mpvq` (`sku`),
  ADD KEY `inventory_id` (`inventory_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `inventory`
--
ALTER TABLE `inventory`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `product`
--
ALTER TABLE `product`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `inventory`
--
ALTER TABLE `inventory`
  ADD CONSTRAINT `inventory_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `product`
--
ALTER TABLE `product`
  ADD CONSTRAINT `product_ibfk_1` FOREIGN KEY (`inventory_id`) REFERENCES `inventory` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `product_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
