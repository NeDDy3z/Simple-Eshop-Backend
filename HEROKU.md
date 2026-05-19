# Heroku Deployment Guide

This project has been reconfigured to run on Heroku using Docker. Each microservice will run as a separate Heroku App, and an Nginx Gateway will act as the single entry point.

## 1. Prerequisites
- Heroku CLI installed and logged in (`heroku login`)
- Docker installed and running
- Heroku Container Registry plugin (`heroku container:login`)

## 2. Create Heroku Apps
Create an app for each service and set the stack to `container`:
```bash
heroku create eshop-product-service
heroku create eshop-order-service
heroku create eshop-notification-service
heroku create eshop-api-gateway

# Set each app to container stack
heroku stack:set container -a eshop-product-service
heroku stack:set container -a eshop-order-service
heroku stack:set container -a eshop-notification-service
heroku stack:set container -a eshop-api-gateway
```

## 3. Provision Add-ons
Each service requires specific add-ons.

### Postgres (for Product and Order)
```bash
heroku addons:create heroku-postgresql:essential-0 -a eshop-product-service
heroku addons:create heroku-postgresql:essential-0 -a eshop-order-service
```

### Kafka (Shared)
Provision Kafka on one app (e.g., `eshop-order-service`). I recommend using `kafkacluster:test`, which is currently free in beta.
```bash
heroku addons:create kafkacluster:test -a eshop-order-service
```
After provisioning, get the connection details:
```bash
heroku config -a eshop-order-service
```
You will see `KAFKACLUSTER_BROKERS`, `KAFKACLUSTER_USERNAME`, etc. Set these on **all apps** (Product, Order, Notification):
```bash
# Example for one app (repeat for all)
heroku config:set SPRING_KAFKA_BOOTSTRAP_SERVERS=your_brokers_here -a your-app-name
heroku config:set SPRING_KAFKA_SECURITY_PROTOCOL=SASL_SSL -a your-app-name
heroku config:set SPRING_KAFKA_SASL_MECHANISM=SCRAM-SHA-512 -a your-app-name
heroku config:set SPRING_KAFKA_SASL_JAAS_CONFIG='org.apache.kafka.common.security.scram.ScramLoginModule required username="your_user" password="your_password";' -a your-app-name
heroku config:set KAFKACLUSTER_PREFIX=your_prefix_here -a your-app-name
```

### Elasticsearch (for Product)
*Note: Due to version compatibility issues, Elasticsearch is currently disabled in the code.*
```bash
heroku addons:create searchbox:starter -a eshop-product-service
```

## 4. Environment Variables
Set the following variables on the **eshop-api-gateway** app:
```bash
heroku config:set PRODUCT_SERVICE_URL=eshop-product-service.herokuapp.com -a eshop-api-gateway
heroku config:set ORDER_SERVICE_URL=eshop-order-service.herokuapp.com -a eshop-api-gateway
```

## 5. Deploy Services
**Note:** I have applied code fixes to handle Elasticsearch version incompatibility and Kafka topic prefixing. Ensure you are on the `main` branch with these changes before pushing.

For each service, navigate to its directory and push to Heroku:

### Product Service
```bash
cd product-service
heroku container:push web -a eshop-product-service
heroku container:release web -a eshop-product-service
```

### Order Service
```bash
cd order-service
heroku container:push web -a eshop-order-service
heroku container:release web -a eshop-order-service
```

### Notification Service
```bash
cd notification-service
heroku container:push web -a eshop-notification-service
heroku container:release web -a eshop-notification-service
```

### API Gateway (Nginx)
```bash
cd nginx
heroku container:push web -a eshop-api-gateway
heroku container:release web -a eshop-api-gateway
```

## 6. Accessing the API
You can now access your API endpoints via the Gateway URL:
- `https://eshop-api-gateway.herokuapp.com/api/products`
- `https://eshop-api-gateway.herokuapp.com/api/orders`
