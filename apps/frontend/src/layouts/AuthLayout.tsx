import { Outlet } from 'react-router-dom';
import { Layout } from 'antd';
import './AuthLayout.css';

const { Content } = Layout;

/**
 * 认证布局 - 用于登录、注册等页面
 */
function AuthLayout() {
  return (
    <Layout className="auth-layout">
      <Content className="auth-content">
        <div className="auth-container">
          <div className="auth-header">
            <div className="auth-logo">
              <h1>AIBidComposer</h1>
              <p className="auth-slogan">AI标书智能创作平台</p>
            </div>
          </div>
          <div className="auth-body">
            <Outlet />
          </div>
          <div className="auth-footer">
            <p>&copy; 2025 AIBidComposer Team. All rights reserved.</p>
          </div>
        </div>
      </Content>
    </Layout>
  );
}

export default AuthLayout;
