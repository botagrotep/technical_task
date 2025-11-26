-- Create the new table for images
CREATE TABLE IF NOT EXISTS `fish_images` (
  `id` int NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) DEFAULT NULL,
  `fish_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_fish_images_fish` (`fish_id`),
  CONSTRAINT `FK_fish_images_fish` FOREIGN KEY (`fish_id`) REFERENCES `fish` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Migrate existing images from 'fish' table to 'fish_images' table
INSERT INTO `fish_images` (file_name, fish_id)
SELECT image_file_name, id
FROM `fish`
WHERE image_file_name IS NOT NULL;

-- Remove the old column from 'fish' table
ALTER TABLE `fish` DROP COLUMN `image_file_name`;
