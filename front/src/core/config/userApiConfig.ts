/* eslint-disable @typescript-eslint/no-explicit-any */
import { auth, BASE_URL } from './apiConfgs';

export const userApiService = {
  getUser: async (): Promise<any> => {
    return auth.get<any>(`${BASE_URL}/api/v1/myPage`);
  },
};
