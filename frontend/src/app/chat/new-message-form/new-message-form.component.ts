import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { FileReaderService } from '../file-reader.service';

@Component({
  selector: 'app-new-message-form',
  templateUrl: './new-message-form.component.html',
  styleUrls: ['./new-message-form.component.css'],
})
export class NewMessageForm implements OnInit {
  messageForm = this.fb.group({
    msg: '',
    filename: '',
  });

  file: File | null = null;

  @Output()
  sendMessage = new EventEmitter<{ text: string; file: File | null }>();
  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {}

  onSend() {
    if (this.messageForm.valid && this.messageForm.value.msg) {
      this.sendMessage.emit({
        text: this.messageForm.value.msg,
        file: this.file,
      });

      this.messageForm.reset();
      this.file = null;
    }
  }

  fileChanged(event: any) {
    this.file = event.target.files[0];
  }
}
