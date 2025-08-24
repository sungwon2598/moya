import { auth, BASE_URL } from '../config';
import { UserApiResponse } from '@/types/auth';

export const userService = {
  getUser: async (): Promise<UserApiResponse> => {
    const response = await auth.get<UserApiResponse>(`${BASE_URL}/api/v1/mypage`);
    return response.data;
  },
};
