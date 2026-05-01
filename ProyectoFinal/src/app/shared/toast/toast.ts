import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../services/notification.service';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast.html',
  styleUrl: './toast.css',
  animations: [
    trigger('toastAnim', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateX(120%)' }),
        animate('300ms cubic-bezier(.4,0,.2,1)', style({ opacity: 1, transform: 'translateX(0)' }))
      ]),
      transition(':leave', [
        animate('220ms ease', style({ opacity: 0, transform: 'translateX(120%)' }))
      ])
    ])
  ]
})
export class Toast {
  constructor(public notify: NotificationService) {}

  icon(type: string) {
    return { success: '✅', error: '❌', info: 'ℹ️', warning: '⚠️' }[type] ?? 'ℹ️';
  }
}
