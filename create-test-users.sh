#!/bin/bash

echo "Creating test users..."

# Пользователь 1
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ivan.petrov",
    "password": "password123",
    "fullName": "Иван Петров",
    "email": "ivan.petrov@company.com",
    "department": "IT"
  }'

echo ""

# Пользователь 2
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "maria.ivanova",
    "password": "password123",
    "fullName": "Мария Иванова",
    "email": "maria.ivanova@company.com",
    "department": "HR"
  }'

echo ""

# Пользователь 3
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alex.sidorov",
    "password": "password123",
    "fullName": "Александр Сидоров",
    "email": "alex.sidorov@company.com",
    "department": "Sales"
  }'

echo ""
echo "Test users created!"
