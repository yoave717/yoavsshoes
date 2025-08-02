import { CreateShoeModelRequest, ShoeModel, StandardResponse, UpdateShoeModelRequest } from "../types";
import api from "./client";


export const shoeModelsApi = {
    createShoeModel: async (newShoeModel: CreateShoeModelRequest) => {
        const response = await api.post<StandardResponse<ShoeModel>>('/products', newShoeModel);
        return response.data.data;
    },

    deleteShoeModel: async (id: number) => {
        return await api.delete<void>(`/products/${id}`);
    },

    updateShoeModel: async (id: number, shoeModel: UpdateShoeModelRequest) => {
        const response = await api.patch<StandardResponse<ShoeModel>>(`/products/${id}`, shoeModel);
        return response.data.data;
    }
}