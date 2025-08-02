import { CreateShoeInventoryRequest, ShoeInventory, StandardResponse, UpdateShoeInventoryRequest } from "../types";
import api from "./client";

export const shoeInventoryApi = {

    updateInventory: async (id: number, data: UpdateShoeInventoryRequest) => {
        const response = await api.patch<StandardResponse<ShoeInventory>>(`/inventory/${id}`, data);
        return response.data.data;
    },

    createInventory: async (newInventory: CreateShoeInventoryRequest) => {
        const response = await api.post<StandardResponse<ShoeInventory>>('/inventory', newInventory);
        return response.data.data;
    },
}