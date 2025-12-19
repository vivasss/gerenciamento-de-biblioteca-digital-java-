# Sistema de Biblioteca Digital

Sistema completo de gerenciamento de biblioteca digital em Java com JavaFX.

## Requisitos

- Java 17 ou superior
- Maven 3.6+
- MySQL 8.0+

## Configuração do Banco de Dados

1. Inicie o MySQL Server
2. Execute o script de criação do banco:
   ```bash
   mysql -u root -p < src/main/resources/database/schema.sql
   ```

3. Se necessário, ajuste as credenciais em:
   `src/main/java/com/biblioteca/utils/DatabaseConnection.java`

## Executando o Projeto

```bash
cd biblioteca-digital
mvn clean javafx:run
```

## Credenciais Padrão

- **E-mail:** admin@biblioteca.com
- **Senha:** admin123

## Funcionalidades

- ✅ Login e autenticação com BCrypt
- ✅ Gestão de usuários (CRUD)
- ✅ Gestão de livros (CRUD)
- ✅ Empréstimos e devoluções
- ✅ Relatórios em PDF
- ✅ Logs de atividades
- ✅ Notificações automáticas (thread)

## Estrutura do Projeto

```
src/main/java/com/biblioteca/
├── App.java              # Classe principal
├── controller/           # Controllers JavaFX
├── dao/                  # Data Access Objects
├── model/                # Modelos/Entidades
└── utils/                # Utilitários
```

## Tecnologias

- JavaFX 21
- MySQL + JDBC
- iTextPDF
- BCrypt
- Maven
