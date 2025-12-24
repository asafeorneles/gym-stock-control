ALTER TABLE tb_products
    ADD COLUMN activity_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN inactivity_reason VARCHAR(255);