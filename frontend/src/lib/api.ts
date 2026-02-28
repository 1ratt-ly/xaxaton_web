import type { Order } from "../types/order";


function getHeaders() {
  const token = localStorage.getItem('authToken');
  return {
    'Authorization': token ? `Basic ${token}` : '',
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

export async function createOrder(orderData: { latitude: number, longitude: number, subtotal: number }): Promise<Order> {
  const res = await fetch("http://localhost:8080/api/orders", {
    method: "POST",
    headers: getHeaders(),
    body: JSON.stringify(orderData)
  });

  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return await res.json();
}