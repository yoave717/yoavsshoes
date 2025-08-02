'use client';

import { useState } from 'react';
import InventoryPage from './inventory/page';
import AdminUsersPage from './users/page';
import AdminOrdersPage from './orders/page';
import OverviewTab from './components/OverviewTab';

type TabType = 'overview' | 'orders' | 'users' | 'inventory';

export default function AdminDashboard() {
  const [activeTab, setActiveTab] = useState<TabType>('overview');

  const tabs = [
    { id: 'overview' as TabType, name: 'Overview', icon: '📊' },
    { id: 'orders' as TabType, name: 'Orders', icon: '📦' },
    { id: 'users' as TabType, name: 'Users', icon: '👥' },
    { id: 'inventory' as TabType, name: 'Inventory', icon: '📋' },
  ];

  return (
    <div className="space-y-6">
      {/* Tab Navigation */}
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex space-x-8">
          {tabs.map(tab => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`flex items-center space-x-2 border-b-2 px-1 py-2 text-sm font-medium whitespace-nowrap ${
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
      {activeTab === 'overview' && <OverviewTab />}

      {/* Orders Tab */}
      {activeTab === 'orders' && <AdminOrdersPage />}

      {/* Users Tab */}
      {activeTab === 'users' && <AdminUsersPage />}

      {/* Inventory Tab */}
      {activeTab === 'inventory' && <InventoryPage />}
    </div>
  );
}
                   