'use client';

import Link from 'next/link';
import { useProfile, useLogout } from '../lib/hooks';
import { useUser } from '@/lib/contexts/UserContext';

export default function Home() {
  const { user, isLoading } = useUser();
  const logoutMutation = useLogout();

  const handleLogout = async () => {
    try {
      await logoutMutation.mutateAsync();
      // The page will automatically update due to the profile query invalidation
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <main className="mx-auto max-w-7xl px-4 py-16 sm:px-6 lg:px-8">
        <div className="text-center">
          <h1 className="text-4xl font-extrabold text-gray-900 sm:text-5xl md:text-6xl">
            Welcome to Yoav&apos;s Shoes
          </h1>
          <p className="mx-auto mt-3 max-w-md text-base text-gray-500 sm:text-lg md:mt-5 md:max-w-3xl md:text-xl">
            Discover the perfect pair of shoes for every occasion. From casual
            sneakers to elegant dress shoes, we have it all.
          </p>
          <div className="mx-auto mt-5 max-w-md sm:flex sm:justify-center md:mt-8">
            {user ? (
              // Authenticated user section
              <div className="space-y-4 text-center">
                <p className="text-lg text-gray-700">
                  Welcome back,{' '}
                  <span className="font-semibold text-indigo-600">
                    {user.firstName}
                  </span>
                  !
                </p>
                <div className="flex flex-col gap-3 sm:flex-row sm:justify-center">
                  <Link
                    href="/shoes"
                    className="flex items-center justify-center rounded-md border border-transparent bg-indigo-600 px-8 py-3 text-base font-medium text-white hover:bg-indigo-700 md:px-10 md:py-4 md:text-lg"
                  >
                    Browse Shoes
                  </Link>
                  <button
                    onClick={handleLogout}
                    disabled={logoutMutation.isPending}
                    className="flex items-center justify-center rounded-md border border-gray-300 bg-white px-8 py-3 text-base font-medium text-gray-700 hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50 md:px-10 md:py-4 md:text-lg"
                  >
                    {logoutMutation.isPending ? 'Signing out...' : 'Sign Out'}
                  </button>
                </div>
              </div>
            ) : (
              // Unauthenticated user section
              <>
                <div className="rounded-md shadow">
                  <Link
                    href="/login"
                    className="flex w-full items-center justify-center rounded-md border border-transparent bg-indigo-600 px-8 py-3 text-base font-medium text-white hover:bg-indigo-700 md:px-10 md:py-4 md:text-lg"
                  >
                    Sign In
                  </Link>
                </div>
                <div className="mt-3 rounded-md shadow sm:mt-0 sm:ml-3">
                  <Link
                    href="/register"
                    className="flex w-full items-center justify-center rounded-md border border-transparent bg-white px-8 py-3 text-base font-medium text-indigo-600 hover:bg-gray-50 md:px-10 md:py-4 md:text-lg"
                  >
                    Sign Up
                  </Link>
                </div>
              </>
            )}
          </div>
        </div>

        <div className="mt-20">
          <h2 className="text-center text-3xl font-extrabold text-gray-900">
            Featured Categories
          </h2>
          <div className="mt-10 grid grid-cols-1 gap-10 sm:grid-cols-2 lg:grid-cols-3">
            <div className="overflow-hidden rounded-lg bg-white shadow">
              <div className="px-4 py-5 sm:p-6">
                <h3 className="text-lg font-medium text-gray-900">
                  Athletic Shoes
                </h3>
                <p className="mt-2 text-sm text-gray-500">
                  Performance shoes for all your athletic needs
                </p>
              </div>
            </div>
            <div className="overflow-hidden rounded-lg bg-white shadow">
              <div className="px-4 py-5 sm:p-6">
                <h3 className="text-lg font-medium text-gray-900">
                  Casual Shoes
                </h3>
                <p className="mt-2 text-sm text-gray-500">
                  Comfortable everyday shoes for any occasion
                </p>
              </div>
            </div>
            <div className="overflow-hidden rounded-lg bg-white shadow">
              <div className="px-4 py-5 sm:p-6">
                <h3 className="text-lg font-medium text-gray-900">
                  Formal Shoes
                </h3>
                <p className="mt-2 text-sm text-gray-500">
                  Elegant shoes for professional and formal events
                </p>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
