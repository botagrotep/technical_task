-- 1. Create new table fish_images
CREATE TABLE IF NOT EXISTS fish_images (
  id INT NOT NULL AUTO_INCREMENT,
  fish_id INT NOT NULL,
  image_file_name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (fish_id) REFERENCES fish(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. Copy fish.image_file_name into fish_images
INSERT INTO fish_images (fish_id, image_file_name)
SELECT id, image_file_name
FROM fish
WHERE image_file_name IS NOT NULL
  AND id NOT IN (
    SELECT fish_id FROM fish_images
  );

-- 3. Drop image_file_name column from fish table
-- ( In case your MySQL version is higher 8.0.29, you can use
-- "ALTER TABLE fish DROP COLUMN IF EXISTS image_file_name;"
-- instead of code below

SET @has_column := (
  SELECT COUNT(*) FROM information_schema.columns
  WHERE table_name = 'fish'
    AND column_name = 'image_file_name'
    AND table_schema = DATABASE()
);

SET @sql := IF(@has_column > 0, 'ALTER TABLE fish DROP COLUMN image_file_name', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;