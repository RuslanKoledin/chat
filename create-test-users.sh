#!/bin/bash

echo "Creating test users with corporate naming convention (firstname_lastname)..."

# Руслан Коледин
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "r_koledin",
    "password": "password",
    "fullName": "Руслан Коледин",
    "email": "r.koledin@company.com",
    "department": "IT"
  }'

echo ""

# Александр Петров
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "a_petrov",
    "password": "password",
    "fullName": "Александр Петров",
    "email": "a.petrov@company.com",
    "department": "Разработка"
  }'

echo ""

# Мария Иванова
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "m_ivanova",
    "password": "password",
    "fullName": "Мария Иванова",
    "email": "m.ivanova@company.com",
    "department": "HR"
  }'

echo ""

# Иван Сидоров
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "i_sidorov",
    "password": "password",
    "fullName": "Иван Сидоров",
    "email": "i.sidorov@company.com",
    "department": "Продажи"
  }'

echo ""

# Елена Кузнецова
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "e_kuznetsova",
    "password": "password",
    "fullName": "Елена Кузнецова",
    "email": "e.kuznetsova@company.com",
    "department": "Маркетинг"
  }'

echo ""

# Дмитрий Смирнов
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "d_smirnov",
    "password": "password",
    "fullName": "Дмитрий Смирнов",
    "email": "d.smirnov@company.com",
    "department": "IT"
  }'

echo ""

# Анна Волкова
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "a_volkova",
    "password": "password",
    "fullName": "Анна Волкова",
    "email": "a.volkova@company.com",
    "department": "Финансы"
  }'

echo ""

# Сергей Морозов
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "s_morozov",
    "password": "password",
    "fullName": "Сергей Морозов",
    "email": "s.morozov@company.com",
    "department": "Разработка"
  }'

echo ""
echo "Test users created successfully!"
echo ""
echo "You can login with:"
echo "  r_koledin / password"
echo "  a_petrov / password"
echo "  m_ivanova / password"
echo "  i_sidorov / password"
echo "  e_kuznetsova / password"
echo "  d_smirnov / password"
echo "  a_volkova / password"
echo "  s_morozov / password"
