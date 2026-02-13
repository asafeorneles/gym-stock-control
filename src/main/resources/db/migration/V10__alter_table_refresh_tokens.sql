ALTER TABLE tb_refresh_tokens DROP COLUMN token;

ALTER TABLE tb_refresh_tokens ADD COLUMN jti VARCHAR(255) NOT NULL;

ALTER TABLE tb_refresh_tokens ADD CONSTRAINT uk_refresh_token_jti UNIQUE (jti);