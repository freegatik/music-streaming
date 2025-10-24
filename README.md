# Музыкальный стриминг

REST API для управления музыкальным стриминговым сервисом с возможностью создания плейлистов, управления треками, альбомами и артистами.

## Описание

Этот проект представляет собой бэкенд для музыкального стримингового сервиса. Пользователи могут создавать свои плейлисты, добавлять в них треки от любимых артистов, искать музыку по различным критериям. Система позволяет организовывать треки в альбомы и управлять всей музыкальной библиотекой.

## Технологии

- **Java 21+**
- **Spring Boot 3.3.5**
- **Spring Data JPA**
- **PostgreSQL**
- **Hibernate**
- **Gradle 9.0**

## Архитектура проекта

Проект построен по классической трехслойной архитектуре:

- **Controller** — REST API контроллеры для обработки HTTP запросов
- **Service** — бизнес-логика приложения
- **Repository** — доступ к данным через Spring Data JPA
- **Model** — сущности базы данных

## Сущности

### 1. Artist (Артист)

Представляет исполнителя или музыкальную группу.

- `id` — уникальный идентификатор
- `name` — имя артиста (обязательное поле)
- `bio` — биография
- `country` — страна

### 2. Album (Альбом)

Музыкальный альбом, принадлежащий определенному артисту.

- `id` — уникальный идентификатор
- `title` — название альбома (обязательное поле)
- `artistId` — ID артиста (обязательное поле)
- `releaseDate` — дата релиза
- `coverUrl` — ссылка на обложку альбома

### 3. Track (Трек)

Музыкальная композиция.

- `id` — уникальный идентификатор
- `title` — название трека (обязательное поле)
- `artistId` — ID артиста (обязательное поле)
- `albumId` — ID альбома (необязательное поле)
- `durationSeconds` — длительность в секундах
- `genre` — музыкальный жанр
- `audioUrl` — ссылка на аудиофайл

### 4. User (Пользователь)

Зарегистрированный пользователь сервиса.

- `id` — уникальный идентификатор
- `firstName` — имя (обязательное поле)
- `lastName` — фамилия (обязательное поле)
- `email` — электронная почта (обязательное поле, уникальное)

### 5. Playlist (Плейлист)

Пользовательский плейлист с набором треков.

- `id` — уникальный идентификатор
- `name` — название плейлиста (обязательное поле)
- `description` — описание
- `userId` — ID владельца (обязательное поле)
- `createdAt` — дата создания
- `isPublic` — публичный ли плейлист

### 6. PlaylistTrack (Трек в плейлисте)

Связь между плейлистом и треком с указанием позиции.

- `id` — уникальный идентификатор
- `playlistId` — ID плейлиста (обязательное поле)
- `trackId` — ID трека (обязательное поле)
- `position` — позиция трека в плейлисте (обязательное поле)

## API Endpoints

### Артисты

- `POST /api/artists` — создать артиста
- `GET /api/artists` — получить всех артистов
- `GET /api/artists/{id}` — получить артиста по ID
- `PUT /api/artists/{id}` — обновить данные артиста
- `DELETE /api/artists/{id}` — удалить артиста
- `GET /api/artists/search?name={name}` — поиск артистов по имени
- `GET /api/artists/country/{country}` — получить артистов по стране

### Альбомы

- `POST /api/albums?artistId={artistId}` — создать альбом
- `GET /api/albums` — получить все альбомы
- `GET /api/albums/{id}` — получить альбом по ID
- `PUT /api/albums/{id}` — обновить альбом
- `DELETE /api/albums/{id}` — удалить альбом
- `GET /api/albums/artist/{artistId}` — получить альбомы артиста
- `GET /api/albums/search?title={title}` — поиск альбомов по названию

### Треки

- `POST /api/tracks?artistId={artistId}&albumId={albumId}` — создать трек
- `GET /api/tracks` — получить все треки
- `GET /api/tracks/{id}` — получить трек по ID
- `PUT /api/tracks/{id}` — обновить трек
- `DELETE /api/tracks/{id}` — удалить трек
- `GET /api/tracks/artist/{artistId}` — получить треки артиста
- `GET /api/tracks/album/{albumId}` — получить треки альбома
- `GET /api/tracks/search?title={title}` — поиск треков по названию
- `GET /api/tracks/genre/{genre}` — получить треки по жанру

### Пользователи

- `POST /api/users` — создать пользователя
- `GET /api/users` — получить всех пользователей
- `GET /api/users/{id}` — получить пользователя по ID
- `PUT /api/users/{id}` — обновить данные пользователя
- `DELETE /api/users/{id}` — удалить пользователя
- `GET /api/users/email/{email}` — получить пользователя по email

### Плейлисты

- `POST /api/playlists?userId={userId}` — создать плейлист
- `GET /api/playlists` — получить все плейлисты
- `GET /api/playlists/{id}` — получить плейлист по ID
- `PUT /api/playlists/{id}` — обновить плейлист
- `DELETE /api/playlists/{id}` — удалить плейлист
- `GET /api/playlists/user/{userId}` — получить плейлисты пользователя
- `GET /api/playlists/public` — получить публичные плейлисты
- `GET /api/playlists/search?name={name}` — поиск плейлистов по названию
- `POST /api/playlists/{playlistId}/tracks?trackId={trackId}&position={position}` — добавить трек в плейлист
- `DELETE /api/playlists/{playlistId}/tracks/{position}` — удалить трек из плейлиста
- `GET /api/playlists/{playlistId}/tracks` — получить треки плейлиста

## Бизнес-логика

### Основные правила:

1. **Уникальность email** — в системе не может быть двух пользователей с одинаковым email
2. **Позиции в плейлисте** — каждый трек занимает уникальную позицию, дублирование невозможно
3. **Обязательная связь с артистом** — каждый трек и альбом должен иметь артиста
4. **Каскадное удаление** — при удалении артиста удаляются все его треки и альбомы
5. **Валидация данных** — все поля проверяются на корректность перед сохранением

## Настройка и запуск

### Требования

- Java 21 или выше (проект протестирован на Java 21-24)
- PostgreSQL 12 или выше
- Gradle 9.x (включен wrapper)

### Настройка базы данных

1. **Установка PostgreSQL на macOS:**

```bash
# Установка через Homebrew
brew install postgresql@15

# Запуск службы PostgreSQL
brew services start postgresql@15

# Добавление PostgreSQL в PATH (добавьте в ~/.zshrc)
export PATH="/opt/homebrew/opt/postgresql@15/bin:$PATH"
```

2. **Создание базы данных и пользователя:**

```bash
# Создание базы данных
createdb musicdb

# Создание пользователя (если не существует)
psql -d musicdb -c "CREATE USER freegatik WITH PASSWORD 'freegatik';"

# Предоставление прав
psql -d musicdb -c "GRANT ALL PRIVILEGES ON DATABASE musicdb TO freegatik;"
psql -d musicdb -c "ALTER USER freegatik CREATEDB;"
```

3. **Параметры подключения** (уже настроены в `src/main/resources/application.properties`):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/musicdb
spring.datasource.username=freegatik
spring.datasource.password=freegatik
```

### Запуск приложения

#### Через командную строку:

```bash
./gradlew bootRun
```

#### Для Windows:

```bash
.\gradlew.bat bootRun
```

После запуска приложение будет доступно по адресу: `http://localhost:8081`

> **Примечание:** Приложение настроено на порт 8081 для избежания конфликтов с другими сервисами.

### Сборка проекта

Для создания исполняемого JAR файла:

```bash
./gradlew clean build
```

Готовый файл будет находиться в папке `build/libs/`

## Примеры использования

### Создание артиста

```bash
curl -X POST http://localhost:8081/api/artists \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mac DeMarco",
    "bio": "Indie",
    "country": "Canada"
  }'
```

### Создание альбома

```bash
curl -X POST "http://localhost:8081/api/albums?artistId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "2",
    "releaseDate": "2012-10-16"
  }'
```

### Создание трека

```bash
curl -X POST "http://localhost:8081/api/tracks?artistId=1&albumId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Chamber Of Reflection",
    "durationSeconds": 231,
    "genre": "Indie"
  }'
```

### Создание пользователя

```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Anton",
    "lastName": "Solovev",
    "email": "anton@example.com"
  }'
```

### Создание плейлиста

```bash
curl -X POST "http://localhost:8081/api/playlists?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My favorite songs",
    "description": "Best songs ever",
    "isPublic": true
  }'
```

### Добавление трека в плейлист

```bash
curl -X POST "http://localhost:8081/api/playlists/1/tracks?trackId=1&position=0" \
  -H "Content-Type: application/json"
```

### Получение всех артистов

```bash
curl http://localhost:8081/api/artists
```

### Поиск треков по жанру

```bash
curl http://localhost:8081/api/tracks/genre/Инди
```

### Получение публичных плейлистов

```bash
curl http://localhost:8081/api/playlists/public
```

## Обработка ошибок

API возвращает понятные сообщения об ошибках в формате JSON:

```json
{
  "timestamp": "2025-01-10T12:00:00",
  "status": 400,
  "error": "Ошибка валидации",
  "errors": {
    "name": "Имя артиста не может быть пустым",
    "email": "Некорректный email"
  }
}
```

## Информация о портах

- **Приложение:** `http://localhost:8081`
- **PostgreSQL:** `localhost:5432`
- **База данных:** `musicdb`

## Будущие улучшения

1. Добавление системы аутентификации и авторизации пользователей
2. Реализация функционала "избранное" для треков и альбомов
3. Статистика прослушиваний и популярные треки
4. Рекомендации треков на основе предпочтений пользователя
5. Возможность добавления комментариев и оценок к трекам
6. Интеграция с внешними музыкальными сервисами
7. Поддержка загрузки обложек альбомов и аватаров артистов