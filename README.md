erDiagram
users ||--o{ items : owns
users ||--o{ bookings : books
users ||--o{ requests : creates
users ||--o{ comments : writes

    items ||--o{ bookings : "is booked"
    items ||--o{ comments : "has comments"
    items }o--|| requests : "created from"
    
    bookings }|--|| users : booker
    bookings }|--|| items : item
    
    comments }|--|| users : author
    comments }|--|| items : item
    
    requests }|--|| users : requester
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