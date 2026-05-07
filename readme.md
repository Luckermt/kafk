# Analytics Platform

Микросервисная платформа для аналитики заказов с использованием Apache Kafka и PostgreSQL. Поддерживает партиционирование сообщений для масштабируемой и упорядоченной обработки данных.

## Архитектура

Приложение состоит из следующих компонентов:

- **API Service** (порт 8080) - REST API для взаимодействия с системой, отправка заказов в Kafka с ключом партиционирования
- **Data Service** (порт 8081) - Сервис обработки данных, потребитель Kafka с параллельной обработкой партиций, работа с базой данных
- **Apache Kafka** (порт 9092) - Брокер сообщений для асинхронной обработки заказов с поддержкой партиционирования
- **ZooKeeper** (порт 2181) - Координатор для Kafka
- **PostgreSQL** (порт 5432) - Реляционная база данных для хранения заказов и клиентов

### Особенности реализации

- **Партиционирование Kafka**: Используется `customerId` как ключ партиционирования для гарантии порядка обработки заказов одного клиента
- **Параллельная обработка**: Data Service обрабатывает разные партиции параллельно (concurrency = 3)
- **Гарантированный порядок**: Все заказы одного клиента попадают в одну партицию и обрабатываются последовательно
- **Равномерное распределение**: Хеширование ключа обеспечивает равномерное распределение нагрузки между партициями


## Предварительные требования

- Docker 20.10+
- Docker Compose 2.0+
- Java 17
- Maven 3.8+
- curl или Postman

## Быстрый старт

### 1. Клонирование репозитория

```bash
git clone https://github.com/Luckermt/kafk.git
cd analytics-platform
```

### 2. Настройка окружения

Создайте файл `.env` в корне проекта:

```env
# PostgreSQL Configuration
POSTGRES_DB=analytics_db
POSTGRES_USER=analytics_user
POSTGRES_PASSWORD=analytics_pass
POSTGRES_PORT=5432

# ZooKeeper Configuration
ZOOKEEPER_CLIENT_PORT=2181
ZOOKEEPER_TICK_TIME=2000

# Kafka Configuration
KAFKA_PORT=9092
KAFKA_INTERNAL_PORT=29092
KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1

# Data Service Configuration
DATA_SERVICE_PORT=8081
DATA_SERVICE_SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/analytics_db
DATA_SERVICE_SPRING_DATASOURCE_USERNAME=analytics_user
DATA_SERVICE_SPRING_DATASOURCE_PASSWORD=analytics_pass
DATA_SERVICE_SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:29092

# API Service Configuration
API_SERVICE_PORT=8080
API_SERVICE_DATA_SERVICE_URL=http://data-service:8081
API_SERVICE_SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:29092
```

### 3. Запуск приложения

```bash
# Сборка и запуск всех сервисов
docker-compose up -d

# Проверка статуса
docker-compose ps

# Просмотр логов в реальном времени
docker-compose logs -f
```

### 4. Проверка работоспособности

```bash
# Проверка API Service
curl http://localhost:8080/api/orders/search

# Проверка Data Service
curl http://localhost:8081/api/data/reports/sales-by-product

# Создание тестового заказа
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "productName": "Test Product",
    "quantity": 1,
    "price": 99.99,
    "status": "PENDING"
  }'
```

## API Endpoints

### API Service (порт 8080)

#### Создание заказа
```http
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "productName": "Test Product",
    "quantity": 1,
    "price": 99.99,
    "status": "PENDING"
  }'
```

**Особенности:**
- Все заказы с одинаковым `customerId` попадают в одну партицию Kafka
- Статус по умолчанию: "PENDING"


**Пример ответа:**
```
Order sent to Kafka successfully with key: 1
```

#### Поиск заказов
```http
# Поиск по продукту
GET /api/orders/search?product=Laptop

# Поиск по статусу
GET /api/orders/search?status=COMPLETED

# Комбинированный поиск
GET /api/orders/search?product=Laptop&status=COMPLETED

# Все заказы
GET /api/orders/search
```

**Поддерживаемые статусы:** PENDING, COMPLETED, CANCELLED

#### Отчеты по продажам
```http
GET /api/reports/sales-by-product
GET /api/reports/customer-spending
GET /api/reports/daily-revenue
```

### Data Service (порт 8081)

Прямой доступ к данным:

```http
GET /api/data/orders/search?product=Laptop
GET /api/data/reports/sales-by-product
GET /api/data/reports/customer-spending
GET /api/data/reports/daily-revenue
```

## Тестирование

### Ручное тестирование

#### 1. Тестирование партиционирования
```bash
# Создать несколько заказов для одного клиента
for i in {1..5}; do
  curl -X POST http://localhost:8080/api/orders \
    -H "Content-Type: application/json" \
    -d "{
      \"customerId\": 1,
      \"productName\": \"Product $i\",
      \"quantity\": $i,
      \"price\": $((i * 10)).99,
      \"status\": \"PENDING\"
    }"
done

# Создать заказы для разных клиентов
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId": 2, "productName": "Keyboard", "quantity": 1, "price": 89.99, "status": "PENDING"}'

curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId": 3, "productName": "Mouse", "quantity": 2, "price": 29.99, "status": "COMPLETED"}'
```

#### 2. Тестирование поиска и отчетов
```bash
# Подождать обработки сообщений
sleep 2

# Проверить заказы
curl "http://localhost:8080/api/orders/search?status=PENDING"
curl "http://localhost:8080/api/reports/sales-by-product"
curl "http://localhost:8080/api/reports/customer-spending"
```

## Конфигурация

### Переменные окружения

Все основные настройки вынесены в `.env` файл:

| Переменная | Описание | Значение по умолчанию |
|-----------|----------|----------------------|
| `POSTGRES_DB` | Имя базы данных | `analytics_db` |
| `POSTGRES_USER` | Пользователь БД | `analytics_user` |
| `POSTGRES_PASSWORD` | Пароль БД | `analytics_pass` |
| `KAFKA_PORT` | Порт Kafka | `9092` |
| `KAFKA_NUM_PARTITIONS` | Количество партиций для новых топиков | `3` |
| `DATA_SERVICE_PORT` | Порт Data Service | `8081` |
| `API_SERVICE_PORT` | Порт API Service | `8080` |

### Параметры Kafka

| Параметр | Описание | Значение |
|----------|----------|----------|
| `partitions` | Количество партиций в топике | 3 |
| `replication.factor` | Фактор репликации | 1 |
| `key.serializer` | Сериализатор ключа | StringSerializer |
| `value.serializer` | Сериализатор значения | JsonSerializer |
| `partition.key` | Ключ партиционирования | customerId |

## Мониторинг и отладка

### Просмотр логов

```bash
# Все сервисы
docker-compose logs -f

# Конкретный сервис с фильтрацией по партициям
docker-compose logs -f data-service | grep "partition"
docker-compose logs -f api-service | grep "partition key"
```

### Проверка Kafka

```bash
# Просмотр топиков и партиций
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
docker exec -it kafka kafka-topics --describe --topic orders-topic --bootstrap-server localhost:9092

# Чтение сообщений из конкретной партиции
docker exec -it kafka kafka-console-consumer \
  --topic orders-topic \
  --partition 0 \
  --from-beginning \
  --bootstrap-server localhost:9092 \
  --property print.key=true \
  --property key.separator=":"

# Просмотр метаданных партиций
docker exec -it kafka kafka-run-class kafka.tools.GetOffsetShell \
  --broker-list localhost:9092 \
  --topic orders-topic \
  --time -1
```

### Проверка PostgreSQL

```bash
# Подключение к БД
docker exec -it postgres psql -U analytics_user -d analytics_db

# Просмотр таблиц
\dt

# Проверка распределения заказов по клиентам
SELECT c.name, COUNT(o.id) as order_count 
FROM customers c 
LEFT JOIN orders o ON c.id = o.customer_id 
GROUP BY c.name 
ORDER BY order_count DESC;
```

### Мониторинг партиционирования

```bash
# Проверка распределения сообщений по партициям
docker-compose logs data-service | grep "Received message with key"

# Статистика по партициям
docker exec -it kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --group data-service-group \
  --describe
```

## Разработка

### Сборка Docker образов

```bash
# Сборка всех образов
docker-compose build

# Сборка конкретного сервиса
docker-compose build api-service
docker-compose build data-service
```

### Пересоздание топика с новыми партициями

```bash
# Удаление существующего топика
docker exec -it kafka kafka-topics --delete \
  --topic orders-topic \
  --bootstrap-server localhost:9092

# Создание с новым количеством партиций
docker exec -it kafka kafka-topics --create \
  --topic orders-topic \
  --bootstrap-server localhost:9092 \
  --partitions 5 \
  --replication-factor 1
```

## Технологический стек

- **Java 17** с Spring Boot 3.x
- **Apache Kafka** для асинхронной обработки сообщений с партиционированием
- **PostgreSQL 15** для хранения данных
- **Docker** для контейнеризации
- **Spring Data JPA** для работы с БД
- **Spring Kafka** для интеграции с Kafka
- **Jackson** для сериализации/десериализации JSON
- **Confluent Platform** для Kafka и ZooKeeper образов

## Производительность и масштабирование

- **Количество партиций**: 3
- **Параллелизм потребителей**: 3 потока в Data Service
- **Ключ партиционирования**: customerId для локализации данных клиента
- **Гарантии доставки**: At-least-once с автоматическим коммитом оффсетов