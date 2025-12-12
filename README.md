ShareIt - Сервис для шеринга вещей
📋 Описание проекта
ShareIt - это платформа для совместного использования вещей (шеринг), позволяющая пользователям брать 
в аренду предметы у других пользователей и делиться своими вещами. 
Сервис решает проблему хранения редко используемых вещей и предоставляет доступ к необходимым предметам 
без необходимости покупки.

🎯 Основные возможности
Для владельцев вещей:
📦 Добавление вещей - публикация предметов для аренды

📊 Управление доступностью - возможность отмечать вещи как доступные/недоступные

📋 Просмотр заявок - управление бронированиями своих вещей

⭐ Отзывы - получение обратной связи от арендаторов

Для арендаторов:
🔍 Поиск вещей - поиск нужных предметов по названию и описанию

📅 Бронирование - создание заявок на аренду вещей

📝 Запросы - создание запросов на вещи, которых нет в системе

💬 Отзывы - оставление комментариев после использования

Системные функции:
📋 Управление бронированиями - система подтверждения/отклонения заявок

🔔 Статусы бронирований - WAITING, APPROVED, REJECTED, CANCELED

⏰ Контроль дат - предотвращение пересечения бронирований

🔍 Расширенный поиск - поиск с фильтрацией по доступности и датам




# Схема базы данных (ER-диаграмма)

```mermaid
erDiagram

    users ||--o{ items : "owns"
    users ||--o{ bookings : "books"
    users ||--o{ requests : "creates"
    users ||--o{ comments : "writes"

    items ||--o{ bookings : "is booked"
    items ||--o{ comments : "has comments"
    items }o--|| requests : "created from"

    bookings }|--|| users : "booker"
    bookings }|--|| items : "item"

    comments }|--|| users : "author"
    comments }|--|| items : "item"

    requests }|--|| users : "requester"
    requests ||--o{ items : "generates items"

    users {
        bigint id PK
        varchar name
        varchar email UK
    }

    items {
        bigint id PK
        varchar name
        text description
        boolean available
        bigint owner_id FK
        bigint request_id FK
    }

    bookings {
        bigint id PK
        timestamp start_time
        timestamp end_time
        bigint item_id FK
        bigint booker_id FK
        varchar status
        timestamp created_time
    }

    comments {
        bigint id PK
        text text
        bigint item_id FK
        bigint author_id FK
        timestamp created
    }

    requests {
        bigint id PK
        text description
        bigint requester_id FK
        timestamp created
    }
```