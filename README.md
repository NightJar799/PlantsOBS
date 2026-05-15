# Библиотеки и фреймворки в plantOBS (по микросервисам)

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

## 4. authREST (сервис аутентификации)
| Библиотека / Фреймворк | Назначение |
|------------------------|-------------|
| Spring Boot Starter Web | REST-контроллеры |
| Spring Boot Starter Security | Аутентификация, JWT-фильтры |
| Spring Boot Starter Data JPA | Хранение пользователей и ролей |
| Spring Boot Starter Validation | Валидация запросов |
| Spring Boot Starter AMQP | Отправка событий в RabbitMQ |
| JJWT (api, impl, jackson) | Генерация и проверка JWT-токенов |
| PostgreSQL | Драйвер БД |
| Jackson Databind + JSR310 | JSON-сериализация LocalDateTime |
| plantrmq | DTO для аудита |
| Lombok | Упрощение кода |

## 5. audit-service (сервис аудита)
| Библиотека / Фреймворк | Назначение |
|------------------------|-------------|
| Spring Boot Starter AMQP | Потребление сообщений из RabbitMQ |
| Spring Boot Starter Web | REST-эндпоинт для просмотра логов |
| Spring Boot Starter Actuator | Метрики, healthcheck |
| plantrmq | DTO для десериализации событий |
| Jackson Databind + JSR310 | Преобразование JSON → объекты |

---

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

`mvn -pl authREST spring-boot:run`

`-pl - необходимы для запуска только отдельного модуля`

## Тестирование

### PlantREST 

Подключенный Swagger - http://localhost:8080/swagger-ui/index.html

### AuditService

Графическое приложение RabbitMQ (http://localhost:15672/)

### AuthREST

Скрипт - `./test-full-system.sh`


