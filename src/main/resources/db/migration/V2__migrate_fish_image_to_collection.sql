CREATE TABLE fish_image_file_names
(
    fish_id          INT NOT NULL,
    image_file_names VARCHAR(255),
    FOREIGN KEY (fish_id) REFERENCES fish (id) ON DELETE CASCADE
);

INSERT INTO fish_image_file_names (fish_id, image_file_names)
SELECT id, image_file_name
FROM fish
WHERE image_file_name IS NOT NULL;

ALTER TABLE fish
DROP
COLUMN image_file_name;