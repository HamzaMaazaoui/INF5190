import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, firstValueFrom } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Message, MessageRequest } from './message.model';

@Injectable({
  providedIn: 'root',
})
export class MessagesService {
  messages = new BehaviorSubject<Message[]>([]);

  constructor(private httpClient: HttpClient) {}

  getMessages() {
    return this.messages.asObservable();
  }

  async fetchMessages() {
    const lastMessageId =
      this.messages.value.length > 0
        ? this.messages.value[this.messages.value.length - 1].id
        : null;

    const isIncrementalFetch = lastMessageId !== null;

    let queryParameters = isIncrementalFetch
      ? new HttpParams().set('fromId', lastMessageId)
      : new HttpParams();

    const messageResponse = await firstValueFrom(
      this.httpClient.get<Message[]>(`${environment.backendUrl}/messages`, {
        params: queryParameters,
      })
    );

    this.messages.next(
      isIncrementalFetch
        ? [...this.messages.value, ...messageResponse]
        : messageResponse
    );
  }

  async postMessage(message: MessageRequest) {
    await firstValueFrom(
      this.httpClient.post<Message>(
        `${environment.backendUrl}/messages`,
        message
      )
    );
  }

  clear() {
    this.messages.next([]);
  }
}
