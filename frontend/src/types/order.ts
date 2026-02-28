export interface Order {
  id: number;
  latitude: number;
  longitude: number;
  subtotal: number;
  state_rate: number;
  county_rate: number;
  city_rate: number;
  special_rate: number;
  composite_tax_rate: number;
  tax_amount: number;
  total_amount: number;
  timestamp: string;
}