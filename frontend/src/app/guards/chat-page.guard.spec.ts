import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { ChatPageGuard } from './chat-page.guard';

describe('ChatPageGuard', () => {
  let guard: ChatPageGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
    });
    guard = TestBed.inject(ChatPageGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
