import axios from 'axios';

const auth = axios.create({
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true,
});

const BASE_URL = 'http://localhost:8080';
// const BASE_URL = 'https://api.moyastudy.com';

export { auth, BASE_URL };
