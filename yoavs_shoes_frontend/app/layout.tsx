import type { Metadata } from 'next';
import { Geist, Geist_Mono } from 'next/font/google';
import './globals.css';
import Navigation from '../components/Navigation';
import QueryProvider from '../lib/query-provider';
import { CartProvider } from '../lib/cart-context';
import { UserProvider } from '@/lib/contexts/UserContext';

const geistSans = Geist({
  variable: '--font-geist-sans',
  subsets: ['latin'],
});

const geistMono = Geist_Mono({
  variable: '--font-geist-mono',
  subsets: ['latin'],
});

export const metadata: Metadata = {
  title: "Yoav's Shoes",
  description: 'Your favorite shoe store',
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased`}
      >
        <QueryProvider>
          <UserProvider>
              <CartProvider>
                <Navigation />
                {children}
              </CartProvider>
            </UserProvider>
          </QueryProvider>
      </body>
    </html>
  );
}
