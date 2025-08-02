import { shoeInventoryApi } from "@api";
import { useQueryClient, useMutation } from "@tanstack/react-query";
import { CreateShoeInventoryRequest } from "@types";

export const useCreateShoeInventory = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (newInventory: CreateShoeInventoryRequest & { shoeId: number }) => {
      const { shoeId, ...inventoryData } = newInventory;
      return shoeInventoryApi.createInventory(inventoryData);
    },
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({ queryKey: ['shoe-models', variables.shoeId] });
      queryClient.invalidateQueries({ queryKey: ['shoes', 'inventory'] });
      queryClient.invalidateQueries({ queryKey: ['shoe-stats'] });
    },
    onError: (error) => {
      console.error('Error creating shoe inventory:', error);
    },
  });
}
