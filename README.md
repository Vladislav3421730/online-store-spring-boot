﻿# Online Market Rest api
## Используемые технологии в проекте 

* Spring Boot
* Spring Secutiry
* Spring Data Jpa
* JWT (access/refresh)
* Spring Actuator
* Swagger
* Redis
* Test Containers
* Docker 
* Flyway
* MapStruct
* Pagination (Page, PageRequest)
    
## Инструкция по запуску 
Для начало нужно скопировать проект себе локально на компьютер
```bash
cd online-store-spring-boot
```
Затем нужно запустить Postgres и Redis через Dcoker
```bash
docker compose up
```
Затем нужно локально запустить Spring boot приложение, для этого у вам на компьютере должны быть установлена java
```bash
java -jar deploy/online-store-spring-boot-0.0.1-SNAPSHOT.jar
```
После всех манипуляций можно выполнять запросы к Rest Api
```bash
localhost:8080/api/products
```
Для перехода на Swagger можно воспользоваться
```bash
http://localhost:8080/swagger-ui/index.html
```
## Реквизиты для входа
1. Если хотите войти с ролями USER, MANAGER, ADMIN
      + Логин:***`vlad@gmail.com`***, Пароль: ***q1w2e3***
2. Если хотите войти с ролью ADMIN
      + Логин:***`admin@gmail.com`***, Пароль: ***q1w2e3***
3. Если хотите войти с ролью MANAGER
      + Логин:***`manager@gmail.com`***, Пароль: ***q1w2e3***
4. Если хотите войти с ролю USER
      + Логин:***`user@gmail.com`***, Пароль: ***q1w2e3***
