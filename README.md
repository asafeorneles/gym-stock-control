# 📦📈 GymStock – Stock & Sales Control

API desenvolvida em Java com Spring Boot para controle de estoque, vendas e cupons, focada em boas práticas de desenvolvimento, arquitetura limpa e testes unitários.
Projeto pessoal/portfólio com objetivo de demonstrar domínio em backend Java moderno.

## 🚀 Tecnologias Utilizadas
- Java 17
- Spring Boot 3
- Spring Data JPA
- Hibernate
- Flyway
- MySQL
- Maven
- Swagger / OpenAPI
- JUnit 5
- Mockito
- Lombok
- Bean Validation
- Docker

## 🧾 Funcionalidades
- Gerenciamento completo de produtos e categorias
- Controle de estoque com atualização automática ao realizar vedndas
- Registro e gerenciamento de vendas
- Aplicação de cupons de desconto
- Ativação e desativação lógica de produtos, categorias e cupons
- Registro de maiores e menores vendas
- Busca dinâmica por parâmetros utilizando Specifications
- Paginação de resultados
- Tratamento global de exceções

## 🧱 Arquitetura e Boas Práticas
- Arquitetura em camadas:
  - Controller
  - Serice
  - Repository
- Uso de DTOs para isolamento da camada de domínio
- Exceções customizadas para regras de negócio
- Validações com Bean Validation
- API baseada em princípios REST
- Código orientado à legibilidade e manutenção

## 🗂️ Estrutura

```
src/
 └── main/
     ├── java/com.asafeorneles.gym_stock_control
     │    ├── controllers/
     │    ├── dto/
     |    ├── entities/
     │    ├── enums/
     │    ├── exceptions/
     │    ├── mapper/    
     │    ├── queryFilters/
     │    ├── repositories/
     │    ├── services/
     │    └── specifications/
     └── resources/
         ├── db.migration/
         ├── application.properties
     └── test
          └ ── ...
```

## 🧪 Testes
- No momento, 88 testes unitários focados na camada de Service
- Testes escritos com JUnit 5 e Mockito
- Cobertura das principais regras de negócio

## 📖 Documentação da API
A API conta com documentação interativa via Swagger:

```
http://localhost:8080/swagger-ui/index.html#/
```

## ⚙️ Como Executar o Projeto
Pré-requisitos:
- Java 17
- Docker e Docker COmpose
- Mavem

### 🐳 Subindo o banco de dados com Docker:

```
services:
  mysql:
    image: mysql:8.0.36
    container_name: mysql_gym_stock_control
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: gym_stock_control_api
    ports:
      - "3306:3306"
    volumes:
      - gym_stock_control_data:/var/lib/mysql

volumes:
  gym_stock_control_data:
```

### ▶️ Executando a aplicação:
```
mvn spring-boot:run
```
Com a aplicação rodando, acesse a interface interativa do Swagger para testar os endpoints:
  
```
http://localhost:8080/swagger-ui/index.html#/
```

- As migrações de banco são executadas automaticamente via Flyway.

## 👤 Autor
- Asafe Orneles
-  🔗 <a href="https://www.linkedin.com/in/asafeorneles">Linkedin</a>
