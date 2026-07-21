CREATE TABLE IF NOT EXISTS fish_images (
    id INT NOT NULL AUTO_INCREMENT,
    file_name VARCHAR(255) NOT NULL,
    fish_id INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (fish_id) REFERENCES fish(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO fish_images (file_name, fish_id)
SELECT image_file_name, id
FROM fish
WHERE image_file_name IS NOT NULL AND image_file_name != '';

ALTER TABLE fish DROP COLUMN image_file_name;
