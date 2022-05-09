export interface CommonResponse {
  messages: string[];
  error: boolean;
  value: string;
}

export interface FieldResponse {
  website: CommonResponse;
  email: CommonResponse;
}

export interface RegisterResponse {
  field: FieldResponse
}
