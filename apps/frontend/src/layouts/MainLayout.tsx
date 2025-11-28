import { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { ProLayout } from '@ant-design/pro-components';
import {
  DashboardOutlined,
  ProjectOutlined,
  FileTextOutlined,
  AppstoreOutlined,
  TeamOutlined,
  SettingOutlined,
  LogoutOutlined,
  UserOutlined,
} from '@ant-design/icons';
import { Dropdown, Avatar, message } from 'antd';
import type { MenuDataItem } from '@ant-design/pro-components';
import { useAuthStore } from '@/stores/auth';
import { authService } from '@/services/auth.service';

/**
 * 主布局 - 使用Ant Design Pro Layout
 */
function MainLayout() {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuthStore();
  const [pathname, setPathname] = useState(location.pathname);

  // 菜单配置
  const menuData: MenuDataItem[] = [
    {
      path: '/dashboard',
      name: '工作台',
      icon: <DashboardOutlined />,
    },
    {
      path: '/projects',
      name: '项目管理',
      icon: <ProjectOutlined />,
    },
    {
      path: '/documents',
      name: '标书文档',
      icon: <FileTextOutlined />,
    },
    {
      path: '/templates',
      name: '模板库',
      icon: <AppstoreOutlined />,
    },
    {
      path: '/capabilities',
      name: '企业能力库',
      icon: <TeamOutlined />,
      children: [
        {
          path: '/capabilities/products',
          name: '产品服务',
        },
        {
          path: '/capabilities/cases',
          name: '项目案例',
        },
        {
          path: '/capabilities/personnel',
          name: '人员资质',
        },
        {
          path: '/capabilities/certifications',
          name: '资质证书',
        },
      ],
    },
    {
      path: '/settings',
      name: '系统设置',
      icon: <SettingOutlined />,
    },
  ];

  // 处理登出
  const handleLogout = async () => {
    try {
      await authService.logout();
      logout();
      message.success('已成功登出');
      navigate('/login');
    } catch (error) {
      console.error('Logout failed:', error);
      // Even if API fails, clear local state
      logout();
      navigate('/login');
    }
  };

  // 用户菜单
  const userMenuItems = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人中心',
      onClick: () => navigate('/profile'),
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '账户设置',
      onClick: () => navigate('/settings/account'),
    },
    {
      type: 'divider' as const,
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: handleLogout,
    },
  ];

  return (
    <ProLayout
      title="AIBidComposer"
      logo={null}
      layout="mix"
      contentWidth="Fluid"
      fixedHeader
      fixSiderbar
      route={{
        path: '/',
        routes: menuData,
      }}
      location={{
        pathname,
      }}
      menuItemRender={(item, dom) => (
        <div
          onClick={() => {
            setPathname(item.path || '/');
            navigate(item.path || '/');
          }}
        >
          {dom}
        </div>
      )}
      avatarProps={{
        src: user?.avatar,
        title: user?.fullName || user?.username,
        size: 'small',
        render: (_, dom) => (
          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
            {dom}
          </Dropdown>
        ),
      }}
      actionsRender={() => [
        <Avatar key="avatar" size="small" icon={<UserOutlined />} src={user?.avatar} />,
      ]}
      headerTitleRender={(logo, title) => (
        <div style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
          {logo}
          <h1
            style={{
              margin: '0 0 0 12px',
              fontSize: '18px',
              fontWeight: 600,
            }}
          >
            AI标书智能创作平台
          </h1>
        </div>
      )}
      footerRender={() => (
        <div style={{ textAlign: 'center', paddingBlock: 16 }}>
          © 2025 AIBidComposer Team. All rights reserved.
        </div>
      )}
    >
      <div style={{ minHeight: 'calc(100vh - 48px)' }}>
        <Outlet />
      </div>
    </ProLayout>
  );
}

export default MainLayout;
