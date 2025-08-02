import { useMutation, useQueryClient } from '@tanstack/react-query';
import { shoeModelsApi } from '../../api/shoeModels';
import { ShoeModel } from '@/lib/types';
import { useToast } from '@/components/Toast';

export const useDeleteShoeModel = () => {
    const queryClient = useQueryClient();
    const { showToast } = useToast();

    return useMutation({
        mutationFn: ({shoeId, shoeModelId}: {shoeId: number, shoeModelId: number}) => shoeModelsApi.deleteShoeModel(shoeModelId),
        onSuccess: (data, variables) => {
            queryClient.setQueryData(['shoe-models', variables.shoeId], (old: ShoeModel[]) => {
                return old?.filter(model => model.id !== variables.shoeModelId);
            });
            queryClient.setQueryData(['shoes', 'inventory'], (old: any[]) => {
                return old?.map(shoe => {
                    if (shoe.id === variables.shoeId) {
                        return { ...shoe, modelCount: (shoe.modelCount || 0) - 1 };
                    }
                    return shoe;
                });
            });
            showToast('Shoe model deleted successfully', 'success');

            return queryClient.invalidateQueries({queryKey: ['shoe-stats']});
        },
        onError: (error) => {
            showToast('Failed to delete shoe model', 'error');
        },
    });
};
