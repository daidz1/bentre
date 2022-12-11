# Them cac fields can thiet vao csdl de dong bo
ALTER TABLE `organization_` ADD `orgIdMongoDB` VARCHAR(255) NULL AFTER `comments`;
ALTER TABLE `user_` ADD `userIdMongoDB` VARCHAR(255) NULL AFTER `status`;