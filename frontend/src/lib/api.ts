import type { Order } from "../types/order";

// кодуємо логін та пароль
const encodedCredentials = btoa('admin:admin123');


const headers = {
  'Authorization': `Basic ${encodedCredentials}`,
  'Content-Type': 'application/json'
};

export async function getOrders(): Promise<Order[]> {

  const res = await fetch("http://localhost:8080/api/orders", {
    method: "GET",
    headers: headers
  });

  if (!res.ok) throw new Error(`HTTP ${res.status}`);

  const json = await res.json();


  return Array.isArray(json) ? json :[];
}

export async function createOrder(orderData: { latitude: number, longitude: number, subtotal: number }): Promise<Order> {
  const res = await fetch("http://localhost:8080/api/orders", {
    method: "POST",
    headers: headers,
    body: JSON.stringify(orderData)
  });

  if (!res.ok) throw new Error(`HTTP ${res.status}`);

  return await res.json();
}