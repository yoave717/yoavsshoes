import { useState } from 'react';
import Image, { ImageProps } from 'next/image';
import { ShoppingCart } from 'lucide-react';

interface SafeImageProps extends Omit<ImageProps, 'onError'> {
  fallback?: React.ReactNode;
}

export const SafeImage = ({ fallback, ...imageProps }: SafeImageProps) => {
  const [hasError, setHasError] = useState(false);

  if (hasError || !imageProps.src) {
    return <>{fallback || <div className="w-16 h-16 mx-auto mb-2 bg-gray-300/70 rounded-full flex items-center justify-center">
        <ShoppingCart className="w-8 h-8" />
      </div>}</>;
  }

  return (
    <Image
      {...imageProps}
      onError={() => setHasError(true)}
    />
  );
};
