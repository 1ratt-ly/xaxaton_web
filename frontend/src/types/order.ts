export interface Order {
  id: number;
  latitude: number;
  longitude: number;
  subtotal: number;
  total_amount: number;
  tax_amount: number;
  composite_tax_rate: number;
  timestamp?: string;
}