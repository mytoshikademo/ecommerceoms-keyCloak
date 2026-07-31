--liquibase formatted sql

-- changeset Abhishek:v1-create-users-roles
-- Already applied before Liquibase integration.

--changeset Abhishek:v2_create_product_inventory.sql
--already applied before Liquibase integration

--changeset Abhishek:v3_add_soft_delete_column
--already applied Liquibase integration

--changeset Abhishek:v4_password_removed_and_added_keyCloakUserId
ALTER TABLE users
DROP COLUMN password;
ALTER TABLE users
ADD COLUMN keycloak_user_id VARCHAR(255);
--rollback ALTER TABLE users DROP COLUMN keycloak_user_id;
--rollback ALTER TABLE users ADD COLUMN password VARCHAR(255) NOT NULL;

--changeset Abhishek:V5_user_role_table_removed
ALTER TABLE user_role
DROP FOREIGN KEY FKj345gk1bovqvfame88rcx7yyx,
DROP FOREIGN KEY FKt7e7djp752sqn6w22i6ocqy6q;
DROP TABLE user_role;
--rollback CREATE TABLE user_role (
--rollback     id BIGINT NOT NULL AUTO_INCREMENT,
--rollback     role_id BIGINT DEFAULT NULL,
--rollback     user_id BIGINT DEFAULT NULL,
--rollback     PRIMARY KEY (id),
--rollback     KEY FKt7e7djp752sqn6w22i6ocqy6q (role_id),
--rollback     KEY FKj345gk1bovqvfame88rcx7yyx (user_id),
--rollback     CONSTRAINT FKj345gk1bovqvfame88rcx7yyx FOREIGN KEY (user_id) REFERENCES users(id),
--rollback     CONSTRAINT FKt7e7djp752sqn6w22i6ocqy6q FOREIGN KEY (role_id) REFERENCES roles(id)
--rollback );


--changeset Abhishek:V6_role_table_removed
DROP TABLE roles;
--rollback CREATE TABLE roles (
--rollback     id BIGINT NOT NULL AUTO_INCREMENT,
--rollback     name ENUM('ADMIN','CUSTOMER','SUPPORT') NOT NULL,
--rollback     PRIMARY KEY (id),
--rollback     UNIQUE KEY UKofx66keruapi6vyqpv6f2or37 (name)
--rollback );

--rollback INSERT INTO roles (id, name) VALUES (1,'ADMIN');
--rollback INSERT INTO roles (id, name) VALUES (2,'CUSTOMER');
--rollback INSERT INTO roles (id, name) VALUES (3,'SUPPROT');
