import type { NextConfig } from 'next';

const nextConfig: NextConfig = {
  /* config options here */
  images: {
    remotePatterns: [
      {
        protocol: 'https',
        hostname: 'static.nike.com',
      },
      {
        protocol: 'https',
        hostname: 'assets.adidas.com',
      },
      {
        protocol: 'https',
        hostname: 'images.puma.com',
      },
      {
        protocol: 'https',
        hostname: 'assets.vans.com',
      },
      {
        protocol: 'https',
        hostname: 'nb.scene7.com',
      },
      {
        protocol: 'https',
        hostname: 'logos-world.net',
      },
      {
        protocol: 'https',
        hostname: 'images.unsplash.com',
      },
    ],
  }
};

export default nextConfig;
