'use client';

import { useState, lazy, Suspense } from 'react';

// Lazy load the components
const InventoryPage = lazy(() => import('./inventory/page'));
const AdminUsersPage = lazy(() => import('./users/page'));
const AdminOrdersPage = lazy(() => import('./orders/page'));
const OverviewTab = lazy(() => import('../../components/admin/OverviewTab'));

type TabType = 'overview' | 'orders' | 'users' | 'inventory';

export default function AdminDashboard() {
  const [activeTab, setActiveTab] = useState<TabType>('overview');

  const tabs = [
    { id: 'overview' as TabType, name: 'Overview', icon: '📊' },
    { id: 'orders' as TabType, name: 'Orders', icon: '📦' },
    { id: 'users' as TabType, name: 'Users', icon: '👥' },
    { id: 'inventory' as TabType, name: 'Inventory', icon: '📋' },
  ];

  // Loading component for Suspense fallback
  const LoadingSpinner = () => (
    <div className="flex items-center justify-center py-12">
      <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
      <span className="ml-2 text-gray-600">Loading...</span>
    </div>
  );

  return (
    <div className="space-y-6">
      {/* Tab Navigation */}
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex space-x-8">
          {tabs.map(tab => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`flex items-center space-x-2 border-b-2 px-1 py-2 text-sm font-medium whitespace-nowrap  ${
                activeTab === tab.id
                  ? 'border-indigo-500 text-indigo-600'
                  : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700'
              }`}
            >
              <span>{tab.icon}</span>
              <span>{tab.name}</span>
            </button>
          ))}
        </nav>
      </div>

      {/* Overview Tab */}
      {activeTab === 'overview' && (
        <Suspense fallback={<LoadingSpinner />}>
          <OverviewTab />
        </Suspense>
      )}

      {/* Orders Tab */}
      {activeTab === 'orders' && (
        <Suspense fallback={<LoadingSpinner />}>
          <AdminOrdersPage />
        </Suspense>
      )}

      {/* Users Tab */}
      {activeTab === 'users' && (
        <Suspense fallback={<LoadingSpinner />}>
          <AdminUsersPage />
        </Suspense>
      )}

      {/* Inventory Tab */}
      {activeTab === 'inventory' && (
        <Suspense fallback={<LoadingSpinner />}>
          <InventoryPage />
        </Suspense>
      )}
    </div>
  );
}
