import axios from 'axios';

const BASE_URL = 'https://api.nestify.com/api';

export const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

let currentAccessToken: string | null = null;
let currentRefreshToken: string | null = null;

export const setAuthTokens = (accessToken: string, refreshToken: string) => {
  currentAccessToken = accessToken;
  currentRefreshToken = refreshToken;
};

export const clearAuthTokens = () => {
  currentAccessToken = null;
  currentRefreshToken = null;
};

// Request interceptor to attach JWT Access Token
apiClient.interceptors.request.use(
  (config) => {
    if (currentAccessToken) {
      config.headers.Authorization = `Bearer ${currentAccessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor to handle token refresh on 401 Unauthorized
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response?.status === 401 && !originalRequest._retry && currentRefreshToken) {
      originalRequest._retry = true;
      try {
        const refreshResponse = await axios.post(`${BASE_URL}/auth/refresh`, {
          refreshToken: currentRefreshToken,
        });
        const { accessToken, refreshToken } = refreshResponse.data;
        setAuthTokens(accessToken, refreshToken);
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return apiClient(originalRequest);
      } catch (refreshError) {
        clearAuthTokens();
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);
