# Музыкальный стриминг

REST API для управления музыкальным стриминговым сервисом. Поддерживаются пользователи, артисты, альбомы, треки и пользовательские плейлисты с расширенными операциями.

## Основные сущности
- **Artist** – исполнитель (имя, страна, биография).
- **Album** – альбом артиста (название, дата релиза, обложка).
- **Track** – трек (название, длительность, жанр, ссылка, связи с артистом и альбомом).
- **User** – пользователь сервиса (имя, фамилия, email).
- **Playlist** – плейлист пользователя (название, описание, публичность).
- **PlaylistTrack** – трек в плейлисте (позиция, связи с плейлистом и треком).

## Операции
### CRUD
Полный набор CRUD-эндпоинтов для всех сущностей (см. `src/main/java/ru/music/streaming/controller`).

### Бизнес-операции
1. `POST /api/playlists/{id}/tracks/move` – перестановка трека с пересчётом позиций.
2. `POST /api/playlists/{id}/shuffle` – случайное перемешивание плейлиста.
3. `POST /api/playlists/{id}/clone` – копирование плейлиста другому пользователю.
4. `POST /api/users/{id}/mix` – генерация персонального "Daily Mix".
5. `GET /api/users/{id}/summary` – агрегированная статистика библиотеки пользователя.

## Подготовка окружения
### Требования
- Java 21+
- PostgreSQL 12+
- Gradle (используется wrapper)

### База данных
```bash
brew install postgresql@15
brew services start postgresql@15

createdb musicdb || true
psql -d musicdb -c "CREATE USER freegatik WITH PASSWORD 'freegatik';" || true
psql -d musicdb -c "GRANT ALL PRIVILEGES ON DATABASE musicdb TO freegatik;"
psql -d musicdb -c "ALTER USER freegatik CREATEDB;"
```

### Настройки приложения
`src/main/resources/application.properties` читает переменные окружения, при отсутствии используются значения по умолчанию:
```bash
export DB_URL="jdbc:postgresql://localhost:5432/musicdb"
export DB_USERNAME="freegatik"
export DB_PASSWORD="freegatik"
```

## Запуск
```bash
lsof -ti:8081 | xargs kill -9 2>/dev/null || true
./gradlew bootRun
```
Приложение доступно по адресу `http://localhost:8081`.

## Проверка
Выполните команды из `EXAMPLES.md` – они покрывают CRUD и все бизнес-операции (создание данных → действие → проверка результата).