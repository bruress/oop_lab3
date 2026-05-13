# backend: только ООП-описание

Этот файл описывает только объектно-ориентированную часть `lab_3/backend` без привязки к REST/БД.

## 1) Базовый абстрактный класс

`Api` (аналог `SocialApi` из lab_2)

Поля:
- `name`
- `baseUrl`

Методы:
- `initialize()` — инициализация API (статический текст)
- `fetchData(command)` — выполнение команды (статический текст)
- доступ к `name/baseUrl` через Lombok-generated методы (`@Data`/`@Getter/@Setter`)

Принципы:
- инкапсуляция: поля закрыты, доступ через методы
- абстракция: общий контракт для всех API
- полиморфизм: вызов `initialize/fetchData` через ссылку на `Api`

## 2) Наследник VK

`VkApi extends Api`

Поля:
- `version`
- `token` (или `accessToken`)

Конструкторы:
- `VkApi(name, baseUrl, version, token)`
- `VkApi(name, baseUrl, token)`
- `VkApi(name, baseUrl)`

Методы:
- `initialize()` — переопределенный метод
- `fetchData(command)` — переопределенный метод
- `getText()` — статические данные текстов
- `getLike()` — статические данные лайков

Команды `fetchData`:
- `getText` -> вернуть данные текстов
- `getLike` -> вернуть данные лайков
- прочее -> нейтральный текст о неподдерживаемой команде

## 3) Наследник TG

`TgApi extends Api`

Поля:
- `botToken`
- `chatId`

Конструкторы:
- `TgApi(name, baseUrl, token, chatId)`
- `TgApi(name, baseUrl, chatId)`
- `TgApi(name, baseUrl, token)`

Методы:
- `initialize()` — переопределенный метод
- `fetchData(command)` — переопределенный метод
- `sendMessage()` — статический ответ
- `sendPhoto()` — статический ответ

Команды `fetchData`:
- `sendMessage` -> вернуть статический ответ сообщения
- `sendPhoto` -> вернуть статический ответ фото
- прочее -> нейтральный текст о неподдерживаемой команде

## 4) Связанные сущности (смысл)

- `VkPost` — запись поста VK (принадлежит `VkApi`)
- `TgPayload` — запись payload TG (принадлежит `TgApi`)
- `Organization` (если используется) — агрегатор связанных сущностей по `organizationId`

## 5) Что демонстрирует ООП

- наследование: `VkApi`/`TgApi` от `Api`
- полиморфизм: единый вызов `fetchData(command)` для разных API
- инкапсуляция: управление доступом к полям через методы/Lombok
- переопределение: разные реализации `initialize/fetchData` в наследниках
