# Foodie
Сервис предоставляющий возможность бронирования столиков у ресторанов

## Микросервисы:
1) Api_Gateway - единая точка входа;
2) Authentication_Service - сервис отвечающий за аутентификацию и авторизацию пользователей. Также отвечает за генерацию, выдачу, проверку Jwt и Refresh токенов;
3) Restaurant_Service - сервис отвечающий за управление ресторанами;
4) Booking_Service - сервис отвечающий за управление бронированиями;
5) Discovery_Service - сервис Eureka.

## Общая схема разработанной микросервисной архитектуры:
![Schema](https://github.com/StsiapanSikorsky/Foodie/images/Foodie_architecture.png)

## Инструкция по развертыванию:

## API Documentation:
1) [Authentication Service](http://localhost:8197/swagger-ui/index.html#/);
2) [Restaurant Service](http://localhost:8198/swagger-ui/index.html#/);
3) [Booking Service](http://localhost:8199/swagger-ui/index.html#/).

## Таблицы баз данных
![authentication](https://github.com/StsiapanSikorsky/Foodie/images/authentication_db_tables.png)  
![restaurants](https://github.com/StsiapanSikorsky/Foodie/images/restaurants_db_tables.png)  
![booking](https://github.com/StsiapanSikorsky/Foodie/images/booking_db_tables.png)


