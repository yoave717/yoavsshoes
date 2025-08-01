import { ShoeInventory, StandardResponse } from "../types";
import api from "./client";

export const shoeInventoryApi = {

    updateInventory: async (modelId: number, size: string, quantityAvailable: number, quantityReserved?: number) => {
        const response = await api.put<StandardResponse<ShoeInventory>>(`/inventory/model/${modelId}/size/${size}`, { quantityAvailable, quantityReserved });
        return response.data.data;
    }
}