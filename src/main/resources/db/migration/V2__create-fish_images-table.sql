CREATE TABLE `fish_images` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `fish_id` INT NOT NULL,
    `image_file_name` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_fish_id`
    FOREIGN KEY (`fish_id`) REFERENCES `fish`(`id`)
    ON DELETE CASCADE
    );

INSERT INTO `fish_images` (`fish_id`, `image_file_name`)
SELECT `id`, `image_file_name`
FROM `fish`
WHERE `image_file_name` IS NOT NULL;

ALTER TABLE `fish`
DROP COLUMN `image_file_name`;
