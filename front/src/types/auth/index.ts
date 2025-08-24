export interface UserProfile {
  nickname: string;
  profileImageUrl: string;
  introduction: string | null;
}

export interface UserApiResponse {
  status: number;
  data: UserProfile;
}
