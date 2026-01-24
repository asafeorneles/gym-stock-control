CREATE TABLE tb_refresh_tokens
(
    refresh_token_id BINARY(16) PRIMARY KEY,
    token            TEXT NOT NULL,
    revoked          BIT          NOT NULL,
    expires_date     DATETIME NOT NULL ,
    user_id          BINARY(16) NOT NULL,
    CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id) REFERENCES tb_users (user_id)
);