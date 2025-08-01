import { CreateShoeModelRequest, ShoeModel, StandardResponse } from "../types";
import api from "./client";


export const shoeModelsApi = {
    createShoeModel: async (newShoeModel: CreateShoeModelRequest) => {
        const response = await api.post<StandardResponse<ShoeModel>>('/products', newShoeModel);
        return response.data.data;
    }
}