CREATE TABLE tb_coupon(
    coupon_id BINARY(16) PRIMARY KEY,
    code VARCHAR(30) NOT NULL,
    description VARCHAR(255) NOT NULL,
    discount_value DECIMAL(38,2) NOT NULL,
    discount_type VARCHAR(30) NOT NULL,
    unlimited BOOLEAN NOT NULL DEFAULT FALSE,
    quantity INT NOT NULL,
    activity_status VARCHAR(30) NOT NULL,
    expiration_date DATETIME,
    created_date DATETIME NOT NULL,
    updated_date DATETIME,
    CONSTRAINT uk_product_name_brand UNIQUE (code)
);

ALTER TABLE tb_sales
    ADD COLUMN discount_amount DECIMAL(38,2),
    ADD COLUMN coupon_id BINARY(16),
    ADD CONSTRAINT fk_sale_coupon
        FOREIGN KEY (coupon_id) REFERENCES tb_coupon (coupon_id)