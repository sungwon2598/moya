export interface ValidationError {
    field: 'nickname' | 'password' | 'terms' | 'general';
    message: string;
}