# Analytics Platform

Микросервисная платформа для аналитики заказов с использованием Apache Kafka и PostgreSQL.

## Архитектура

Приложение состоит из следующих компонентов:

- **API Service** (порт 8080) - REST API для взаимодействия с системой, отправка заказов в Kafka
- **Data Service** (порт 8081) - Сервис обработки данных, потребитель Kafka, работа с базой данных
- **Apache Kafka** (порт 9092) - Брокер сообщений для асинхронной обработки заказов
- **ZooKeeper** (порт 2181) - Координатор для Kafka
- **PostgreSQL** (порт 5432) - Реляционная база данных для хранения заказов и клиентов


## Предварительные требования

- Docker 20.10+
- Docker Compose 2.0+
- Java 17 (для локальной разработки)
- Maven 3.8+ (для локальной разработки)

## Быстрый старт

### 1. Клонирование репозитория

```bash
git clone https://github.com/Luckermt/kafk.git
cd analytics-platform
```

### 2. Настройка окружения

Создайте файл `.env` в корне проекта


### 3. Запуск приложения

```bash
docker-compose up -d
```

### 4. Проверка работоспособности

```bash
# Проверка статуса сервисов
docker-compose ps

# Просмотр логов
docker-compose logs -f
```

## API Endpoints

### API Service (порт 8080)

#### Создание заказа
```http
POST /api/orders
Content-Type: application/json

{
  "customerId": 1,
  "productName": "Laptop",
  "quantity": 1,
  "price": 999.99,
  "status": "PENDING"
}
```

#### Поиск заказов
```http
GET /api/orders/search?product=Laptop&status=COMPLETED
```

#### Отчеты по продажам
```http
GET /api/reports/sales-by-product
GET /api/reports/customer-spending
GET /api/reports/daily-revenue
```

### Data Service (порт 8081)

Прямой доступ к данным (для внутреннего использования):

```http
GET /api/data/orders/search?product=Laptop
GET /api/data/reports/sales-by-product
GET /api/data/reports/customer-spending
GET /api/data/reports/daily-revenue
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
| `DATA_SERVICE_PORT` | Порт Data Service | `8081` |
| `API_SERVICE_PORT` | Порт API Service | `8080` |

## Разработка

### Сборка Docker образов

```bash
# Сборка всех образов
docker-compose build

# Сборка конкретного сервиса
docker-compose build api-service
docker-compose build data-service
```

## Мониторинг и отладка

### Просмотр логов

```bash
# Все сервисы
docker-compose logs -f

# Конкретный сервис
docker-compose logs -f api-service
docker-compose logs -f data-service
docker-compose logs -f kafka
```

### Проверка Kafka

```bash
# Просмотр топиков
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Чтение сообщений из топика
docker exec -it kafka kafka-console-consumer --topic orders-topic --from-beginning --bootstrap-server localhost:9092
```

### Проверка PostgreSQL

```bash
# Подключение к БД
docker exec -it postgres psql -U analytics_user -d analytics_db

# Просмотр таблиц
\dt

# Запросы к данным
SELECT * FROM customers;
SELECT * FROM orders;
```


## Технологический стек

- **Java 17** с Spring Boot 3.x
- **Apache Kafka** для асинхронной обработки сообщений
- **PostgreSQL 15** для хранения данных
- **Docker** для контейнеризации
- **Spring Data JPA** для работы с БД
- **Spring Kafka** для интеграции с Kafka
