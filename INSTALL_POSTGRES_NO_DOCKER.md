# Установка PostgreSQL без Docker

## Для Linux (CentOS/RHEL)

### 1. Установить PostgreSQL

```bash
# Установка PostgreSQL 15
sudo yum install -y postgresql15-server postgresql15

# Инициализация БД
sudo /usr/pgsql-15/bin/postgresql-15-setup initdb

# Запуск и автозапуск
sudo systemctl start postgresql-15
sudo systemctl enable postgresql-15
```

### 2. Настроить доступ

Отредактируйте файл `/var/lib/pgsql/15/data/pg_hba.conf`:

```bash
sudo nano /var/lib/pgsql/15/data/pg_hba.conf
```

Добавьте или измените строку для локального доступа:

```
# IPv4 local connections:
host    messenger_db    messenger_user    127.0.0.1/32    md5
host    all             all               127.0.0.1/32    md5
```

Перезапустите PostgreSQL:

```bash
sudo systemctl restart postgresql-15
```

### 3. Создать базу данных и пользователя

```bash
# Войти в PostgreSQL под пользователем postgres
sudo -u postgres psql

# В консоли PostgreSQL выполнить:
CREATE DATABASE messenger_db;
CREATE USER messenger_user WITH PASSWORD 'messenger_password';
GRANT ALL PRIVILEGES ON DATABASE messenger_db TO messenger_user;

# Для PostgreSQL 15 также нужно:
\c messenger_db
GRANT ALL ON SCHEMA public TO messenger_user;

# Выйти
\q
```

### 4. Проверить подключение

```bash
psql -h localhost -U messenger_user -d messenger_db
# Введите пароль: messenger_password
```

---

## Для Linux (Ubuntu/Debian)

### 1. Установить PostgreSQL

```bash
# Обновить пакеты
sudo apt update

# Установить PostgreSQL
sudo apt install -y postgresql postgresql-contrib

# Запуск и автозапуск
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

### 2. Создать базу данных и пользователя

```bash
# Войти в PostgreSQL
sudo -u postgres psql

# Выполнить команды:
CREATE DATABASE messenger_db;
CREATE USER messenger_user WITH PASSWORD 'messenger_password';
GRANT ALL PRIVILEGES ON DATABASE messenger_db TO messenger_user;

\c messenger_db
GRANT ALL ON SCHEMA public TO messenger_user;

\q
```

### 3. Настроить доступ

```bash
sudo nano /etc/postgresql/14/main/pg_hba.conf
```

Добавить:
```
host    messenger_db    messenger_user    127.0.0.1/32    md5
```

Перезапустить:
```bash
sudo systemctl restart postgresql
```

---

## Настройка Backend для работы с установленным PostgreSQL

Файл `messenger-backend/src/main/resources/application.properties` уже настроен правильно:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/messenger_db
spring.datasource.username=messenger_user
spring.datasource.password=messenger_password
```

Никаких изменений не требуется!

---

## Для Windows Server

### 1. Скачать PostgreSQL

Скачайте установщик с официального сайта:
https://www.postgresql.org/download/windows/

### 2. Установить PostgreSQL

- Запустите установщик
- Порт: 5432 (по умолчанию)
- Пароль для пользователя postgres: установите свой пароль
- Locale: Russian, Russia

### 3. Создать базу данных через pgAdmin

Откройте pgAdmin (устанавливается вместе с PostgreSQL):

1. Подключитесь к серверу PostgreSQL
2. Правой кнопкой на "Databases" → Create → Database
   - Name: `messenger_db`
3. Правой кнопкой на "Login/Group Roles" → Create → Login/Group Role
   - General → Name: `messenger_user`
   - Definition → Password: `messenger_password`
   - Privileges → Can login: Yes
4. Правой кнопкой на `messenger_db` → Properties → Security
   - Добавить `messenger_user` с правами ALL

### 4. Через SQL (альтернатива)

Откройте Query Tool в pgAdmin:

```sql
CREATE DATABASE messenger_db;
CREATE USER messenger_user WITH PASSWORD 'messenger_password';
GRANT ALL PRIVILEGES ON DATABASE messenger_db TO messenger_user;
```

---

## Безопасность для Production

⚠️ **ВАЖНО:** Для production-сервера измените пароль!

В файле `application.properties`:

```properties
spring.datasource.password=ВАШ_НАДЕЖНЫЙ_ПАРОЛЬ
```

И соответственно при создании пользователя в PostgreSQL:

```sql
CREATE USER messenger_user WITH PASSWORD 'ВАШ_НАДЕЖНЫЙ_ПАРОЛЬ';
```

---

## Проверка работы

После настройки PostgreSQL и запуска backend проверьте логи:

```bash
cd messenger-backend
mvn spring-boot:run
```

Если всё настроено правильно, вы увидите:

```
Hibernate: create table users (...)
Hibernate: create table chats (...)
Hibernate: create table messages (...)
Started MessengerApplication in X seconds
```

---

## Удаленное подключение (если backend на другом сервере)

Если PostgreSQL и backend на разных серверах, отредактируйте:

1. `/var/lib/pgsql/15/data/postgresql.conf`:
   ```
   listen_addresses = '*'  # или укажите IP backend-сервера
   ```

2. `/var/lib/pgsql/15/data/pg_hba.conf`:
   ```
   host    messenger_db    messenger_user    IP_BACKEND_СЕРВЕРА/32    md5
   ```

3. В `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://IP_БД_СЕРВЕРА:5432/messenger_db
   ```

4. Перезапустите PostgreSQL:
   ```bash
   sudo systemctl restart postgresql-15
   ```

---

## Резервное копирование

Создание backup:
```bash
pg_dump -h localhost -U messenger_user messenger_db > backup.sql
```

Восстановление:
```bash
psql -h localhost -U messenger_user messenger_db < backup.sql
```
