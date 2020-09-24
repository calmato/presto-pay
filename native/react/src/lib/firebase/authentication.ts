export interface AuthUser {
  id: string;
  email: string;
  emailVerified: boolean;
  token: string;
  creationTime?: string;
  lastSignInTime?: string;
}
