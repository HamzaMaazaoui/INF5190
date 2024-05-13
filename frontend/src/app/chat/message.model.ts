export interface Message {
  id: string;
  username: string;
  text: string;
  timestamp: number;
  imageUrl: string | null;
}

export interface MessageRequest {
  username: string;
  text: string;
  imageData: ChatImageData | null;
}

export interface ChatImageData {
  data: string;
  type: string;
}
