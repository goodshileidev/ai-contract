import { Card, Row, Col, Statistic, Button } from 'antd';
import {
  ProjectOutlined,
  FileTextOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  PlusOutlined,
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { PageContainer } from '@ant-design/pro-components';

/**
 * 工作台页面
 */
function DashboardPage() {
  const navigate = useNavigate();

  // TODO: Fetch real statistics from API
  const statistics = {
    totalProjects: 12,
    activeProjects: 5,
    completedProjects: 7,
    totalDocuments: 28,
  };

  return (
    <PageContainer
      header={{
        title: '工作台',
        subTitle: '欢迎使用AI标书智能创作平台',
        extra: [
          <Button
            key="new-project"
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => navigate('/projects/new')}
          >
            新建项目
          </Button>,
        ],
      }}
    >
      {/* Statistics Cards */}
      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card bordered={false}>
            <Statistic
              title="总项目数"
              value={statistics.totalProjects}
              prefix={<ProjectOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card bordered={false}>
            <Statistic
              title="进行中"
              value={statistics.activeProjects}
              prefix={<ClockCircleOutlined />}
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card bordered={false}>
            <Statistic
              title="已完成"
              value={statistics.completedProjects}
              prefix={<CheckCircleOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card bordered={false}>
            <Statistic
              title="标书文档"
              value={statistics.totalDocuments}
              prefix={<FileTextOutlined />}
              valueStyle={{ color: '#722ed1' }}
            />
          </Card>
        </Col>
      </Row>

      {/* Recent Projects */}
      <Card
        title="最近项目"
        extra={<a onClick={() => navigate('/projects')}>查看全部</a>}
        bordered={false}
        style={{ marginBottom: 24 }}
      >
        <p style={{ color: '#8c8c8c', textAlign: 'center', padding: '40px 0' }}>
          暂无最近项目
        </p>
      </Card>

      {/* Quick Actions */}
      <Card title="快捷操作" bordered={false}>
        <Row gutter={16}>
          <Col span={6}>
            <Button
              type="dashed"
              block
              icon={<PlusOutlined />}
              onClick={() => navigate('/projects/new')}
            >
              新建项目
            </Button>
          </Col>
          <Col span={6}>
            <Button
              type="dashed"
              block
              icon={<FileTextOutlined />}
              onClick={() => navigate('/documents/new')}
            >
              创建标书
            </Button>
          </Col>
          <Col span={6}>
            <Button
              type="dashed"
              block
              icon={<ProjectOutlined />}
              onClick={() => navigate('/templates')}
            >
              浏览模板
            </Button>
          </Col>
          <Col span={6}>
            <Button type="dashed" block onClick={() => navigate('/capabilities')}>
              管理能力库
            </Button>
          </Col>
        </Row>
      </Card>
    </PageContainer>
  );
}

export default DashboardPage;
