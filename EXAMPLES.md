# Проверочный сценарий (macOS Terminal)

Лаконичный набор команд, который запускает сервис и проверяет CRUD + бизнес-операции.

## 1. Подготовка окружения
```bash
brew services start postgresql@15
createdb musicdb || true
psql -d musicdb -c "CREATE USER freegatik WITH PASSWORD 'freegatik';" || true
psql -d musicdb -c "GRANT ALL PRIVILEGES ON DATABASE musicdb TO freegatik;"
psql -d musicdb -c "ALTER USER freegatik CREATEDB;"

export DB_URL="jdbc:postgresql://localhost:5432/musicdb"
export DB_USERNAME="freegatik"
export DB_PASSWORD="freegatik"

lsof -ti:8081 | xargs kill -9 2>/dev/null || true
./gradlew bootRun
```
Оставьте процесс `bootRun` активным.

## 2. Переменные для запросов
```bash
export API_URL="http://localhost:8081/api"
export JSON_HEADER="Content-Type: application/json"
```
> Для удобства чтения ответов установите `jq` (`brew install jq`).

## 3. CRUD-проверка
```bash
# Создать артиста
curl -X POST "$API_URL/artists" \
  -H "$JSON_HEADER" \
  -d '{"name":"Mac DeMarco","bio":"Canadian singer-songwriter","country":"Canada"}'

# Получить артистов
curl "$API_URL/artists"

# Создать трек (artistId=1, albumId=1 уже есть после инициализации)
curl -X POST "$API_URL/tracks?artistId=1&albumId=1" \
  -H "$JSON_HEADER" \
  -d '{"title":"Let Her Go","durationSeconds":210,"genre":"Indie"}'

# Создать плейлист для пользователя 1 и сохранить его id
PLAYLIST_ID=$(curl -s -X POST "$API_URL/playlists?userId=1" \
  -H "$JSON_HEADER" \
  -d '{"name":"Weekend Indie","description":"Relax mode","isPublic":true}' | jq -r '.id')

echo "Playlist id: $PLAYLIST_ID"

# Добавить в плейлист треки, которых там ещё нет
curl -X POST "$API_URL/playlists/$PLAYLIST_ID/tracks?trackId=3" -H "$JSON_HEADER"
curl -X POST "$API_URL/playlists/$PLAYLIST_ID/tracks?trackId=4" -H "$JSON_HEADER"
```

## 4. Бизнес-операции
```bash
# Переставить трек
curl -X POST "$API_URL/playlists/$PLAYLIST_ID/tracks/move" \
  -H "$JSON_HEADER" \
  -d '{"trackId":4,"newPosition":0}'

# Перемешать плейлист
curl -X POST "$API_URL/playlists/$PLAYLIST_ID/shuffle"

# Клонировать плейлист пользователю 2
curl -X POST "$API_URL/playlists/$PLAYLIST_ID/clone" \
  -H "$JSON_HEADER" \
  -d '{"targetUserId":2,"name":"Chill Copy","description":"Shared by Alice","makePublic":false}'

# Сгенерировать Daily Mix пользователю 1
curl -X POST "$API_URL/users/1/mix" \
  -H "$JSON_HEADER" \
  -d '{"name":"Daily Mix QA","description":"Smoke test mix","genre":"Indie","limit":5,"makePublic":false}'

# Получить сводку библиотеки
curl "$API_URL/users/1/summary"
```

## 5. Проверка результатов
```bash
curl "$API_URL/playlists/public"
curl "$API_URL/playlists/$PLAYLIST_ID/tracks" | jq
curl "$API_URL/albums/artist/1"
```

Если какой-либо запрос возвращает 4xx/5xx, проверьте, что сервис запущен, окружение настроено и база данных доступна.
