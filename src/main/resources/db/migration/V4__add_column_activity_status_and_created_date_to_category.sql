ALTER TABLE tb_categories
    ADD COLUMN activity_status VARCHAR(30),
    ADD COLUMN created_date DATETIME;

UPDATE tb_categories
SET activity_status = 'ACTIVE',
    created_date = NOW();

ALTER TABLE tb_categories
    MODIFY activity_status VARCHAR(30) NOT NULL,
    MODIFY created_date DATETIME NOT NULL;