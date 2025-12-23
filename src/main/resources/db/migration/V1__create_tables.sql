CREATE TABLE tb_categories
(
    category_id  BINARY(16) PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    description  VARCHAR(255) NOT NULL,
    updated_date DATETIME
);

CREATE TABLE tb_products
(
    product_id   BINARY(16) PRIMARY KEY,
    name         VARCHAR(100)   NOT NULL,
    brand        VARCHAR(100)   NOT NULL,
    sale_price   DECIMAL(38, 2) NOT NULL,
    cost_price   DECIMAL(38, 2) NOT NULL,
    category_id  BINARY(16),
    created_date DATETIME       NOT NULL,
    updated_date DATETIME,
    CONSTRAINT uk_product_name_brand UNIQUE (name, brand),
    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id) REFERENCES tb_categories (category_id)
);

CREATE TABLE tb_product_inventories
(
    product_id          BINARY(16) PRIMARY KEY,
    quantity            INT         NOT NULL,
    low_stock_threshold INT         NOT NULL,
    inventory_status    VARCHAR(30) NOT NULL,
    updated_date        DATETIME,
    CONSTRAINT fk_inventory_product
        FOREIGN KEY (product_id) REFERENCES tb_products (product_id)
);

CREATE TABLE tb_sales
(
    sale_id        BINARY(16) PRIMARY KEY,
    total_price    DECIMAL(38, 2) NOT NULL,
    payment_method VARCHAR(30)    NOT NULL,
    created_date   DATETIME       NOT NULL,
    updated_date   DATETIME
);

CREATE TABLE tb_sale_itens
(
    sale_item_id BINARY(16) PRIMARY KEY,
    sale_id      BINARY(16),
    product_id   BINARY(16),
    quantity     INT            NOT NULL,
    cost_price   DECIMAL(38, 2) NOT NULL,
    unity_price  DECIMAL(38, 2) NOT NULL,
    total_price  DECIMAL(38, 2) NOT NULL,
    CONSTRAINT fk_sale_item_sale
        FOREIGN KEY (sale_id) REFERENCES tb_sales (sale_id),
    CONSTRAINT fk_sale_item_product
        FOREIGN KEY (product_id) REFERENCES tb_products (product_id)
);