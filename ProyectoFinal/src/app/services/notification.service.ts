import { Injectable, signal } from '@angular/core';
import { ToastMessage } from '../models';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private _toasts = signal<ToastMessage[]>([]);
  readonly toasts = this._toasts.asReadonly();
  private nextId = 0;

  private add(type: ToastMessage['type'], message: string, ms = 3500) {
    const id = ++this.nextId;
    this._toasts.update(t => [...t, { id, type, message }]);
    setTimeout(() => this.dismiss(id), ms);
  }

  success(m: string) { this.add('success', m); }
  error(m: string)   { this.add('error', m); }
  info(m: string)    { this.add('info', m); }
  warning(m: string) { this.add('warning', m); }
  dismiss(id: number){ this._toasts.update(t => t.filter(x => x.id !== id)); }
}
