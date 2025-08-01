import { StandardResponse, PageResponse, Category } from "../types";
import api from "./client";

export const categoryApi = {
    getCategories: async () => {
        const response = await api.get<StandardResponse<PageResponse<Category>>>('/shoe-categories');
        return response.data.data;
    }
};