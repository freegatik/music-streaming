# Примеры запросов для macOS Terminal

Этот файл содержит готовые примеры запросов к API музыкального стримингового сервиса.

## Предварительные требования

Убедитесь, что:
1. **PostgreSQL установлен и запущен:**
   ```bash
   # Установка через Homebrew
   brew install postgresql@15
   
   # Запуск службы
   brew services start postgresql@15
   
   # Создание базы данных и пользователя
   createdb musicdb
   psql -d musicdb -c "CREATE USER user WITH PASSWORD 'user';"
   psql -d musicdb -c "GRANT ALL PRIVILEGES ON DATABASE musicdb TO user;"
   ```

2. **Сервер запущен на http://localhost:8081**
   ```bash
   ./gradlew bootRun
   ```

## Примеры запросов

### 1. Создание артиста

```bash
curl -X POST http://localhost:8081/api/artists \
  -H "Content-Type: application/json" \
  -d '{
    "name": "$uicide boy$",
    "bio": "Rap Duet",
    "country": "USA"
  }'
```

### 2. Получение всех артистов

```bash
curl http://localhost:8081/api/artists
```

### 3. Получение артиста по ID

```bash
curl http://localhost:8081/api/artists/1
```

### 4. Поиск артистов по имени

```bash
curl "http://localhost:8081/api/artists/search?name=$uicide boy$"
```

### 5. Создание альбома для артиста

```bash
curl -X POST "http://localhost:8081/api/albums?artistId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Eternal Grey",
    "releaseDate": "2016-09-11",
    "coverUrl": "https://example.com/cover.jpg"
  }'
```

### 6. Создание трека

```bash
curl -X POST "http://localhost:8081/api/tracks?artistId=1&albumId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Eclipse",
    "durationSeconds": 162,
    "genre": "Rap",
    "audioUrl": "https://example.com/audio.mp3"
  }'
```

### 7. Создание пользователя

```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Anton",
    "lastName": "Solovev",
    "email": "anton.solovev@example.com"
  }'
```

### 8. Создание плейлиста

```bash
curl -X POST "http://localhost:8081/api/playlists?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My favorite music",
    "description": "Best songs ever",
    "isPublic": true
  }'
```

### 9. Добавление трека в плейлист

```bash
curl -X POST "http://localhost:8081/api/playlists/1/tracks?trackId=1&position=0" \
  -H "Content-Type: application/json"
```

### 10. Получение треков плейлиста

```bash
curl http://localhost:8081/api/playlists/1/tracks
```

### 11. Обновление артиста

```bash
curl -X PUT http://localhost:8081/api/artists/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "$uicide boy$",
    "bio": "Rap Duet, the New Orleans-bred duo of cousins",
    "country": "USA"
  }'
```

### 12. Удаление трека из плейлиста

```bash
curl -X DELETE "http://localhost:8081/api/playlists/1/tracks/0"
```

### 13. Поиск треков по жанру

```bash
curl http://localhost:8081/api/tracks/genre/Rap
```

### 14. Получение публичных плейлистов

```bash
curl http://localhost:8081/api/playlists/public
```

### 15. Удаление артиста

```bash
curl -X DELETE http://localhost:8081/api/artists/1
```

## Полный сценарий использования

Вот пример полного сценария создания музыкальной библиотеки:

```bash
# 1. Создаем артиста
curl -X POST http://localhost:8081/api/artists \
  -H "Content-Type: application/json" \
  -d '{"name": "$uicide boy$", "bio": "Rap Duet", "country": "USA"}'

# 2. Создаем альбом
curl -X POST "http://localhost:8081/api/albums?artistId=1" \
  -H "Content-Type: application/json" \
  -d '{"title": "Eternal Grey", "releaseDate": "2016-09-11"}'

# 3. Добавляем несколько треков
curl -X POST "http://localhost:8081/api/tracks?artistId=1&albumId=1" \
  -H "Content-Type: application/json" \
  -d '{"title": "Chariot of Fire", "durationSeconds": 129, "genre": "Rap"}'

curl -X POST "http://localhost:8081/api/tracks?artistId=1&albumId=1" \
  -H "Content-Type: application/json" \
  -d '{"title": "I Want to Believe", "durationSeconds": 142, "genre": "Rap"}'

# 4. Создаем пользователя
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"firstName": "Anton", "lastName": "Solovev", "email": "anton@example.com"}'

# 5. Создаем плейлист
curl -X POST "http://localhost:8081/api/playlists?userId=1" \
  -H "Content-Type: application/json" \
  -d '{"name": "$uicide boy$", "description": "This is $uicide boy$", "isPublic": true}'

# 6. Добавляем треки в плейлист
curl -X POST "http://localhost:8081/api/playlists/1/tracks?trackId=1&position=0" \
  -H "Content-Type: application/json"

curl -X POST "http://localhost:8081/api/playlists/1/tracks?trackId=2&position=1" \
  -H "Content-Type: application/json"

# 7. Получаем результат
curl http://localhost:8081/api/playlists/1/tracks
```

## Обработка ошибок

Примеры запросов с ошибками:

```bash
# Попытка создать пользователя с некорректным email
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"firstName": "Петр", "lastName": "Иванов", "email": "invalid-email"}'

# Попытка создать артиста с пустым именем
curl -X POST http://localhost:8081/api/artists \
  -H "Content-Type: application/json" \
  -d '{"name": "", "bio": "Тест"}'
```

## Переменные для удобства

Вы можете определить переменные в терминале:

```bash
# Определение базового URL
BASE_URL="http://localhost:8081/api"

# Использование
curl -X POST "$BASE_URL/artists" \
  -H "Content-Type: application/json" \
  -d '{"name": "Test Artist", "bio": "Bio", "country": "Russia"}'

# Сохранение ID из ответа
ARTIST_ID=$(curl -s -X POST "$BASE_URL/artists" \
  -H "Content-Type: application/json" \
  -d '{"name": "Test", "bio": "Bio", "country": "RU"}' | jq -r '.id')

echo "Created artist with ID: $ARTIST_ID"
```

## Быстрое тестирование API

Для быстрой проверки работоспособности API выполните следующие команды:

```bash
# 1. Проверка доступности сервера
curl -s http://localhost:8081/api/users

# 2. Создание тестового пользователя
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User", 
    "email": "test@example.com"
  }'

# 3. Проверка создания пользователя
curl -s http://localhost:8081/api/users
```

Если все работает корректно, вы увидите созданного пользователя в ответе.
