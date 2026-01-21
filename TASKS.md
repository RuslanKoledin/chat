# Быстрый список задач (Tasks Backlog)

## 🔴 Sprint 1: Критичные доработки

### Task 1.1: Поиск сотрудников
**Приоритет:** HIGH
**Пункт ТЗ:** п.4.1.1
**Статус:** ❌ TODO

**Описание:** Добавить поиск по ФИО и логину при создании чата

**Subtasks:**
- [ ] Backend: добавить endpoint `GET /api/users/search?query={term}`
- [ ] Backend: реализовать поиск по `fullName` и `username` (LIKE)
- [ ] Frontend: добавить поле поиска в `NewChatModal.tsx`
- [ ] Frontend: debounce для запросов поиска
- [ ] Тестирование: проверить поиск по частичному совпадению

**Файлы:**
- `messenger-backend/src/main/java/com/company/messenger/controller/UserController.java`
- `messenger-backend/src/main/java/com/company/messenger/repository/UserRepository.java`
- `messenger-frontend/src/components/NewChatModal.tsx`

---

### Task 1.2: Ограничение длины сообщений
**Приоритет:** HIGH
**Пункт ТЗ:** п.4.1.3
**Статус:** ❌ TODO

**Описание:** Ограничить сообщения до 2000 символов

**Subtasks:**
- [ ] Backend: добавить `@Size(max=2000)` в `SendMessageRequest.java`
- [ ] Backend: обновить `@Column(length=2000)` в `Message.java`
- [ ] Frontend: проверка длины перед отправкой
- [ ] Frontend: счетчик символов (опционально)
- [ ] Frontend: сообщение об ошибке при превышении

**Файлы:**
- `messenger-backend/src/main/java/com/company/messenger/dto/SendMessageRequest.java`
- `messenger-backend/src/main/java/com/company/messenger/entity/Message.java`
- `messenger-frontend/src/components/ChatWindow.tsx`

---

### Task 1.3: Улучшение обработки ошибок
**Приоритет:** HIGH
**Пункт ТЗ:** п.4.1.3
**Статус:** ⚠️ IN PROGRESS (базовая реализация есть)

**Описание:** Сообщение не должно пропадать при ошибке отправки

**Subtasks:**
- [ ] Frontend: не очищать поле ввода при ошибке
- [ ] Frontend: кнопка "Повторить отправку"
- [ ] Frontend: индикатор статуса (отправляется/ошибка)
- [ ] Frontend: toast-уведомления вместо alert

**Файлы:**
- `messenger-frontend/src/components/ChatWindow.tsx`

---

### Task 1.4: Индикатор состояния WebSocket
**Приоритет:** MEDIUM
**Пункт ТЗ:** п.4.1.4
**Статус:** ❌ TODO

**Описание:** Показывать статус подключения к WebSocket

**Subtasks:**
- [ ] Frontend: добавить state для статуса соединения
- [ ] Frontend: слушать события connect/disconnect
- [ ] Frontend: индикатор в header чата
- [ ] Frontend: подгрузка пропущенных сообщений после восстановления

**Файлы:**
- `messenger-frontend/src/services/websocket.ts`
- `messenger-frontend/src/components/ChatWindow.tsx`
- `messenger-frontend/src/components/ChatWindow.css`

---

## 🟡 Sprint 2: Важные улучшения

### Task 2.1: Пагинация сообщений
**Приоритет:** MEDIUM
**Пункт ТЗ:** п.4.1.5
**Статус:** ❌ TODO

**Описание:** Загружать последние 50 сообщений, догружать при прокрутке

**Subtasks:**
- [ ] Backend: добавить пагинацию в `MessageRepository`
- [ ] Backend: endpoint с параметрами `limit` и `beforeMessageId`
- [ ] Frontend: загрузка последних 50 при открытии чата
- [ ] Frontend: IntersectionObserver для догрузки при прокрутке вверх
- [ ] Frontend: индикатор загрузки

**Файлы:**
- `messenger-backend/src/main/java/com/company/messenger/repository/MessageRepository.java`
- `messenger-backend/src/main/java/com/company/messenger/service/MessageService.java`
- `messenger-backend/src/main/java/com/company/messenger/controller/MessageController.java`
- `messenger-frontend/src/components/ChatWindow.tsx`
- `messenger-frontend/src/api/messageApi.ts`

---

### Task 2.2: Администратор чата
**Приоритет:** MEDIUM
**Пункт ТЗ:** п.4.2.1, п.4.2.2, п.4.2.3
**Статус:** ❌ TODO

**Описание:** Добавить роль администратора группового чата

**Subtasks:**
- [ ] Backend: добавить поле `chatAdminId` в `Chat` entity
- [ ] Backend: при создании чата назначать создателя админом
- [ ] Backend: проверка прав при добавлении/удалении участников
- [ ] Backend: запрет удаления самого себя для админа
- [ ] Frontend: показывать кнопки управления только админу
- [ ] Frontend: отображать значок админа в списке участников

**Файлы:**
- `messenger-backend/src/main/java/com/company/messenger/entity/Chat.java`
- `messenger-backend/src/main/java/com/company/messenger/service/ChatService.java`
- `messenger-frontend/src/components/ChatWindow.tsx`

---

### Task 2.3: Валидация группового чата
**Приоритет:** MEDIUM
**Пункт ТЗ:** п.4.2.1
**Статус:** ❌ TODO

**Описание:** Название 3-50 символов, минимум 2 участника

**Subtasks:**
- [ ] Backend: `@Size(min=3, max=50)` для названия
- [ ] Backend: валидация минимум 2 участника
- [ ] Frontend: disabled кнопка "Создать" если < 2 участников
- [ ] Frontend: валидация поля названия

**Файлы:**
- `messenger-backend/src/main/java/com/company/messenger/dto/CreateChatRequest.java`
- `messenger-backend/src/main/java/com/company/messenger/service/ChatService.java`
- `messenger-frontend/src/components/NewChatModal.tsx`

---

### Task 2.4: Индикатор непрочитанных
**Приоритет:** MEDIUM
**Пункт ТЗ:** п.4.4.1
**Статус:** ❌ TODO

**Описание:** Бейдж с количеством непрочитанных сообщений

**Subtasks:**
- [ ] Backend: добавить таблицу `chat_member` с полем `lastReadMessageId`
- [ ] Backend: endpoint для пометки сообщений как прочитанных
- [ ] Backend: возвращать количество непрочитанных в списке чатов
- [ ] Frontend: отображать бейдж в `ChatList`
- [ ] Frontend: обнулять при открытии чата
- [ ] Frontend: обновлять счетчик при новом сообщении

**Файлы:**
- `messenger-backend/src/main/java/com/company/messenger/entity/ChatMember.java` (новый)
- `messenger-backend/src/main/java/com/company/messenger/repository/ChatMemberRepository.java` (новый)
- `messenger-backend/src/main/java/com/company/messenger/dto/ChatDto.java`
- `messenger-frontend/src/components/ChatList.tsx`
- `messenger-frontend/src/components/ChatList.css`

---

## 🔵 Sprint 3: Дополнительные функции

### Task 3.1: Прокрутка к Reply
**Приоритет:** LOW
**Пункт ТЗ:** п.4.4.2
**Статус:** ❌ TODO

**Описание:** Клик на блок Reply прокручивает к исходному сообщению

**Subtasks:**
- [ ] Frontend: добавить onClick на блок `.message-reply`
- [ ] Frontend: найти сообщение по `replyToId`
- [ ] Frontend: `scrollIntoView` к элементу
- [ ] Frontend: подсветка исходного сообщения (анимация)

**Файлы:**
- `messenger-frontend/src/components/ChatWindow.tsx`
- `messenger-frontend/src/components/ChatWindow.css`

---

### Task 3.2: Прикрепление изображений (Optional)
**Приоритет:** LOW (не входит в MVL)
**Пункт ТЗ:** п.4.3.2
**Статус:** ❌ TODO

**Описание:** Отправка скриншотов и изображений

**Subtasks:**
- [ ] Backend: FileController для загрузки файлов
- [ ] Backend: FileStorageService (local или S3)
- [ ] Backend: валидация формата (PNG, JPG) и размера (5 МБ)
- [ ] Backend: поле `attachmentUrl` в `Message`
- [ ] Frontend: кнопка "Прикрепить изображение"
- [ ] Frontend: превью загруженного изображения
- [ ] Frontend: lightbox для просмотра

**Файлы:**
- `messenger-backend/src/main/java/com/company/messenger/controller/FileController.java` (новый)
- `messenger-backend/src/main/java/com/company/messenger/service/FileStorageService.java` (новый)
- `messenger-backend/src/main/java/com/company/messenger/entity/Message.java`
- `messenger-frontend/src/components/ImageUpload.tsx` (новый)
- `messenger-frontend/src/components/ChatWindow.tsx`

---

## 📊 Статистика

**Всего задач:** 11
- ✅ Выполнено: 0
- ⚠️ В процессе: 1 (Task 1.3)
- ❌ Не начато: 10

**По приоритетам:**
- 🔴 HIGH: 4 задачи
- 🟡 MEDIUM: 5 задач
- 🔵 LOW: 2 задачи

**По спринтам:**
- Sprint 1: 4 задачи
- Sprint 2: 4 задачи
- Sprint 3: 2 задачи
- Опционально: 1 задача

---

## 🎯 Инструкции для Claude

При работе с задачами:
1. Всегда ссылайтесь на номер задачи (например, "реализуй Task 1.1")
2. Отмечайте выполненные subtasks через `[x]`
3. Обновляйте статус задачи (TODO → IN PROGRESS → DONE)
4. Указывайте изменённые файлы в коммитах

**Пример команды:**
```
Реализуй Task 1.1 (Поиск сотрудников)
```

Я прочитаю REQUIREMENTS.md и TASKS.md, найду задачу и выполню все subtasks.

---

**Последнее обновление:** 2026-01-21
