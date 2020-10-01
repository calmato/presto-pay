import axios from "axios";

import { Auth } from "~/domain/models";
import * as LocalStorage from "~/lib/local-storage";

const instance = axios.create({
  baseURL: process.env.API_URL,
});

instance.interceptors.request.use(async (config) => {
  const auth: Auth.Model = await LocalStorage.AuthInformation.retrieve();
  if (auth) {
    const token = `Bearer ${auth.token}`;
    config.headers.Authorization = token;
  }

  return config;
});

export default instance;
