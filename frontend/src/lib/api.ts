import type { Order } from "../types/order";

// Хардкодимо пароль для тестування (щоб не треба було робити форму логіну)
const token = btoa('admin:admin123');

function getHeaders() {
  return {
    'Authorization': `Basic ${token}`,
    'Content-Type': 'application/json'
  };
}

export async function getOrders(): Promise<Order[]> {
  const res = await fetch("http://localhost:8080/api/orders", {
    method: "GET",
    headers: getHeaders()
  });

  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  const json = await res.json();
  return Array.isArray(json) ? json :[];
}

export async function createOrder(orderData: { latitude: number, longitude: number, subtotal: number }): Promise<Order> {
  const res = await fetch("http://localhost:8080/api/orders", {
    method: "POST",
    headers: getHeaders(),
    body: JSON.stringify(orderData)
  });

  if (!res.ok) {
    const errorData = await res.json();
    throw new Error(errorData.message || `HTTP ${res.status}`);
  }
  return await res.json();
}

// НОВЕ: Відправка CSV файлу (FormData)
export async function uploadCsv(file: File) {
  const formData = new FormData();
  formData.append("file", file);

  const res = await fetch("http://localhost:8080/api/orders/import", {
    method: "POST",
    headers: {
      'Authorization': `Basic ${token}`
      // ВАЖЛИВО: Не ставимо Content-Type! Браузер сам встановить multipart/form-data
    },
    body: formData
  });

  if (!res.ok) {
    const errorData = await res.json();
    throw new Error(errorData.message || `HTTP ${res.status}`);
  }
  return await res.json(); // Поверне ImportJobResponse { jobId: "..." }
}

// НОВЕ: Перевірка статусу завантаження CSV
export async function checkJobStatus(jobId: string) {
  const res = await fetch(`http://localhost:8080/api/orders/import/${jobId}`, {
    method: "GET",
    headers: getHeaders()
  });
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return await res.json();
}