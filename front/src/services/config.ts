import axios from 'axios';
const BASE_URL = 'https://api.moyastudy.com';

const auth = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true,
});

// const BASE_URL = 'http://localhost:8080';

export { auth, BASE_URL };
