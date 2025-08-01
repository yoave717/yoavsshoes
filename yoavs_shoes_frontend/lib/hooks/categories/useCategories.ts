import { categoryApi } from "@/lib/api/categories";
import { useQuery } from "@tanstack/react-query"


export const useCategories = () => {
    return useQuery({
        queryKey: ['categories'],
        queryFn: categoryApi.getCategories
    });
}