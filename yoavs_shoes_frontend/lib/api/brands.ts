import { StandardResponse, PageResponse, Brand } from "@types";
import api from "./client";

export const brandsApi = {
    getBrands: async () => {
        const response = await api.get<StandardResponse<PageResponse<Brand>>>('/brands');
        return response.data.data;
    }
}