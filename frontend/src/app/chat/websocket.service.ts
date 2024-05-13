import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class WebsocketService {
  static ATTEMPT_DELAY_IN_MS = 2000;

  private notifications: Subject<'notif'> | null = null;
  private ws: WebSocket | null = null;
  private shouldReconnect: boolean = false;
  private timeout: ReturnType<typeof setTimeout> | null = null;

  constructor() {}

  public connect() {
    this.shouldReconnect = true;
    if (!this.notifications) {
      this.notifications = new Subject<'notif'>();
      this.connectWebSocket();
    }
    return this.notifications.asObservable();
  }

  public disconnect() {
    if (this.timeout) {
      clearTimeout(this.timeout);
    }
    this.timeout = null;
    this.shouldReconnect = false;
    this.notifications?.complete();
    this.notifications = null;
    this.ws?.close();
    this.ws = null;
  }

  private connectWebSocket() {
    this.ws = new WebSocket(`${environment.wsServer}/notifications`);
    this.ws.onopen = () => this.notifications?.next('notif');
    this.ws.onmessage = () => this.notifications?.next('notif');
    this.ws.onclose = (e) => {
      if (this.shouldReconnect) {
        console.error('Websocket close, attempting reconnection in 2 seconds');
        this.timeout = setTimeout(
          () => this.connectWebSocket(),
          WebsocketService.ATTEMPT_DELAY_IN_MS
        );
      }
    };
    this.ws.onerror = (e) => {
      console.error('Error on web socket.');
    };
  }
}
