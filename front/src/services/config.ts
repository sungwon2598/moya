import axios from 'axios';
// const BASE_URL = 'http://localhost:8080';
const BASE_URL = 'https://api.moyastudy.com';

export const API_KEYS = {
  ADMIN: 'test-admin-key-moya',
  USER: 'test-user-key-moya',
};

const auth = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
    ...(import.meta.env.DEV && {
      'X-Test-API-Key': API_KEYS.ADMIN,
    }),
  },
});

export { auth, BASE_URL };
