import { Injectable, signal, computed } from '@angular/core';
import { ProductItem } from '../models';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private _search  = signal('');
  private _category = signal('all');

  readonly searchTerm = this._search.asReadonly();
  readonly activeCategory = this._category.asReadonly();

  readonly PRODUCTS: ProductItem[] = [
    { id:1,  name:'Teclado Mecánico RGB K95',   price:189000, originalPrice:229000, image:'https://placehold.co/400x300/111/00f5ff?text=TECLADO', category:'gaming',     rating:4.9, reviews:312, stock:12, tags:['RGB','Cherry MX'], isHot:true },
    { id:2,  name:'Mouse Inalámbrico Pro X',     price:129000, originalPrice:159000, image:'https://placehold.co/400x300/111/ff006e?text=MOUSE',   category:'gaming',     rating:4.7, reviews:198, stock:25, tags:['25600 DPI'], isNew:true },
    { id:3,  name:'Audífonos Gamer 7.1 Surround',price:249000, originalPrice:299000, image:'https://placehold.co/400x300/111/ffbe00?text=HEADSET', category:'audio',      rating:4.8, reviews:445, stock:8,  tags:['7.1','Noise Cancel'], isHot:true },
    { id:4,  name:'Monitor 27" 165Hz IPS',       price:890000, originalPrice:1050000,image:'https://placehold.co/400x300/111/00ff88?text=MONITOR', category:'monitores',  rating:4.9, reviews:267, stock:5,  tags:['165Hz','1ms','IPS'] },
    { id:5,  name:'SSD NVMe 1TB Gen4',            price:219000, originalPrice:269000, image:'https://placehold.co/400x300/111/ff2d55?text=SSD',     category:'almacenamiento',rating:4.8,reviews:523,stock:30,tags:['7000MB/s','PCIe 4.0'] },
    { id:6,  name:'RAM DDR5 32GB 6000MHz',        price:349000, originalPrice:399000, image:'https://placehold.co/400x300/111/00f5ff?text=RAM',     category:'componentes',rating:4.7, reviews:189, stock:18, tags:['DDR5','XMP 3.0'], isNew:true },
    { id:7,  name:'GPU RTX 5070 Ti 12GB',         price:2890000,originalPrice:3200000,image:'https://placehold.co/400x300/111/ff006e?text=GPU',     category:'componentes',rating:4.9, reviews:87,  stock:3,  tags:['DLSS 4','RT'], isHot:true },
    { id:8,  name:'Silla Gamer Pro Lumbar',       price:689000, originalPrice:799000, image:'https://placehold.co/400x300/111/ffbe00?text=SILLA',   category:'muebles',    rating:4.6, reviews:342, stock:15, tags:['Ergonómica','4D'] },
    { id:9,  name:'Webcam 4K 60fps AF',           price:289000, originalPrice:349000, image:'https://placehold.co/400x300/111/00ff88?text=WEBCAM',  category:'streaming',  rating:4.7, reviews:156, stock:22, tags:['4K','Autofocus'] },
    { id:10, name:'Micrófono USB Condensador',    price:189000, originalPrice:219000, image:'https://placehold.co/400x300/111/ff2d55?text=MIC',     category:'streaming',  rating:4.8, reviews:298, stock:14, isNew:true, tags:['Cardioide','RGB'] },
    { id:11, name:'Pad XL RGB 90x40cm',           price:79000,  originalPrice:99000,  image:'https://placehold.co/400x300/111/00f5ff?text=PAD',     category:'gaming',     rating:4.5, reviews:421, stock:50, tags:['XXL','RGB Border'] },
    { id:12, name:'Capturadora 4K HDMI',          price:319000, originalPrice:389000, image:'https://placehold.co/400x300/111/ff006e?text=CAPTURE', category:'streaming',  rating:4.6, reviews:134, stock:9,  tags:['4K30fps','USB-C'] },
    { id:13, name:'Hub USB-C 12 en 1',            price:139000, originalPrice:169000, image:'https://placehold.co/400x300/111/ffbe00?text=HUB',     category:'accesorios', rating:4.7, reviews:267, stock:35, tags:['HDMI 4K','100W PD'] },
    { id:14, name:'Controlador Xbox Elite S3',    price:549000, originalPrice:599000, image:'https://placehold.co/400x300/111/00ff88?text=CONTROL', category:'gaming',     rating:4.8, reviews:189, stock:7,  isHot:true, tags:['Hall Effect','BT 5.2'] },
    { id:15, name:'Fuente 850W 80+ Gold',         price:389000, originalPrice:449000, image:'https://placehold.co/400x300/111/ff2d55?text=PSU',     category:'componentes',rating:4.9, reviews:312, stock:20, tags:['Modular','80+Gold'] },
    { id:16, name:'Case ATX Mid Tower Mesh',      price:289000, originalPrice:329000, image:'https://placehold.co/400x300/111/00f5ff?text=CASE',    category:'componentes',rating:4.7, reviews:245, stock:11, tags:['Tempered Glass','Airflow'] },
  ];

  readonly categories = ['all','gaming','audio','monitores','componentes','almacenamiento','streaming','accesorios','muebles'];

  readonly filtered = computed(() => {
    let list = this.PRODUCTS;
    const q = this._search().toLowerCase();
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
