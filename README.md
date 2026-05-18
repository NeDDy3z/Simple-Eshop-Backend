# E-Shop Microservices Architecture

A robust, distributed e-commerce platform built with Spring Boot, leveraging a microservices architecture to handle product management, order processing, and asynchronous notifications.

## 🚀 Sestavení a spuštění projektu (Build & Run)

Aplikace je plně kontejnerizovaná pomocí Dockeru. Všechny potřebné služby (databáze, Kafka, Elasticsearch, aplikační moduly) se spouští jediným příkazem.

1. Nalezněte si repozitář:
   ```bash
   git clone https://gitlab.fel.cvut.cz/vanekeri/nss-semestralni-prace.git
   cd nss-semestralni-prace
   ```
2. Sestavte a spusťte celý projekt (inicializace vytvoří databáze a testovací data):
   ```bash
   docker-compose up --build
   ```
3. Aplikace (Nginx Gateway) naslouchá na portu `80` (tj. `http://localhost`).

---

## 📌 Implementované Use Cases a mapování na endpointy

V aplikaci je naimplementováno více než 4 Use Cases, aby si každý z členů týmu odpracoval svou část. Lze je testovat připravenou kolekcí v `docs/postman-test-collection/eshop_collection.json`.

1. **Založení objednávky s fixací kupní ceny a aplikací slev (FR-01, FR-03, FR-04):**
   * Endpoint: `POST /api/orders/checkout` (Vyžaduje JWT token, asynchronně zpracováno)
2. **Kategorizace a vyhledávání produktů (FR-02):**
   * Endpointy: `GET /api/products` nebo přes kategorie `GET /api/categories/{name}`
3. **Správa produktů administrátorem (FR-06):**
   * Endpointy: `POST /api/products` (vytvoření), `DELETE /api/products/{id}` (smazání)
4. **Správa vlastního profilu uživatelem / Historie (FR-05):**
   * Endpointy: `GET /api/users/me`, `GET /api/orders`
5. **Práce s distribuovaným košíkem (NFR-05):**
   * Endpointy: `POST /api/basket/add`, `DELETE /api/basket`

---

## 🧠 Přehled technologií a splněných kritérií (Obhajoba Checklist)

### 🗄️ Cache (Hazelcast)
* **Využití:** Distribuované ukládání relací a obsahů nákupního košíku. Aktivní (read/write in-memory) přístup přes REST API.
* **Endpoint pro ověření:** `GET /api/basket` (Předtím zavolejte `POST /api/basket/add` pro naplnění dat. Data se ihned ukládají a čtou z Hazelcast instance).

### 📨 Messaging (Apache Kafka)
* **Využití:** Dekuplování modulu objednávek od produktového a notifikačního modulu.
* **Producer Endpoint:** `POST /api/orders/checkout` pošle při úspěchu asynchronní zprávu. K dispozici je také dedikovaný testovací **`POST /api/products/test-kafka`**.
* **Consumer:** Posluchač `StockListener` v Product Service a `NotificationListener` v Notification Service přijímají `order-created` topic a reagují na něj v logu/systému.

### 📐 Design Patterns (Implementovány tam, kde logicky dávají smysl)
* **Facade Pattern:** Koordinuje složitý checkout přes služby (`cz.cvut.fel.nss.order.facade.CheckoutFacade`).
* **Strategy Pattern:** Výpočet procentuální nebo fixní slevy (`cz.cvut.fel.nss.order.strategy.DiscountStrategy` a její potomci).
* **Observer Pattern:** Přijímání zpráv od Kafky (`cz.cvut.fel.nss.notification.service.NotificationObserver`).
* **Builder Pattern:** DTO a Entity napříč systémem pro bezpečné a čisté generování objektů (např. `cz.cvut.fel.nss.order.dto.OrderDto` s pomocí Lomboku `@Builder`).
* **Singleton:** Základní chování Spring kontextu (např. bean pro Kafka producenty, Service vrstvy).

### 🛡️ Zabezpečení (Security)
* **Zvolené řešení:** Bezstavová autentizace a autorizace přes **JWT tokeny**.
* **Kde v kódu:** Ve službách Order i Product jsou vlastní `JwtAuthenticationFilter` filtry a konfigurační třídy `SecurityConfig` nacházející se v package `cz.cvut.fel.nss.order.security`, resp. `cz.cvut.fel.nss.product.security`.

### 🔍 Elasticsearch
* **Využití:** Rychlé a výkonné fulltextové vyhledávání v katalogu produktů.
* **Kde v kódu:** `ProductSearchClient` a `ProductSearchRepository` ve službě `product-service`.
* **Endpointy pro ověření:** `GET /api/products/search` a `GET /api/products/search/category/{category}`.

### 🕵️ Interceptory (Spring MVC HandlerInterceptor)
* **Využití:** Logování detailů každého příchozího HTTP requestu.
* **Kde v kódu:** Třídy `LoggingInterceptor` nacházející se lokálně u služeb (package `cz.cvut.fel.nss.order.interceptor` a `cz.cvut.fel.nss.product.interceptor`) zaregistrované přes rozhraní `WebMvcConfigurer`.

---

## 🌿 Práce s verzovacím systémem Git (Gitlab Workflow)
* Projekt je vyvíjen třemi členy týmu a dbá se na spravedlivou dělbu commitů u každého člena.
* Vývojové praktiky:
  * Práce probíhá odděleně ve **feature větvích** (např. `feature/jwt-auth`).
  * Slučování kódu je prováděno skrze **Merge Requesty (MR)** s code review.
  * Odevzdané verze jsou udržovány ve stabilních **release větvích** a verzovány pomocí Git tagů.

---

## ⚠️ Error Handling

Systém používá **Global Exception Handler** napříč službami. Zajišťuje strukturovaný chybový JSON:
```json
{
    "timestamp": "2026-05-18T22:33:02.923",
    "status": 400,
    "error": "Application Error",
    "message": "Insufficient stock for product: Laptop (Available: 5, Requested: 10)"
}
```

## 👥 Authors
* **Erik Vaněk** - `order-service`
* **Adam Malý** - `product-service`
* **Petr Houska** - `notification-service`