#!/bin/bash

# Скрипт для генерации цепочки сертификатов
# Использование: ./scripts/generate_certificates.sh [STUDENT_ID]
# Если STUDENT_ID не указан, будет использована переменная окружения STUDENT_ID

set -e

STUDENT_ID="${1:-${STUDENT_ID}}"

if [ -z "$STUDENT_ID" ]; then
    echo "Ошибка: Не указан идентификатор студента (номер студенческого билета)"
    echo "Использование: ./scripts/generate_certificates.sh [STUDENT_ID]"
    echo "Или установите переменную окружения: export STUDENT_ID=your_student_id"
    exit 1
fi

echo "Генерация цепочки сертификатов для студента: $STUDENT_ID"

CERT_DIR="certs"
mkdir -p "$CERT_DIR"

# Пароли (в продакшене должны быть в секретах)
ROOT_CA_PASS="RootCA_${STUDENT_ID}_Pass"
INT_CA_PASS="IntCA_${STUDENT_ID}_Pass"
SERVER_PASS="Server_${STUDENT_ID}_Pass"

# Имена файлов (отличаются от примера)
ROOT_CA_ALIAS="musicRootCA"
INT_CA_ALIAS="musicIntCA"
SERVER_ALIAS="musicServer"

ROOT_CA_JKS="${CERT_DIR}/musicRootCA.jks"
INT_CA_JKS="${CERT_DIR}/musicIntCA.jks"
SERVER_JKS="${CERT_DIR}/musicServer.jks"

# 1. Генерация корневого сертификата (Root CA)
echo "1. Генерация корневого сертификата..."
keytool -genkeypair \
    -alias "$ROOT_CA_ALIAS" \
    -keyalg RSA \
    -keysize 4096 \
    -validity 3650 \
    -keystore "$ROOT_CA_JKS" \
    -storepass "$ROOT_CA_PASS" \
    -dname "CN=MusicStreaming Root CA Student $STUDENT_ID, OU=Development, O=MusicStreaming, L=Moscow, ST=Moscow, C=RU" \
    -ext bc=ca:true \
    -ext KeyUsage=digitalSignature,keyCertSign

# Экспорт публичной части Root CA
echo "2. Экспорт публичной части Root CA..."
keytool -export \
    -alias "$ROOT_CA_ALIAS" \
    -keystore "$ROOT_CA_JKS" \
    -storepass "$ROOT_CA_PASS" \
    -file "${CERT_DIR}/musicRootCA.crt"

# 3. Создание keystore для Intermediate CA
echo "3. Создание промежуточного CA..."
keytool -genkeypair \
    -alias "$INT_CA_ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 1825 \
    -keystore "$INT_CA_JKS" \
    -storepass "$INT_CA_PASS" \
    -dname "CN=MusicStreaming Intermediate CA Student $STUDENT_ID, OU=Development, O=MusicStreaming, L=Moscow, ST=Moscow, C=RU"

# 4. Генерация CSR для Intermediate CA
echo "4. Генерация CSR для Intermediate CA..."
keytool -certreq \
    -alias "$INT_CA_ALIAS" \
    -keystore "$INT_CA_JKS" \
    -storepass "$INT_CA_PASS" \
    -file "${CERT_DIR}/musicIntCA.csr"

# 5. Подпись Intermediate CA через Root CA
echo "5. Подпись Intermediate CA через Root CA..."
keytool -gencert \
    -alias "$ROOT_CA_ALIAS" \
    -keystore "$ROOT_CA_JKS" \
    -storepass "$ROOT_CA_PASS" \
    -infile "${CERT_DIR}/musicIntCA.csr" \
    -outfile "${CERT_DIR}/musicIntCA.crt" \
    -validity 1825 \
    -ext "BasicConstraints:critical:true,CA:true,pathLen:0" \
    -ext "KeyUsage=digitalSignature,keyCertSign"

# 6. Импорт Root CA в Intermediate CA keystore
echo "6. Импорт Root CA в Intermediate CA keystore..."
keytool -import \
    -alias "$ROOT_CA_ALIAS" \
    -keystore "$INT_CA_JKS" \
    -storepass "$INT_CA_PASS" \
    -file "${CERT_DIR}/musicRootCA.crt" \
    -noprompt

# 7. Импорт подписанного Intermediate CA сертификата
echo "7. Импорт подписанного Intermediate CA сертификата..."
keytool -import \
    -alias "$INT_CA_ALIAS" \
    -keystore "$INT_CA_JKS" \
    -storepass "$INT_CA_PASS" \
    -file "${CERT_DIR}/musicIntCA.crt" \
    -noprompt

# 8. Создание keystore для сервера
echo "8. Создание серверного сертификата..."
keytool -genkeypair \
    -alias "$SERVER_ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 365 \
    -keystore "$SERVER_JKS" \
    -storepass "$SERVER_PASS" \
    -dname "CN=localhost Student $STUDENT_ID, OU=Development, O=MusicStreaming, L=Moscow, ST=Moscow, C=RU" \
    -ext "SAN=dns:localhost,dns:localhost.localdomain"

# 9. Генерация CSR для сервера
echo "9. Генерация CSR для сервера..."
keytool -certreq \
    -alias "$SERVER_ALIAS" \
    -keystore "$SERVER_JKS" \
    -storepass "$SERVER_PASS" \
    -file "${CERT_DIR}/musicServer.csr"

# 10. Подпись серверного CSR через Intermediate CA
echo "10. Подпись серверного CSR через Intermediate CA..."
keytool -gencert \
    -alias "$INT_CA_ALIAS" \
    -keystore "$INT_CA_JKS" \
    -storepass "$INT_CA_PASS" \
    -infile "${CERT_DIR}/musicServer.csr" \
    -outfile "${CERT_DIR}/musicServer.crt" \
    -validity 365 \
    -ext KeyUsage=digitalSignature,keyEncipherment \
    -ext EKU=serverAuth,clientAuth \
    -ext "SAN=dns:localhost,dns:localhost.localdomain"

# 11. Сборка полной цепочки в server.jks
echo "11. Сборка полной цепочки в server keystore..."

# Импорт Root CA
keytool -import \
    -alias "$ROOT_CA_ALIAS" \
    -keystore "$SERVER_JKS" \
    -storepass "$SERVER_PASS" \
    -file "${CERT_DIR}/musicRootCA.crt" \
    -noprompt

# Импорт Intermediate CA
keytool -import \
    -alias "$INT_CA_ALIAS" \
    -keystore "$SERVER_JKS" \
    -storepass "$SERVER_PASS" \
    -file "${CERT_DIR}/musicIntCA.crt" \
    -noprompt

# Импорт серверного сертификата
keytool -import \
    -alias "$SERVER_ALIAS" \
    -keystore "$SERVER_JKS" \
    -storepass "$SERVER_PASS" \
    -file "${CERT_DIR}/musicServer.crt" \
    -noprompt

# 12. Проверка цепочки
echo "12. Проверка цепочки сертификатов..."
keytool -list -v -keystore "$SERVER_JKS" -storepass "$SERVER_PASS" -alias "$SERVER_ALIAS" | grep -A 5 "Certificate chain length"

echo ""
echo "✓ Цепочка сертификатов успешно создана!"
echo ""
echo "Созданные файлы:"
echo "  - $ROOT_CA_JKS (Root CA keystore)"
echo "  - $INT_CA_JKS (Intermediate CA keystore)"
echo "  - $SERVER_JKS (Server keystore - для Spring Boot)"
echo "  - ${CERT_DIR}/musicRootCA.crt (Root CA сертификат - для импорта в доверенные)"
echo ""
echo "Пароли (сохраните их в секретах CI/CD):"
echo "  ROOT_CA_PASS: $ROOT_CA_PASS"
echo "  INT_CA_PASS: $INT_CA_PASS"
echo "  SERVER_PASS: $SERVER_PASS"

