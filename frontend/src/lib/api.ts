import type { Order } from "../types/order";

function getHeaders() {
  const token = localStorage.getItem('authToken');
  // Додано базову авторизацію, якщо вона є
  return {
    'Authorization': token ? `Basic ${token}` : 'Basic YWRtaW46YWRtaW4xMjM=', // admin:admin123 (Base64)
    'Content-Type': 'application/json'
  };
}

export async function getOrders(): Promise<Order[]> {
  const res = await fetch("http://localhost:8080/api/orders", {
    method: "GET",
    headers: getHeaders()
  });

  if (res.status === 401) {
    localStorage.removeItem('authToken');
    window.location.reload();
  }

  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  const json = await res.json();
  return Array.isArray(json) ? json :[];
}

export async function createOrder(orderData: { latitude: number, longitude: number, subtotal: number }): Promise<any> {
  const res = await fetch("http://localhost:8080/api/orders", {
    method: "POST",
    headers: getHeaders(),
    body: JSON.stringify(orderData)
  });

  if (!res.ok) {
    const err = await res.json();
    throw new Error(err.message || `HTTP ${res.status}`);
  }
  return await res.json();
}

// ДОДАНО: Відправка файлу
export async function uploadCsv(file: File): Promise<any> {
  const formData = new FormData();
  formData.append("file", file);

  const headers: HeadersInit = {
    'Authorization': getHeaders()['Authorization']
  };
  // Увага: Content-Type для FormData браузер встановить сам (з boundary)

  const res = await fetch("http://localhost:8080/api/orders/import", {
    method: "POST",
    headers: headers,
    body: formData
  });

  if (!res.ok) {
    const err = await res.json();
    throw new Error(err.message || `HTTP ${res.status}`);
  }
  return await res.json(); // повертає ImportJobResponse
}

// ДОДАНО: Отримання статусу імпорту
export async function getImportStatus(jobId: string): Promise<any> {
  const res = await fetch(`http://localhost:8080/api/orders/import/${jobId}`, {
    method: "GET",
    headers: getHeaders()
  });

  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return await res.json();
}