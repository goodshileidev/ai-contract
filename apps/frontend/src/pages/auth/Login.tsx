// 需求编号: REQ-FRONT-002 - 登录注册页面
// 技术选型: ProForm (替代基础Form，提升企业级表单体验)
import { ProForm, ProFormText, ProFormCheckbox } from '@ant-design/pro-form';
import { message } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useNavigate, Link } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { authService, type LoginRequest } from '@/services/auth.service';
import { useAuthStore } from '@/stores/auth';
import './Login.css';

/**
 * 登录页面
 * 需求编号: REQ-FRONT-002
 * 使用 ProForm 提供企业级表单体验
 */
function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuthStore();

  // 登录mutation
  const loginMutation = useMutation({
    mutationFn: (data: LoginRequest) => authService.login(data),
    onSuccess: (response) => {
      // 保存认证信息到store
      login(response.user, response.access_token, response.refresh_token);

      message.success('登录成功！');

      // 跳转到工作台
      navigate('/dashboard');
    },
    onError: (error: any) => {
      console.error('Login failed:', error);
      message.error(error.message || '登录失败，请检查用户名和密码');
    },
  });

  // 处理登录提交
  const handleSubmit = async (values: LoginRequest & { remember?: boolean }) => {
    const { remember, ...loginData } = values;

    // 处理"记住我"功能
    if (remember) {
      localStorage.setItem('remember_email', loginData.email);
    } else {
      localStorage.removeItem('remember_email');
    }

    await loginMutation.mutateAsync(loginData);
    return true;  // ProForm要求返回true表示提交成功
  };

  // 获取记住的邮箱
  const rememberedEmail = localStorage.getItem('remember_email');

  return (
    <div className="login-page">
      <h2 className="login-title">用户登录</h2>
      <ProForm
        name="login"
        initialValues={{
          email: rememberedEmail || '',
          remember: !!rememberedEmail,
        }}
        onFinish={handleSubmit}
        submitter={{
          searchConfig: {
            submitText: '登录',
          },
          render: (_, dom) => dom[1],  // 只显示提交按钮
          submitButtonProps: {
            loading: loginMutation.isPending,
            size: 'large',
            block: true,
          },
        }}
        autoFocusFirstInput
      >
        <ProFormText
          name="email"
          fieldProps={{
            size: 'large',
            prefix: <UserOutlined />,
            placeholder: '邮箱地址',
          }}
          rules={[
            { required: true, message: '请输入邮箱地址' },
            { type: 'email', message: '请输入有效的邮箱地址' },
          ]}
        />

        <ProFormText.Password
          name="password"
          fieldProps={{
            size: 'large',
            prefix: <LockOutlined />,
            placeholder: '密码',
          }}
          rules={[
            { required: true, message: '请输入密码' },
            { min: 6, message: '密码至少6个字符' },
          ]}
        />

        <div className="login-options">
          <ProFormCheckbox name="remember">记住我</ProFormCheckbox>
          <Link to="/forgot-password" className="login-forgot">
            忘记密码?
          </Link>
        </div>

        <div className="login-register" style={{ marginTop: 16 }}>
          还没有账号? <Link to="/register">立即注册</Link>
        </div>
      </ProForm>
    </div>
  );
}

export default LoginPage;
