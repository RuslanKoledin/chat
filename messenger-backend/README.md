# Corporate Messenger - Backend

Корпоративный мессенджер для внутренней коммуникации сотрудников (MVL).

## Технологии

- Java 17
- Spring Boot 3.2.1
- PostgreSQL 15
- Spring Security + JWT
- WebSocket (STOMP)
- Maven

## Быстрый старт

### 1. Запустить PostgreSQL

```bash
docker-compose up -d
```

### 2. Собрать проект

```bash
mvn clean install
```

### 3. Запустить приложение

```bash
mvn spring-boot:run
```

Приложение будет доступно на `http://localhost:8080`

## API Endpoints

### Авторизация

- `POST /api/auth/register` - Регистрация нового пользователя
- `POST /api/auth/login` - Вход в систему

### Пользователи

- `GET /api/users` - Получить список всех пользователей
- `GET /api/users/{id}` - Получить пользователя по ID

### Чаты

- `POST /api/chats` - Создать новый чат
- `GET /api/chats` - Получить все чаты текущего пользователя
- `GET /api/chats/{id}` - Получить чат по ID

### Сообщения

- `POST /api/messages` - Отправить сообщение
- `GET /api/messages/chat/{chatId}` - Получить все сообщения чата

### WebSocket

- Endpoint: `/ws`
- Subscribe to: `/topic/chat/{chatId}` - для получения новых сообщений в чате
- Send to: `/app/chat.sendMessage` - для отправки сообщений через WebSocket

## Структура базы данных

### users
- id (PK)
- username (unique)
- password
- full_name
- email
- department
- active
- role (EMPLOYEE, ADMIN)
- created_at
- updated_at

### chats
- id (PK)
- name
- type (DIRECT, GROUP)
- created_at

### chat_members (многие-ко-многим)
- chat_id (FK)
- user_id (FK)

### messages
- id (PK)
- content
- chat_id (FK)
- sender_id (FK)
- reply_to_id (FK, nullable)
- created_at

## Пример регистрации пользователя

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ivan.petrov",
    "password": "password123",
    "fullName": "Иван Петров",
    "email": "ivan.petrov@company.com",
    "department": "IT"
  }'
```

## Пример входа

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ivan.petrov",
    "password": "password123"
  }'
```

## Конфигурация

Основные настройки находятся в `src/main/resources/application.properties`:

- Порт сервера: 8080
- База данных: PostgreSQL (localhost:5432)
- JWT secret: настраивается в production
- JWT expiration: 24 часа

## Security

- Все endpoints (кроме `/api/auth/**` и `/ws/**`) требуют JWT токен
- Токен передается в заголовке: `Authorization: Bearer <token>`
- Пароли хранятся в хешированном виде (BCrypt)
