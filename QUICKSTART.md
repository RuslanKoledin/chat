# Быстрый старт - Корпоративный мессенджер

## Запуск проекта за 5 минут

### Шаг 1: Запустить PostgreSQL

**Если у вас есть Docker:**
```bash
cd messenger-backend
docker-compose up -d
```

**Если Docker недоступен (сервер FUDO и т.п.):**

Установите PostgreSQL вручную (см. `INSTALL_POSTGRES_NO_DOCKER.md`), затем:
```bash
sudo -u postgres psql
CREATE DATABASE messenger_db;
CREATE USER messenger_user WITH PASSWORD 'messenger_password';
GRANT ALL PRIVILEGES ON DATABASE messenger_db TO messenger_user;
\c messenger_db
GRANT ALL ON SCHEMA public TO messenger_user;
\q
```

Подождите несколько секунд, пока PostgreSQL запустится.

### Шаг 2: Запустить Backend (в новом терминале)

```bash
cd messenger-backend
mvn spring-boot:run
```

Дождитесь сообщения "Started MessengerApplication"

### Шаг 3: Запустить Frontend (в новом терминале)

```bash
cd messenger-frontend
npm start
```

Браузер автоматически откроется на `http://localhost:3000`

### Шаг 4: Создать тестовых пользователей (в новом терминале)

```bash
# Пользователь 1: Иван Петров
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"ivan.petrov","password":"password123","fullName":"Иван Петров","email":"ivan.petrov@company.com","department":"IT"}'

# Пользователь 2: Мария Иванова
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"maria.ivanova","password":"password123","fullName":"Мария Иванова","email":"maria.ivanova@company.com","department":"HR"}'

# Пользователь 3: Александр Сидоров
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alex.sidorov","password":"password123","fullName":"Александр Сидоров","email":"alex.sidorov@company.com","department":"Sales"}'
```

### Шаг 5: Начать использование

1. Откройте `http://localhost:3000` в браузере
2. Войдите: `ivan.petrov` / `password123`
3. Нажмите "+ Новый чат"
4. Выберите пользователей (например, Мария Иванова)
5. Выберите тип чата (Личный или Групповой)
6. Нажмите "Создать чат"
7. Начните общение!

### Тест в нескольких вкладках

Откройте приложение в разных вкладках браузера:
- Вкладка 1: войдите как `ivan.petrov`
- Вкладка 2: войдите как `maria.ivanova`
- Создайте чат между ними
- Отправьте сообщения - они будут появляться в реальном времени!

## Остановка приложения

```bash
# Остановить Backend: Ctrl+C в терминале backend
# Остановить Frontend: Ctrl+C в терминале frontend
# Остановить PostgreSQL:
cd messenger-backend
docker-compose down
```

## Проблемы?

### Backend не запускается
- Убедитесь, что PostgreSQL запущен: `docker ps`
- Проверьте, что порт 8080 свободен: `lsof -i :8080`

### Frontend не запускается
- Убедитесь, что установлены зависимости: `npm install`
- Проверьте, что порт 3000 свободен: `lsof -i :3000`

### База данных не подключается
- Убедитесь, что Docker запущен
- Перезапустите контейнер: `docker-compose restart`

## Что дальше?

- Прочитайте полную документацию в `README.md`
- Изучите API endpoints в `messenger-backend/README.md`
- Настройте Nginx для production (`nginx.conf`)
