import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { NotificationService } from './notification.service';

describe('NotificationService', () => {
  let service: NotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NotificationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start with no toasts', () => {
    expect(service.toasts().length).toBe(0);
  });

  describe('success()', () => {
    it('should add a success toast', () => {
      service.success('Operación exitosa');
      expect(service.toasts().length).toBe(1);
      expect(service.toasts()[0].type).toBe('success');
      expect(service.toasts()[0].message).toBe('Operación exitosa');
    });
  });

  describe('error()', () => {
    it('should add an error toast', () => {
      service.error('Algo salió mal');
      expect(service.toasts()[0].type).toBe('error');
      expect(service.toasts()[0].message).toBe('Algo salió mal');
    });
  });

  describe('info()', () => {
    it('should add an info toast', () => {
      service.info('Información');
      expect(service.toasts()[0].type).toBe('info');
    });
  });

  describe('warning()', () => {
    it('should add a warning toast', () => {
      service.warning('Advertencia');
      expect(service.toasts()[0].type).toBe('warning');
    });
  });

  describe('dismiss()', () => {
    it('should remove a toast by id', () => {
      service.success('Toast 1');
      const id = service.toasts()[0].id;
      service.dismiss(id);
      expect(service.toasts().length).toBe(0);
    });

    it('should only remove the toast with the matching id', () => {
      service.success('Toast 1');
      service.error('Toast 2');
      const id = service.toasts()[0].id;
      service.dismiss(id);
      expect(service.toasts().length).toBe(1);
      expect(service.toasts()[0].type).toBe('error');
    });
  });

  describe('auto-dismiss', () => {
    it('should auto-dismiss toast after 3500ms', fakeAsync(() => {
      service.success('Auto-dismiss me');
      expect(service.toasts().length).toBe(1);
      tick(3500);
      expect(service.toasts().length).toBe(0);
    }));

    it('should not dismiss before 3500ms', fakeAsync(() => {
      service.success('Still visible');
      tick(3000);
      expect(service.toasts().length).toBe(1);
      tick(500);
      expect(service.toasts().length).toBe(0);
    }));
  });

  it('should assign unique ids to each toast', () => {
    service.success('First');
    service.error('Second');
    const ids = service.toasts().map(t => t.id);
    expect(ids[0]).not.toBe(ids[1]);
  });
});
