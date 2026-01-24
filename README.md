# 📦📈 GymStock – Stock & Sales Control

API desenvolvida em Java com Spring Boot para controle de estoque, vendas e cupons, focada em boas práticas de desenvolvimento, arquitetura limpa e testes unitários.
Projeto pessoal/portfólio com objetivo de demonstrar domínio em backend Java moderno.

## 🚀 Tecnologias Utilizadas
- Java 17
- Spring Boot 3
- Spring Data JPA
- Spring Security (JWT, Access Token e Refresh Token)
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
- Controle de estoque com atualização automática ao realizar vendas
- Registro e gerenciamento de vendas
- Aplicação de cupons de desconto
- Ativação e desativação lógica de produtos, categorias e cupons
- Registro de maiores e menores vendas
- Busca dinâmica por parâmetros utilizando Specifications
- Paginação de resultados
- Tratamento global de exceções
- Autenticação e autorização com Spring Security
- Implementação de fluxo de autenticação com Access Token e Refresh Token

## 🧱 Arquitetura e Boas Práticas
- Arquitetura em camadas:
  - Controller
  - Service
  - Repository
- Uso de DTOs para isolamento da camada de domínio
- Exceções customizadas para regras de negócio
- Validações de entrada com Bean Validation
- API baseada em princípios REST
- Código orientado à legibilidade e manutenção
- Configuração centralizada de segurança com Spring Security
- Controle de acesso baseado em roles e authorities

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

## 🔐 Segurança e Autenticação
A API utiliza Spring Security com autenticação baseada em JWT (JSON Web Token), garantindo controle de acesso seguro aos recursos.

### 🔑 Autenticação
- Autenticação baseada em JWT
- Utilização de Access Token e Refresh Token
- Tokens assinados com chave RSA (public/private key)
- Renovação automática do token de acesso via Refresh Token
- O token é enviado no header das requisições protegidas:

Authorization: Bearer <token>
  
```
Authorization: Bearer <token>
```

### 👥 Perfis de Usuário (Roles)
Atualmente, o sistema possui os seguintes perfis:

- ROLE_ADMIN
  - Acesso total ao sistema
  - Gerenciamento completo de produtos, categorias, cupons, usuários e vendas

- ROLE_BASIC
    - Acesso restrito às funcionalidades operacionais
    - Registro de vendas e consulta de dados permitidos

### 🔒 Controle de Acesso
- Todas as rotas, com exceção da autenticação, são protegidas por Spring Security
- O controle de acesso é realizado através de:
  - Configuração centralizada no `SecurityConfig`
  - Permissões granulares (authorities) definidas via Enum
  - Associação das permissões às roles do sistema
  - Uso de anotações como:

@PreAuthorize("hasAuthority('product:create')")

### 🛣️ Rotas e Permissões (Resumo)

#### 🔓 Rotas Públicas
```
Método      Endpoint          Descrição

POST        /auth/login       Autenticação do usuário
POST        /auth/refresh     Renovação do token de acesso
```

#### 🔒 Rotas Protegidas (JWT obrigatório)
```
Método      Endpoint         Permissão

POST        /products        product:create
GET         /products        roduct:read
```
*Obs: Lista resumida. A documentação completa está disponível via Swagger.*
  
## 🧪 Testes
- O projeto conta atualmente com 98 testes unitários
- Foco principal na camada de Service
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
- Docker e Docker Compose
- Maven

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
Com a aplicação rodando, acesse a interface interativa do Swagger para testar os endpoints seguindo esses passos:
  
```
1- acesse a interface através do link: http://localhost:8080/swagger-ui/index.html#/
2- Realize o login no endpoint `/auth/login`
3- Copie o Access Token retornado
4- Clique em **Authorize** no Swagger (canto superior direito)
5- Insira o seu token
6- Utilize o Refresh Token para renovação do token quando necessário

Após isso, os endpoints protegidos poderão ser acessados normalmente.
```

- As migrações de banco são executadas automaticamente via Flyway.

## 👤 Autor
- Asafe Orneles
-  🔗 <a href="https://www.linkedin.com/in/asafeorneles">Linkedin</a>
