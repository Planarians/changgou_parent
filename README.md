# SkyTakeout

## 1. Project Introduction

- This e-commerce project is an online shopping mall under the B2C model, supporting users to browse and search for products online. Users can add their favorite items to the shopping cart for ordering, and the platform also supports online payments through Alipay, WeChat, and UnionPay. Additionally, users can participate in flash sales for discounted products.

  Changou Mall adopts a microservices architecture with the Spring Cloud technology stack. Each microservice site is built on Spring Boot, and Spring Cloud Gateway is used to connect the functionalities of various microservices, forming a cohesive system. The microservices gateway (Gateway) also implements filtering and rate-limiting strategies to protect and authenticate the microservices. The project utilizes Spring Security OAuth 2.0 to achieve single sign-on and user authorization between microservices.

  To address distributed transactions between microservices, the project employs the popular Seata framework. Elasticsearch is used for real-time indexing of a large number of products. MySQL is chosen for data storage, coupled with Canal for data synchronization, and Redis is utilized for data caching. RabbitMQ facilitates asynchronous communication between various microservices.

  For handling the outermost layer's high concurrency, OpenResty-integrated Nginx is employed. Keepalived and Nginx are used together to address Nginx single-point-of-failure issues.

Frontend Web Pages![1559111851979](file:///Users/xiaoshitou/Documents/pj2/ccg/%E8%AE%B2%E4%B9%89-HTML/day01/image/1559111851979.png)



Backend Administration Page![img](file:///Users/xiaoshitou/Documents/pj2/ccg/%E8%AE%B2%E4%B9%89-HTML/day01/image/Snipaste_2019-09-05_14-35-42.png)

Shopping Cart Page

![20201130213020179](/Users/xiaoshitou/Downloads/20201130213020179.png)

## Technology Stack

Frontend:

- **Node.js**: The frontend server responsible for launching Vue-related content.

- **Vue.js**: The frontend utilizes technologies related to Vue.js.

- **Element UI**: A frontend framework for Vue that provides common components, reducing the need for custom CSS and effects.

- **Nuxt.js**: A frontend framework for Vue used to address SSR (Server-Side Rendering) issues, making it SEO-friendly.

DevOps Technologies:

- **Nacos**: Service center and configuration center.

- **Redis**: Data storage center.

- **Elasticsearch**: Search service center.

- **Docker**: Used for project deployment. The project is deployed to a Linux system and then specific deployment is carried out using Docker.

- **Canal**: [Canal](https://github.com/alibaba/canal) for self-learning.

Distributed Architecture:

- **Spring Boot**: Foundation for project structure.

- **Spring Cloud Alibaba**: Microservices architecture.

- **JWT + RSA**: Authentication center, where JWT is used for token generation, and RSA is used for encryption operations to assist in token generation or verification.

Persistence Technology Stack:

- **MyBatis Plus**: Replaces MyBatis and provides better tools and performance improvements.

- **Spring Data Redis**: Simplifies Java operations on Redis.

- **Spring Data Elasticsearch**: Simplifies Java operations on Elasticsearch.

Database:

- **MySQL**: Free database.

- **MyCat**: Used for database sharding and partitioning.

External Interfaces:

- **Alibaba Cloud SMS (阿里大鱼)**: Used for sending SMS.

- **WeChat Payment Interface**: Used for WeChat payment integration.



![img](file:///Users/xiaoshitou/Documents/pj2/ccg/%E8%AE%B2%E4%B9%89-HTML/day01/image/1-4.png)

![img](file:///Users/xiaoshitou/Documents/pj2/ccg/%E8%AE%B2%E4%B9%89-HTML/day01/image/1-5.png)

![68747470733a2f2f73312e617831782e636f6d2f323032302f30332f31342f3851797135382e706e67](/Users/xiaoshitou/Downloads/68747470733a2f2f73312e617831782e636f6d2f323032302f30332f31342f3851797135382e706e67.png)



Backend and Frontend, Back-end and Front-end

Both the front-end and back-end can be developed using a separation of concerns, resulting in corresponding front-end and back-end components.

**Backend:** 

- Designed for administrator use.

**E-commerce Backend Modules:** 

- Member management, category management, express news management, advertising management, product management, order management, financial management, and report management.

**Frontend:** 

- Utilizes Element UI and Element UI Admin.

**Backend Technologies:** 

- Spring Boot, Spring Cloud, Spring Cloud Alibaba.

**Frontend:** 

- Designed for user interaction. As of now, only a portion of the e-commerce project has been completed, specifically the frontend.

**E-commerce Frontend Modules:** 

- Registration, authentication, unlimited category hierarchy, express news, search, shopping cart, order, payment.

**Frontend Technologies:** 

- Nuxt.js.

**Backend Technologies:** 

- Spring Cloud Alibaba.

