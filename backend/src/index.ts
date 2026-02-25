import express from 'express';
import type { Request, Response } from 'express';
import cors from 'cors';

const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.json());

// Тестовый эндпоинт для списка заказов (согласно ТЗ)
app.get('/api/orders', (req: Request, res: Response) => {
    const mockOrders = [
        {
            id: 1,
            latitude: 40.7128,
            longitude: -74.0060,
            subtotal: 100.00,
            composite_tax_rate: 0.08875,
            tax_amount: 8.88,
            total_amount: 108.88,
            timestamp: new Date().toISOString()
        },
        {
            id: 2,
            latitude: 42.8864,
            longitude: -78.8784,
            subtotal: 50.00,
            composite_tax_rate: 0.0875,
            tax_amount: 4.38,
            total_amount: 54.38,
            timestamp: new Date().toISOString()
        }
    ];

    res.json({ success: true, data: mockOrders });
});

app.listen(PORT, () => {
    console.log(`Backend is running on http://localhost:${PORT}`);
});