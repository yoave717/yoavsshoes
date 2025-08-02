'use client';

import { useOrderStats, useUserStats } from '@hooks';

export default function OverviewTab() {
  const { data: orderStats, isLoading: orderStatsLoading } = useOrderStats();
  const { data: userStats, isLoading: userStatsLoading } = useUserStats();

  return (
    <div className="space-y-6">
      {/* Quick Stats */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        {/* Order Stats */}
        <div className="overflow-hidden rounded-lg bg-white shadow">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="flex h-8 w-8 items-center justify-center rounded-md bg-indigo-500">
                  <span className="text-sm text-white">📦</span>
                </div>
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="truncate text-sm font-medium text-gray-500">
                    Total Orders
                  </dt>
                  <dd className="text-lg font-medium text-gray-900">
                    {orderStatsLoading
                      ? '...'
                      : orderStats?.data?.totalOrders || 0}
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div className="overflow-hidden rounded-lg bg-white shadow">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="flex h-8 w-8 items-center justify-center rounded-md bg-yellow-500">
                  <span className="text-sm text-white">⏳</span>
                </div>
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="truncate text-sm font-medium text-gray-500">
                    Pending Orders
                  </dt>
                  <dd className="text-lg font-medium text-gray-900">
                    {orderStatsLoading
                      ? '...'
                      : orderStats?.data?.pendingOrders || 0}
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div className="overflow-hidden rounded-lg bg-white shadow">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="flex h-8 w-8 items-center justify-center rounded-md bg-green-500">
                  <span className="text-sm text-white">👥</span>
                </div>
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="truncate text-sm font-medium text-gray-500">
                    Total Users
                  </dt>
                  <dd className="text-lg font-medium text-gray-900">
                    {userStatsLoading
                      ? '...'
                      : userStats?.data?.totalUsers || 0}
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div className="overflow-hidden rounded-lg bg-white shadow">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="flex h-8 w-8 items-center justify-center rounded-md bg-purple-500">
                  <span className="text-sm text-white">💰</span>
                </div>
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="truncate text-sm font-medium text-gray-500">
                    Total Revenue
                  </dt>
                  <dd className="text-lg font-medium text-gray-900">
                    $
                    {orderStatsLoading
                      ? '...'
                      : (
                          orderStats?.data?.totalRevenue || 0
                        ).toLocaleString()}
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
