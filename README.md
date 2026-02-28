# BetterMe: Instant Wellness Kits - Tax Calculation Engine

[![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)](https://reactjs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)

Веб-сайт для автоматизованого розрахунку податків для сервісу доставки wellness-наборів квадрокоптерами в межах штату Нью-Йорк. 

---

## Проблематика

**Проблема:** Мобільний застосунок передає лише координати доставки (latitude, longitude) та вартість товару (subtotal), повністю ігноруючи податкове законодавство США. Компанія має розрахувати підсумкову податкову ставку на основі геолокації.

**Наше рішення:** Ми розробили клієнт-серверну систему, яка автоматизує цей процес. Наш бекенд інтегровано з офіційним FCC Area API (Federal Communications Commission). Це дозволяє зі 100% точністю визначати округ (County) за координатами без жорстких обмежень на кількість запитів. Система приймає дані замовлення, зіставляє їх із локальною базою податкових ставок і розраховує composite_tax_rate, tax_amount та total_amount.

---

## Стек

* **Frontend:** React, TypeScript, Vite
* **Backend:** Java, Spring Boot
* **База даних:** MySQL

---

## Інструкція

1. Відкрийте термінал MySQL (CREATE DATABASE wellness_tax;)
2. Відкрийте термінал та перейдіть у директорію бекенду
3. Відкрийте конфігураційний файл src/main/resources/application.properties
4. Вкажіть ваші локальні креденшіали до MySQL
5. Запустіть застосунок за допомогою Maven-обгортки
6. Відкрийте нову вкладку терміналу та перейдіть у директорію фронтенду
7. Встановіть залежності (npm install)
8. Запустіть сервер (npm run dev)
9. Відкрийте браузер і перейдіть за адресою: http://localhost:5173/


