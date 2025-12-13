# Сертификаты для HTTPS

Эта директория содержит сертификаты для настройки HTTPS в приложении.

## ⚠️ ВАЖНО: Безопасность

**НЕ КОММИТЬТЕ** следующие файлы в репозиторий:
- `*.jks` (keystore с приватными ключами)
- `*.csr` (запросы на сертификат)

**МОЖНО КОММИТЬТЬ**:
- `*.crt` (публичные сертификаты)

## Генерация сертификатов

Для генерации цепочки сертификатов выполните:

```bash
./scripts/generate_certificates.sh [STUDENT_ID]
```

Или установите переменную окружения:

```bash
export STUDENT_ID=your_student_id
./scripts/generate_certificates.sh
```

Где `STUDENT_ID` - ваш номер студенческого билета.

## Структура цепочки

Цепочка состоит из трёх звеньев:
1. **Root CA** (`musicRootCA.jks`) - корневой центр сертификации
2. **Intermediate CA** (`musicIntCA.jks`) - промежуточный центр сертификации
3. **Server Certificate** (`musicServer.jks`) - сертификат сервера

## Использование в Spring Boot

После генерации сертификатов, скопируйте `musicServer.jks` в `src/main/resources/certs/`:

```bash
cp certs/musicServer.jks src/main/resources/certs/musicServer.jks
```

Или настройте путь в `application.properties`:

```properties
server.ssl.key-store=file:./certs/musicServer.jks
```

## Пароли

Пароли генерируются автоматически на основе `STUDENT_ID`:
- Root CA: `RootCA_{STUDENT_ID}_Pass`
- Intermediate CA: `IntCA_{STUDENT_ID}_Pass`
- Server: `Server_{STUDENT_ID}_Pass`

**В продакшене** используйте переменные окружения или секреты CI/CD для хранения паролей.

## Добавление Root CA в доверенные

Для того чтобы браузер доверял сертификату, нужно добавить `musicRootCA.crt` в доверенные корневые сертификаты:

### Windows
1. Откройте `certmgr.msc`
2. Перейдите в "Доверенные корневые центры сертификации" → "Сертификаты"
3. Правый клик → "Все задачи" → "Импорт..."
4. Выберите `certs/musicRootCA.crt`

### macOS
```bash
sudo security add-trusted-cert -d -r trustRoot -k /Library/Keychains/System.keychain certs/musicRootCA.crt
```

### Linux (Ubuntu/Debian)
```bash
sudo cp certs/musicRootCA.crt /usr/local/share/ca-certificates/musicRootCA.crt
sudo update-ca-certificates
```

## CI/CD

Для использования в CI/CD настройте следующие секреты:

### GitHub Secrets
- `STUDENT_ID` - номер студенческого билета
- `SSL_KEYSTORE_PASSWORD` - пароль для keystore (опционально, если хотите использовать свой)
- `SSL_KEYSTORE_BASE64` - base64-encoded keystore файл (опционально, если хотите использовать предварительно созданный)

### GitLab CI/CD Variables
- `STUDENT_ID` - номер студенческого билета
- `SSL_KEYSTORE_PASSWORD` - пароль для keystore
- `SSL_KEYSTORE_BASE64` - base64-encoded keystore файл

## Проверка цепочки

Для проверки цепочки сертификатов:

```bash
keytool -list -v -keystore certs/musicServer.jks -storepass Server_{STUDENT_ID}_Pass -alias musicServer
```

В выводе должна быть цепочка длиной 3 (Root CA → Intermediate CA → Server).

