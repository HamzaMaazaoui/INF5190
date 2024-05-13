import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { NewMessageForm } from './new-message-form.component';

describe('NewMessageForm', () => {
  let component: NewMessageForm;
  let fixture: ComponentFixture<NewMessageForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NewMessageForm],
      imports: [ReactiveFormsModule],
    }).compileComponents();

    fixture = TestBed.createComponent(NewMessageForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
