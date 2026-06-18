# Foodie
**Foodie** - это микросервисная платформа для регистрации ресторанов и бронирования в них столиков. Проект построен на Spring Boot 4 с использованием Eureka, API Gateway, JWT-аутентификации, хранения картинок в хранилище S3 MinIO и кеширования в Redis. Вся инфраструктура развертывается в Docker.

## Используемый стек:
- Язык: Java 21
- Фреймворк: Spring Boot 4 (Security, Data, Cloud)
- База данных: PostgreSQL
- Кэширование: Redis
- Миграции: Flyway
- HTTP Client: Feign Clients
- Service Discovery: Eureka
- Документация API: Swagger
- Тестирование: Junit5 / Mockito
- Контейнеризация: Docker

## Микросервисы:
1) **Api_Gateway:**
- единая точка входа для всех запросов
- маршрутизация к сервисам
2) **Authentication_Service:**
- регистрация/логин пользователей
- регистрация/логин собственников
- генерация JWT и Refresh токенов
- проверка токенов
3) **Restaurant_Service:**
- управление ресторанами
- управление столиками
- проверка владельца ресторана
- сохранение картинок в [S3 MinIO](http://localhost:8201)
4) **Booking_Service:**
- создание/обновление/отмена бронирований
- проверка бронирований на конфликты
- кэширование
5) **Discovery_Service:**
- [Eureka Server](http://localhost:8201)
- регистрация всех сервисов

## Общая схема разработанной микросервисной архитектуры:
![Schema](https://github.com/StsiapanSikorsky/Foodie/blob/main/images/Foodie_architecture.png)

## Инструкция по развертыванию:
### Требования:
- **Java 21**
- **Maven**
- **Docker & Docker Compose**

~~~
#Скачать проект 
git clone https://github.com/StsiapanSikorsky/Foodie.git

#Cборка JAR файлов (в каждом сервисе: api_gateway, authentication_service, restaurant_service, booking_service)
mvn clean package -DskipTests

#Запустить все сервисы в docker
cd docker_compose
docker-compose up --build -d
~~~
Проверка работоспособности: 4 микросервиса должны зарегистрироваться в [Eureka Service](http://localhost:8201);

## API Documentation:
После запуска Swagger UI доступен по ссылкам:
1) [Authentication Service](http://localhost:8197/swagger-ui/index.html#/);
2) [Restaurant Service](http://localhost:8198/swagger-ui/index.html#/);
3) [Booking Service](http://localhost:8199/swagger-ui/index.html#/).

## Мониторинг и управление:
- [Eureka Dashboard](http://localhost:8201);
- [Prometheus](http://localhost:9090);
- [Grafana](http://localhost:3000) `login:admin / password:admin`;
- [MinIO](http://localhost:9001) `login:foodie_admin / password:foodie_admin`;