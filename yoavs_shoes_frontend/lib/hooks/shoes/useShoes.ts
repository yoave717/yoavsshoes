import {  keepPreviousData, useMutation, useQuery, useQueryClient, UseQueryResult } from '@tanstack/react-query';
import { shoeInventoryApi, shoesApi } from '@api';
import { ShoeFilters, PageResponse, Shoe, ShoeModelInventoryView, ShoeModel, CreateShoeInventoryRequest } from '@/lib/types';

export const useShoes = (filters: ShoeFilters = {}): UseQueryResult<PageResponse<Shoe>, Error> => {
  return useQuery({
    queryKey: ['shoes', filters],
    queryFn: () => shoesApi.getShoes(filters),
    staleTime: 5 * 60 * 1000, // 5 minutes
    refetchOnWindowFocus: false,
  });
};

export const useShoesForInventory = (filters: ShoeFilters = {}) => {
  return useQuery({
    queryKey: ['shoes', 'inventory', filters],
    queryFn: () => shoesApi.getShoesForInventory(filters),
    staleTime: 2 * 60 * 1000, // 2 minutes for inventory data
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData
  });
};

export const useShoe = (id: number): UseQueryResult<Shoe, Error> => {
  return useQuery({
    queryKey: ['shoes', id],
    queryFn: () => shoesApi.getShoe(id),
    enabled: !!id,
    staleTime: 10 * 60 * 1000, // 10 minutes
  });
};

export const useShoeStats = () => {
  return useQuery({
    queryKey: ['shoe-stats'],
    queryFn: () => shoesApi.getShoeStats(),
    staleTime: 5 * 60 * 1000, // 5 minutes
    refetchOnWindowFocus: false,
  });
};

export const useShoeModels = (shoeId: number ): UseQueryResult<ShoeModelInventoryView[], Error> => {
  return useQuery({
    queryKey: ['shoe-models', shoeId],
    queryFn: () => shoesApi.getShoeModels(shoeId),
    enabled: !!shoeId,
    staleTime: 5 * 60 * 1000, // 5 minutes
    refetchOnWindowFocus: false,
    // calculate total stock for each model
    select: (data) => {
      return data.map(model => ({
        ...model,
        totalStock: model.availableSizes.reduce((total, size) => total + size.quantityAvailable, 0)
      }));
    }
  });

 
};

export const useUpdateShoeInventory = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({modelId, size, quantityAvailable, quantityReserved, shoeId }: { modelId: number; size: string; quantityAvailable: number; quantityReserved?: number; shoeId: number }) =>
      shoeInventoryApi.updateInventory(modelId, size, quantityAvailable, quantityReserved),
    onMutate: async (variables) => {
      // Cancel outgoing refetches for the specific shoe
      await queryClient.cancelQueries({ queryKey: ['shoe-models', variables.shoeId] });
      
      // Snapshot previous value
      const previousData = queryClient.getQueryData(['shoe-models', variables.shoeId]);
      
      // Optimistically update cache for the specific shoe
      queryClient.setQueryData(['shoe-models', variables.shoeId], (old: ShoeModel[]) => {
        if (!old) return old;
        return old.map((model) => {
          if (model.id === variables.modelId) {
            return {
              ...model,
              availableSizes: model.availableSizes.map((sizeItem) => 
                sizeItem.size === variables.size 
                  ? { ...sizeItem, quantityAvailable: variables.quantityAvailable }
                  : sizeItem
              )
            };
          }
          return model;
        });
      });
      queryClient.invalidateQueries({ queryKey: ['shoe-stats'] });

      return { previousData, shoeId: variables.shoeId };
    },
    onError: (err, variables, context) => {
      // Rollback on error
      if (context?.previousData && context?.shoeId) {
        queryClient.setQueryData(['shoe-models', context.shoeId], context.previousData);
      }
    },
    onSettled: (data, error, variables) => {
      // Always refetch after error or success
      queryClient.invalidateQueries({ queryKey: ['shoe-models', variables.shoeId] });
      queryClient.invalidateQueries({ queryKey: ['shoes', 'inventory'] });
      queryClient.invalidateQueries({ queryKey: ['shoe-stats'] });
    }
  });
}

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
