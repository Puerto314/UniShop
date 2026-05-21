import { Injectable, signal, computed } from '@angular/core';
import { ProductItem } from '../models';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private _search   = signal('');
  private _category = signal('all');

  readonly searchTerm     = this._search.asReadonly();
  readonly activeCategory = this._category.asReadonly();

  /** Productos reales con links a Amazon */
  readonly PRODUCTS: ProductItem[] = [
    {
      id: 1, name: 'Apple iPhone 15 128GB', price: 3490000, originalPrice: 3890000,
      image: 'https://m.media-amazon.com/images/I/61bK6PMOC3L._AC_SX679_.jpg',
      category: 'smartphones', rating: 4.8, reviews: 12430, stock: 15,
      tags: ['Dynamic Island', 'USB-C', '48MP'], isHot: true,
      amazonUrl: 'https://www.amazon.com/dp/B0CHX1W1XY', asin: 'B0CHX1W1XY'
    },
    {
      id: 2, name: 'Samsung Galaxy S24 Ultra 256GB', price: 4290000, originalPrice: 4790000,
      image: 'https://m.media-amazon.com/images/I/71PvHfU6-pL._AC_SX679_.jpg',
      category: 'smartphones', rating: 4.7, reviews: 8910, stock: 10,
      tags: ['S Pen', '200MP', 'Titanium'], isNew: true,
      amazonUrl: 'https://www.amazon.com/dp/B0CMDRCZBX', asin: 'B0CMDRCZBX'
    },
    {
      id: 3, name: 'Sony WH-1000XM5 Auriculares Inalámbricos', price: 1190000, originalPrice: 1390000,
      image: 'https://m.media-amazon.com/images/I/61+btxzpfDL._AC_SX679_.jpg',
      category: 'audio', rating: 4.8, reviews: 22100, stock: 25,
      tags: ['ANC', '30h batería', 'Hi-Res'], isHot: true,
      amazonUrl: 'https://www.amazon.com/dp/B09XS7JWHH', asin: 'B09XS7JWHH'
    },
    {
      id: 4, name: 'LG 27" Monitor UltraGear QHD 165Hz', price: 1490000, originalPrice: 1790000,
      image: 'https://m.media-amazon.com/images/I/81SPCFzpVsL._AC_SX679_.jpg',
      category: 'monitores', rating: 4.7, reviews: 5670, stock: 8,
      tags: ['165Hz', '1ms', 'IPS', 'G-Sync'],
      amazonUrl: 'https://www.amazon.com/dp/B08GH3XBJT', asin: 'B08GH3XBJT'
    },
    {
      id: 5, name: 'Logitech MX Master 3S Mouse Inalámbrico', price: 490000, originalPrice: 590000,
      image: 'https://m.media-amazon.com/images/I/614NzpiEVlL._AC_SX679_.jpg',
      category: 'accesorios', rating: 4.8, reviews: 9870, stock: 30,
      tags: ['8000 DPI', 'Silencioso', 'Multi-device'], isNew: true,
      amazonUrl: 'https://www.amazon.com/dp/B09HM94VDS', asin: 'B09HM94VDS'
    },
    {
      id: 6, name: 'Samsung 970 EVO Plus 1TB SSD NVMe', price: 390000, originalPrice: 490000,
      image: 'https://m.media-amazon.com/images/I/81tJk8UjM4L._AC_SX679_.jpg',
      category: 'almacenamiento', rating: 4.8, reviews: 18000, stock: 40,
      tags: ['3500MB/s', 'M.2', 'PCIe 3.0'],
      amazonUrl: 'https://www.amazon.com/dp/B07MFZY2F2', asin: 'B07MFZY2F2'
    },
    {
      id: 7, name: 'Corsair K95 RGB Platinum Teclado Mecánico', price: 690000, originalPrice: 890000,
      image: 'https://m.media-amazon.com/images/I/81r8t6JQAXL._AC_SX679_.jpg',
      category: 'gaming', rating: 4.7, reviews: 6540, stock: 12,
      tags: ['Cherry MX', 'RGB', 'Macro G-Keys'], isHot: true,
      amazonUrl: 'https://www.amazon.com/dp/B07T6BXBKK', asin: 'B07T6BXBKK'
    },
    {
      id: 8, name: 'Apple iPad Air M2 256GB WiFi', price: 2890000, originalPrice: 3190000,
      image: 'https://m.media-amazon.com/images/I/71LvJl5VHEL._AC_SX679_.jpg',
      category: 'tablets', rating: 4.9, reviews: 7230, stock: 6,
      tags: ['M2 chip', 'Liquid Retina', 'USB-C'], isNew: true,
      amazonUrl: 'https://www.amazon.com/dp/B0D3J9XDMQ', asin: 'B0D3J9XDMQ'
    },
    {
      id: 9, name: 'Razer BlackShark V2 Pro Auriculares Gaming', price: 790000, originalPrice: 990000,
      image: 'https://m.media-amazon.com/images/I/71B07mHZDvL._AC_SX679_.jpg',
      category: 'audio', rating: 4.6, reviews: 4320, stock: 18,
      tags: ['7.1 Surround', 'THX', 'Inalámbrico'],
      amazonUrl: 'https://www.amazon.com/dp/B08QB39JMR', asin: 'B08QB39JMR'
    },
    {
      id: 10, name: 'NVIDIA GeForce RTX 4070 Super', price: 3290000, originalPrice: 3690000,
      image: 'https://m.media-amazon.com/images/I/71bOuEhqpkL._AC_SX679_.jpg',
      category: 'componentes', rating: 4.8, reviews: 2100, stock: 4,
      tags: ['DLSS 3', '12GB VRAM', 'Ray Tracing'], isHot: true,
      amazonUrl: 'https://www.amazon.com/dp/B0CS3K3C3P', asin: 'B0CS3K3C3P'
    },
    {
      id: 11, name: 'Elgato Stream Deck MK.2', price: 590000, originalPrice: 690000,
      image: 'https://m.media-amazon.com/images/I/61RfAhSFmXL._AC_SX679_.jpg',
      category: 'streaming', rating: 4.8, reviews: 11000, stock: 20,
      tags: ['15 teclas LCD', 'Plugins', 'USB'],
      amazonUrl: 'https://www.amazon.com/dp/B09738CV2G', asin: 'B09738CV2G'
    },
    {
      id: 12, name: 'Secretlab TITAN Evo Silla Gamer', price: 1890000, originalPrice: 2190000,
      image: 'https://m.media-amazon.com/images/I/71TBaxQakwL._AC_SX679_.jpg',
      category: 'muebles', rating: 4.7, reviews: 3240, stock: 5,
      tags: ['Lumbar 4D', 'Cuero sintético', 'Reclina 165°'],
      amazonUrl: 'https://www.amazon.com/dp/B09TPJQ8QN', asin: 'B09TPJQ8QN'
    },
  ];

  readonly categories = ['all','smartphones','tablets','gaming','audio','monitores',
                          'componentes','almacenamiento','streaming','accesorios','muebles'];

  readonly filtered = computed(() => {
    let list = this.PRODUCTS;
    const q   = this._search().toLowerCase();
    const cat = this._category();
    if (cat !== 'all') list = list.filter(p => p.category === cat);
    if (q) list = list.filter(p =>
      p.name.toLowerCase().includes(q) ||
      p.tags?.some(t => t.toLowerCase().includes(q))
    );
    return list;
  });

  setSearch(q: string)    { this._search.set(q); }
  setCategory(c: string)  { this._category.set(c); }
  getById(id: number)     { return this.PRODUCTS.find(p => p.id === id); }
  getFeatured()           { return this.PRODUCTS.filter(p => p.isHot || p.isNew).slice(0, 6); }
  getByCategory(c: string){ return this.PRODUCTS.filter(p => p.category === c); }
}
