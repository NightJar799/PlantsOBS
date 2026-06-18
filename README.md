# Pet-проект plantOBS

Это pet-проект реализующий систему мониторинга растения при помощи различных датчиков всего в проекте 6 микросервисов:
1. plantREST - реализация контракта через API REST и GraphQL
2. notification-service - сервис уведомлений (WebSocket + RabbitMQ), заменивший authREST
3. auditService - сервис сбора записей и перенаправления запросов из rabbitMQ
4. grpc-analytics-server - gRPC-сервер для аналитики растений (вычисление метрик)
5. grpc-robot-customise-client - gRPC-клиент, слушает RabbitMQ и вызывает grpc-analytics-server
6. RabbitMQ - шина проекта (не является микросервисом, но используется для обмена событиями)

## Контракты
1. plantOBS - контракт для RESTAPI
2. plantrmq - общая библиотека событий rabbitMQ
3. plants-grpc-contract - gRPC-контракт (определения .proto и сгенерированные стабы)

## Библиотеки и фреймворки в plantOBS 

## 1. plantOBS (ядро, JPA-сущности)
| Библиотека / Фреймворк | Назначение |
|------------------------|-------------|
| Spring Boot Starter Data JPA | Работа с БД через Hibernate, репозитории |
| Jakarta Validation API | Валидация полей сущностей |
| Jackson Annotations | Аннотации для JSON-сериализации |
| Swagger Annotations Jakarta | Документирование моделей для OpenAPI |
| Lombok | Генерация геттеров/сеттеров, конструкторов |
| Spring WebMVC | Поддержка HATEOAS и REST-моделей |
| Spring Boot Starter HATEOAS | Построение гипермедиа-ссылок |

## 2. plantrmq (общая библиотека для RabbitMQ)
| Библиотека / Фреймворк | Назначение |
|------------------------|-------------|
| Jackson Annotations | Сериализация DTO в JSON |
| Lombok | Сокращение кода (@Data, @Builder) |

## 3. plantREST (основной API-шлюз)
| Библиотека / Фреймворк | Назначение |
|------------------------|-------------|
| Spring WebMVC | REST-контроллеры |
| Spring Boot Starter HATEOAS | Генерация ссылок в ответах |
| Spring Boot Starter Data JPA | Доступ к данным через репозитории plantOBS |
| Swagger Annotations Jakarta | Документирование API |
| SpringDoc OpenAPI Starter WebMVC UI | Swagger UI + OpenAPI 3.0 |
| Jakarta Validation API | Валидация входящих DTO |
| Jackson Annotations | JSON-маппинг |
| plantOBS | Сущности и репозитории |
| Lombok | Генерация кода |
| Netflix DGS GraphQL Spring GraphQL Starter | GraphQL-эндпоинты (DataFetchers) |
| Netflix DGS Extended Scalars | Поддержка дополнительных скаляров |
| GraphiQL Spring Boot Starter | Интерактивная GraphQL-консоль |

## 4. notification-service (сервис уведомлений)
| Библиотека / Фреймворк | Назначение |
|------------------------|-------------|
| Spring Boot Starter WebSocket | WebSocket-поддержка для отправки уведомлений в реальном времени |
| Spring Boot Starter AMQP | Потребление событий из RabbitMQ |
| Spring Boot Starter Web | REST-эндпоинты |
| Spring Boot Starter Actuator | Метрики и healthcheck |
| plantrmq | DTO для десериализации событий |
| Jackson Databind + JSR310 | JSON-сериализация/десериализация LocalDateTime |

## 5. auditService (сервис аудита)
| Библиотека / Фреймворк | Назначение |
|------------------------|-------------|
| Spring Boot Starter AMQP | Потребление сообщений из RabbitMQ |
| Spring Boot Starter Web | REST-эндпоинт для просмотра логов |
| Spring Boot Starter Actuator | Метрики, healthcheck |
| plantrmq | DTO для десериализации событий |
| Jackson Databind + JSR310 | Преобразование JSON в объекты |

## 6. grpc-analytics-server (gRPC-сервер аналитики)
| Библиотека / Фреймворк | Назначение |
|------------------------|-------------|
| Spring Boot Starter Web | Actuator / health-проверки |
| Spring Boot Starter Actuator | Метрики и healthcheck |
| plants-grpc-contract | Сгенерированные gRPC-стабы (сервисный интерфейс) |
| gRPC Netty Shaded | Транспортный уровень для gRPC-сервера |

## 7. grpc-robot-customise-client (gRPC-клиент, слушает RabbitMQ)
| Библиотека / Фреймворк | Назначение |
|------------------------|-------------|
| Spring Boot Starter Web | Actuator / health-проверки |
| Spring Boot Starter AMQP | Потребление событий из RabbitMQ |
| Spring Boot Starter Actuator | Метрики и healthcheck |
| plants-grpc-contract | Сгенерированные gRPC-стабы |
| gRPC Netty Shaded | Транспортный уровень для gRPC-клиента |
| plantrmq | DTO для десериализации событий из RabbitMQ |
| Jackson Databind + JSR310 | JSON-сериализация/десериализация |

## Общие технологии
| Технология | Назначение |
|------------|-------------|
| Java | Язык программирования |
| Maven | Система сборки |
| Spring Boot | Основной фреймворк |
| RabbitMQ | Брокер сообщений |
| PostgreSQL | Реляционная БД |

## Запуск
    
### Поднятие RabbitMQ и PostgreSQL

docker-compose up

### Запуск микро-сервисов

`mvn -pl audit-service spring-boot:run`

`mvn -pl plantREST spring-boot:run`

`mvn -pl grpc-analytics-server spring-boot:run`

`mvn -pl grpc-robot-customise-client spring-boot:run`

`mvn -pl notification-service spring-boot:run`

`-pl - необходимы для запуска только отдельного модуля`

## Тестирование

### PlantREST 

Подключенный Swagger - http://localhost:8080/swagger-ui/index.html

### AuditService

Графическое приложение RabbitMQ (http://localhost:15672/)

### AuthREST

Скрипт - `./test-full-system.sh`


