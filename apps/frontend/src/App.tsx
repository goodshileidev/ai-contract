import { Routes, Route, Navigate } from 'react-router-dom';
import { Suspense, lazy } from 'react';
import { Spin } from 'antd';
import AuthLayout from './layouts/AuthLayout';
import MainLayout from './layouts/MainLayout';
import { useAuthStore } from './stores/auth';

// Lazy load pages
const LoginPage = lazy(() => import('./pages/auth/Login'));
const DashboardPage = lazy(() => import('./pages/Dashboard'));
const ProjectsPage = lazy(() => import('./pages/projects/ProjectList'));
const NotFoundPage = lazy(() => import('./pages/NotFound'));

// Loading component
const PageLoading = () => (
  <div style={{ textAlign: 'center', paddingTop: '200px' }}>
    <Spin size="large" tip="加载中..." />
  </div>
);

// Protected Route component
const ProtectedRoute = ({ children }: { children: React.ReactNode }) => {
  const { isAuthenticated } = useAuthStore();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
};

function App() {
  return (
    <Suspense fallback={<PageLoading />}>
      <Routes>
        {/* Auth routes */}
        <Route path="/login" element={<AuthLayout />}>
          <Route index element={<LoginPage />} />
        </Route>

        {/* Protected routes */}
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <MainLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="dashboard" element={<DashboardPage />} />
          <Route path="projects" element={<ProjectsPage />} />
          {/* More routes will be added */}
        </Route>

        {/* 404 */}
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </Suspense>
  );
}

export default App;
