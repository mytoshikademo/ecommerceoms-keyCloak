import axios from "axios";
import { getAccessToken } from "../utils/tokenStorage.js";

// The ONLY place the backend base URL is read from.
// Never hardcode a URL anywhere else in the app — always go through apiClient.
const baseURL = import.meta.env.VITE_API_BASE_URL;

if (!baseURL) {
  // Fails loudly in dev if someone forgets to create a .env file.
  // eslint-disable-next-line no-console
  console.error(
      "VITE_API_BASE_URL is not set. Copy .env.example to .env and set it."
  );
}

const apiClient = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Attach bearer token (if present) to every outgoing request.
apiClient.interceptors.request.use((config) => {
  const token = getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// On 401 (expired/invalid token), broadcast an event that AuthContext
// listens for to clear the session and redirect to login. Kept as a DOM
// event (not a direct import) so this module stays framework-agnostic and
// has no dependency on React/AuthContext.
apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response?.status === 401) {
        window.dispatchEvent(new CustomEvent("auth:unauthorized"));
      }
      return Promise.reject(error);
    }
);

export default apiClient;