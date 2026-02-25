import { useEffect, useState } from 'react';

// Описываем тип данных заказа из ТЗ
interface Order {
    id: number;
    latitude: number;
    longitude: number;
    subtotal: number;
    total_amount: number;
    tax_amount: number;
    composite_tax_rate: number;
}

function App() {
    const [orders, setOrders] = useState<Order[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Делаем запрос на наш бекенд
        fetch('/api/orders')
            .then(res => res.json())
            .then(data => {
                setOrders(data.data);
                setLoading(false);
            })
            .catch(err => {
                console.error("Помилка з'єднання з бекендом:", err);
                setLoading(false);
            });
    }, []);

    return (
        <div style={{ padding: '20px', fontFamily: 'sans-serif' }}>
            <h1>BetterMe: Instant Wellness Kits 🚁</h1>
            <h2>Orders List (Test Connection)</h2>

            {loading ? (
                <p>Loading data from backend...</p>
            ) : (
                <table border={1} cellPadding={10} style={{ borderCollapse: 'collapse', width: '100%' }}>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Location (Lat, Lon)</th>
                        <th>Subtotal</th>
                        <th>Tax Rate</th>
                        <th>Tax Amount</th>
                        <th>Total Amount</th>
                    </tr>
                    </thead>
                    <tbody>
                    {orders.map(order => (
                        <tr key={order.id}>
                            <td>{order.id}</td>
                            <td>{order.latitude}, {order.longitude}</td>
                            <td>${order.subtotal}</td>
                            <td>{order.composite_tax_rate * 100}%</td>
                            <td>${order.tax_amount}</td>
                            <td><strong>${order.total_amount}</strong></td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default App;