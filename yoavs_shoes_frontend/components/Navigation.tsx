'use client';

import Link from 'next/link';
import { useState } from 'react';
import { usePathname } from 'next/navigation';
import { useProfile, useLogout } from '@hooks';
import CartIcon from './CartIcon';
import CartDrawer from './CartDrawer';

export default function Navigation() {
  const pathname = usePathname();
  const { data: user } = useProfile();
  const logoutMutation = useLogout();
  const [isCartOpen, setIsCartOpen] = useState(false);

  const handleLogout = async () => {
    try {
      await logoutMutation.mutateAsync();
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  // Navigation items for unauthenticated users
  const publicNavItems = [
    { href: '/', label: 'Home' },
    { href: '/shoes', label: 'Shoes' },
    { href: '/login', label: 'Login' },
    { href: '/register', label: 'Register' },
  ];

  // Navigation items for authenticated users
  const privateNavItems = [
    { href: '/', label: 'Home' },
    { href: '/shoes', label: 'Shoes' },
  ];

  const navItems = user ? privateNavItems : publicNavItems;

  return (
    <nav className="border-b bg-white shadow-sm">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="flex h-16 justify-between">
          <div className="flex items-center">
            <Link href="/" className="text-xl font-bold text-gray-900">
              Yoav&apos;s Shoes
            </Link>
          </div>
          <div className="flex items-center space-x-8">
            {navItems.map(item => (
              <Link
                key={item.href}
                href={item.href}
                className={`rounded-md px-3 py-2 text-sm font-medium transition-colors ${
                  pathname === item.href
                    ? 'bg-indigo-50 text-indigo-600'
                    : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                }`}
              >
                {item.label}
              </Link>
            ))}

            {/* Cart Icon */}
            <CartIcon onClick={() => setIsCartOpen(true)} />

            {user && (
              <div className="ml-4 flex items-center space-x-4 border-l border-gray-200 pl-4">
                {/* Admin Panel Link */}
                {user.isAdmin && (
                  <Link
                    href="/admin"
                    className="flex items-center space-x-2 rounded-md px-3 py-2 text-sm font-medium text-purple-600 transition-colors hover:bg-purple-50 hover:text-purple-700"
                  >
                    <svg
                      className="h-5 w-5"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"
                      />
                    </svg>
                    <span>Admin</span>
                  </Link>
                )}

                <Link
                  href="/profile"
                  className="flex items-center space-x-2 rounded-md px-3 py-2 text-sm font-medium text-gray-600 transition-colors hover:bg-gray-50 hover:text-gray-900"
                >
                  <svg
                    className="h-5 w-5"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                    />
                  </svg>
                  <span>
                    {user.firstName} {user.lastName}
                  </span>
                </Link>
                <button
                  onClick={handleLogout}
                  disabled={logoutMutation.isPending}
                  className="rounded-md px-3 py-2 text-sm font-medium text-gray-600 transition-colors hover:bg-gray-50 hover:text-gray-900 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {logoutMutation.isPending ? 'Signing out...' : 'Sign Out'}
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
      
      {/* Cart Drawer */}
      <CartDrawer isOpen={isCartOpen} onClose={() => setIsCartOpen(false)} />
    </nav>
  );
}
