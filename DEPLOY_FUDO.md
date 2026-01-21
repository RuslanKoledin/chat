# Развертывание на сервере FUDO (без Docker)

Инструкции для развертывания корпоративного мессенджера на сервере FUDO без использования Docker.

## Предварительные требования на сервере FUDO

- Java 17+ (или Java 11+)
- PostgreSQL 13+ (устанавливается вручную)
- Maven (для сборки backend)
- Node.js 16+ (для сборки frontend)
- Nginx (опционально, для production)

---

## Этап 1: Установка и настройка PostgreSQL

### 1.1 Установка PostgreSQL на CentOS/RHEL

```bash
# Установить PostgreSQL 15
sudo yum install -y postgresql15-server postgresql15

# Инициализация БД
sudo /usr/pgsql-15/bin/postgresql-15-setup initdb

# Запуск
sudo systemctl start postgresql-15
sudo systemctl enable postgresql-15

# Проверка статуса
sudo systemctl status postgresql-15
```

### 1.2 Создание базы данных

```bash
# Войти в PostgreSQL
sudo -u postgres psql

# Выполнить команды:
CREATE DATABASE messenger_db;
CREATE USER messenger_user WITH PASSWORD 'УКАЖИТЕ_НАДЕЖНЫЙ_ПАРОЛЬ';
GRANT ALL PRIVILEGES ON DATABASE messenger_db TO messenger_user;

\c messenger_db
GRANT ALL ON SCHEMA public TO messenger_user;

\q
```

### 1.3 Настройка доступа

Отредактируйте конфигурацию PostgreSQL:

```bash
sudo nano /var/lib/pgsql/15/data/pg_hba.conf
```

Добавьте строку:
```
host    messenger_db    messenger_user    127.0.0.1/32    md5
```

Перезапустите PostgreSQL:
```bash
sudo systemctl restart postgresql-15
```

### 1.4 Проверка подключения

```bash
psql -h localhost -U messenger_user -d messenger_db
# Введите пароль
```

---

## Этап 2: Развертывание Backend

### 2.1 Загрузить проект на сервер

```bash
# Создать директорию для проекта
mkdir -p /opt/messenger
cd /opt/messenger

# Загрузить проект (используйте свой метод: scp, git, и т.д.)
# Например через scp:
# scp -r messenger-backend user@fudo-server:/opt/messenger/
```

### 2.2 Настроить конфигурацию

Отредактируйте `application.properties`:

```bash
cd /opt/messenger/messenger-backend/src/main/resources
nano application.properties
```

Убедитесь, что пароль БД соответствует тому, что вы установили:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/messenger_db
spring.datasource.username=messenger_user
spring.datasource.password=ВАШ_ПАРОЛЬ_ИЗ_ШАГА_1.2

# Для production измените JWT secret на криптостойкий:
jwt.secret=СГЕНЕРИРУЙТЕ_ДЛИННЫЙ_СЛУЧАЙНЫЙ_КЛЮЧ_256_БИТ
```

### 2.3 Собрать проект

```bash
cd /opt/messenger/messenger-backend
mvn clean package -DskipTests
```

JAR файл будет создан в `target/messenger-1.0.0.jar`

### 2.4 Создать systemd сервис для автозапуска

```bash
sudo nano /etc/systemd/system/messenger-backend.service
```

Содержимое:
```ini
[Unit]
Description=Corporate Messenger Backend
After=postgresql-15.service

[Service]
Type=simple
User=messenger
WorkingDirectory=/opt/messenger/messenger-backend
ExecStart=/usr/bin/java -jar /opt/messenger/messenger-backend/target/messenger-1.0.0.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Создать пользователя и установить права:
```bash
sudo useradd -r -s /bin/false messenger
sudo chown -R messenger:messenger /opt/messenger
```

Запустить сервис:
```bash
sudo systemctl daemon-reload
sudo systemctl start messenger-backend
sudo systemctl enable messenger-backend

# Проверить статус
sudo systemctl status messenger-backend

# Проверить логи
sudo journalctl -u messenger-backend -f
```

---

## Этап 3: Развертывание Frontend

### 3.1 Настроить переменные окружения

```bash
cd /opt/messenger/messenger-frontend

# Создать .env файл
cat > .env << EOF
REACT_APP_API_URL=http://IP_СЕРВЕРА_FUDO:8080/api
REACT_APP_WS_URL=http://IP_СЕРВЕРА_FUDO:8080/ws
EOF
```

### 3.2 Собрать production версию

```bash
cd /opt/messenger/messenger-frontend
npm install
npm run build
```

Собранные файлы будут в `build/`

### 3.3 Настроить Nginx для раздачи frontend

Установить Nginx (если еще не установлен):
```bash
sudo yum install -y nginx
```

Создать конфигурацию:
```bash
sudo nano /etc/nginx/conf.d/messenger.conf
```

Содержимое:
```nginx
server {
    listen 80;
    server_name IP_ВАШЕГО_СЕРВЕРА_ИЛИ_ДОМЕН;

    # Frontend статика
    location / {
        root /opt/messenger/messenger-frontend/build;
        try_files $uri $uri/ /index.html;
        index index.html;
    }

    # Backend API
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # WebSocket
    location /ws/ {
        proxy_pass http://localhost:8080/ws/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_read_timeout 86400;
    }
}
```

Запустить Nginx:
```bash
sudo systemctl start nginx
sudo systemctl enable nginx

# Проверить конфигурацию
sudo nginx -t

# Если есть ошибки, исправить и перезапустить
sudo systemctl restart nginx
```

---

## Этап 4: Настройка Firewall

Откройте необходимые порты:

```bash
# Для Nginx (HTTP)
sudo firewall-cmd --permanent --add-port=80/tcp

# Для Backend напрямую (если нужно)
sudo firewall-cmd --permanent --add-port=8080/tcp

# Применить изменения
sudo firewall-cmd --reload
```

---

## Этап 5: Создание тестовых пользователей

После запуска всех сервисов:

```bash
# Заменить IP_СЕРВЕРА на реальный IP сервера FUDO
curl -X POST http://IP_СЕРВЕРА:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "fullName": "Администратор",
    "email": "admin@company.com",
    "department": "IT"
  }'
```

---

## Проверка работы

1. Откройте браузер: `http://IP_СЕРВЕРА_FUDO`
2. Войдите с созданным пользователем
3. Создайте чат и отправьте сообщение

---

## Управление сервисами

```bash
# Backend
sudo systemctl status messenger-backend
sudo systemctl restart messenger-backend
sudo journalctl -u messenger-backend -f  # логи

# PostgreSQL
sudo systemctl status postgresql-15
sudo systemctl restart postgresql-15

# Nginx
sudo systemctl status nginx
sudo systemctl restart nginx
```

---

## Резервное копирование

### Backup базы данных

```bash
# Создать backup
pg_dump -h localhost -U messenger_user messenger_db > /backup/messenger_$(date +%Y%m%d).sql

# Восстановить
psql -h localhost -U messenger_user messenger_db < /backup/messenger_20260120.sql
```

### Автоматический backup (cron)

```bash
sudo crontab -e
```

Добавить:
```cron
# Backup каждый день в 2:00 ночи
0 2 * * * pg_dump -h localhost -U messenger_user messenger_db > /backup/messenger_$(date +\%Y\%m\%d).sql
```

---

## Troubleshooting

### Backend не запускается

```bash
# Проверить логи
sudo journalctl -u messenger-backend -n 100

# Проверить, что PostgreSQL доступен
psql -h localhost -U messenger_user -d messenger_db

# Проверить, что порт 8080 свободен
sudo lsof -i :8080
```

### Frontend не загружается

```bash
# Проверить Nginx
sudo nginx -t
sudo systemctl status nginx

# Проверить логи Nginx
sudo tail -f /var/log/nginx/error.log
```

### База данных недоступна

```bash
# Проверить статус PostgreSQL
sudo systemctl status postgresql-15

# Проверить логи PostgreSQL
sudo tail -f /var/lib/pgsql/15/data/log/postgresql-*.log
```

---

## Security Checklist для Production

- [ ] Изменен пароль БД на криптостойкий
- [ ] Изменен JWT secret в application.properties
- [ ] Настроен HTTPS (SSL сертификат для Nginx)
- [ ] Настроен firewall (только необходимые порты)
- [ ] Настроен автоматический backup БД
- [ ] Ограничен доступ к серверу (только корпоративная сеть)
- [ ] Настроен мониторинг сервисов
- [ ] Настроена ротация логов
