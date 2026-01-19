ALTER TABLE tb_users
    ADD COLUMN activity_status VARCHAR(30),
    ADD COLUMN registered_date DATETIME;

UPDATE tb_users
SET activity_status = 'ACTIVE',
    registered_date = NOW();

ALTER TABLE tb_users
    MODIFY activity_status VARCHAR(30) NOT NULL,
    MODIFY registered_date DATETIME NOT NULL;