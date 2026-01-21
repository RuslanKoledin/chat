# Шпаргалка: Развертывание на FUDO сервере

Быстрая памятка для развертывания мессенджера на сервере без Docker.

## 🚀 Быстрая установка (5 команд)

```bash
# 1. Установить PostgreSQL
sudo yum install -y postgresql15-server postgresql15
sudo /usr/pgsql-15/bin/postgresql-15-setup initdb
sudo systemctl start postgresql-15 && sudo systemctl enable postgresql-15

# 2. Создать БД
sudo -u postgres psql -c "CREATE DATABASE messenger_db;"
sudo -u postgres psql -c "CREATE USER messenger_user WITH PASSWORD 'НАДЕЖНЫЙ_ПАРОЛЬ';"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE messenger_db TO messenger_user;"

# 3. Собрать Backend
cd messenger-backend
mvn clean package -DskipTests

# 4. Собрать Frontend
cd ../messenger-frontend
npm install && npm run build

# 5. Запустить
java -jar messenger-backend/target/messenger-1.0.0.jar
```

---

## 📂 Структура на сервере

```
/opt/messenger/
├── messenger-backend/
│   ├── target/messenger-1.0.0.jar    ← Backend JAR
│   └── src/main/resources/
│       └── application.properties    ← Настройки БД
└── messenger-frontend/
    └── build/                        ← Frontend статика для Nginx
```

---

## ⚙️ Основные конфигурации

### PostgreSQL: `/var/lib/pgsql/15/data/pg_hba.conf`
```
host    messenger_db    messenger_user    127.0.0.1/32    md5
```

### Backend: `application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/messenger_db
spring.datasource.username=messenger_user
spring.datasource.password=ВАШ_ПАРОЛЬ
jwt.secret=ДЛИННЫЙ_СЛУЧАЙНЫЙ_КЛЮЧ_256_БИТ
```

### Nginx: `/etc/nginx/conf.d/messenger.conf`
```nginx
server {
    listen 80;

    location / {
        root /opt/messenger/messenger-frontend/build;
        try_files $uri /index.html;
    }

    location /api/ {
        proxy_pass http://localhost:8080/api/;
    }

    location /ws/ {
        proxy_pass http://localhost:8080/ws/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

---

## 🔧 Systemd сервис: `/etc/systemd/system/messenger-backend.service`

```ini
[Unit]
Description=Messenger Backend
After=postgresql-15.service

[Service]
User=messenger
WorkingDirectory=/opt/messenger/messenger-backend
ExecStart=/usr/bin/java -jar target/messenger-1.0.0.jar
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

Команды:
```bash
sudo systemctl start messenger-backend
sudo systemctl enable messenger-backend
sudo systemctl status messenger-backend
```

---

## 📊 Проверка работы

```bash
# PostgreSQL
sudo systemctl status postgresql-15
psql -h localhost -U messenger_user -d messenger_db

# Backend
curl http://localhost:8080/api/users
sudo journalctl -u messenger-backend -f

# Nginx
sudo systemctl status nginx
curl http://localhost
```

---

## 🔥 Firewall

```bash
sudo firewall-cmd --permanent --add-port=80/tcp
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload
```

---

## 💾 Backup

```bash
# Создать backup
pg_dump -U messenger_user messenger_db > backup.sql

# Восстановить
psql -U messenger_user messenger_db < backup.sql
```

---

## 🐛 Troubleshooting

| Проблема | Решение |
|----------|---------|
| Backend не стартует | `sudo journalctl -u messenger-backend -f` |
| БД недоступна | `sudo systemctl restart postgresql-15` |
| Nginx ошибка | `sudo nginx -t && sudo systemctl restart nginx` |
| Порт занят | `sudo lsof -i :8080` или `sudo lsof -i :80` |

---

## 👤 Создать первого пользователя

```bash
curl -X POST http://localhost:8080/api/auth/register \
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

## 📚 Полная документация

- **Установка PostgreSQL без Docker:** `INSTALL_POSTGRES_NO_DOCKER.md`
- **Полная инструкция по развертыванию:** `DEPLOY_FUDO.md`
- **Общая документация:** `README.md`
- **Быстрый старт (с Docker):** `QUICKSTART.md`

---

## ✅ Security Checklist

- [ ] Сменить пароль БД
- [ ] Сменить JWT secret
- [ ] Настроить HTTPS
- [ ] Ограничить доступ firewall
- [ ] Настроить backup
