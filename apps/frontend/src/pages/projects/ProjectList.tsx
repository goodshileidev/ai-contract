import { useState } from 'react';
import { Button, Tag, Space } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { PageContainer, ProTable } from '@ant-design/pro-components';
import type { ProColumns } from '@ant-design/pro-components';
import { useNavigate } from 'react-router-dom';

/**
 * 项目接口
 */
interface Project {
  id: string;
  name: string;
  code: string;
  status: string;
  priority: string;
  budget_amount?: number;
  submission_deadline?: string;
  created_at: string;
}

/**
 * 项目列表页面
 */
function ProjectListPage() {
  const navigate = useNavigate();

  // Table columns
  const columns: ProColumns<Project>[] = [
    {
      title: '项目名称',
      dataIndex: 'name',
      width: 200,
      fixed: 'left',
      render: (text, record) => (
        <a onClick={() => navigate(`/projects/${record.id}`)}>{text}</a>
      ),
    },
    {
      title: '项目编号',
      dataIndex: 'code',
      width: 150,
      hideInSearch: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 120,
      valueType: 'select',
      valueEnum: {
        draft: { text: '草稿', status: 'Default' },
        in_progress: { text: '进行中', status: 'Processing' },
        review: { text: '审核中', status: 'Warning' },
        submitted: { text: '已提交', status: 'Success' },
        won: { text: '已中标', status: 'Success' },
        lost: { text: '未中标', status: 'Error' },
        archived: { text: '已归档', status: 'Default' },
      },
      render: (_, record) => {
        const statusMap: Record<string, { color: string; text: string }> = {
          draft: { color: 'default', text: '草稿' },
          in_progress: { color: 'processing', text: '进行中' },
          review: { color: 'warning', text: '审核中' },
          submitted: { color: 'success', text: '已提交' },
          won: { color: 'success', text: '已中标' },
          lost: { color: 'error', text: '未中标' },
          archived: { color: 'default', text: '已归档' },
        };
        const status = statusMap[record.status] || { color: 'default', text: record.status };
        return <Tag color={status.color}>{status.text}</Tag>;
      },
    },
    {
      title: '优先级',
      dataIndex: 'priority',
      width: 100,
      valueType: 'select',
      valueEnum: {
        low: { text: '低', status: 'Default' },
        medium: { text: '中', status: 'Processing' },
        high: { text: '高', status: 'Warning' },
        urgent: { text: '紧急', status: 'Error' },
      },
    },
    {
      title: '预算金额',
      dataIndex: 'budget_amount',
      width: 150,
      hideInSearch: true,
      render: (value) => (value ? `¥${Number(value).toLocaleString()}` : '-'),
    },
    {
      title: '提交截止时间',
      dataIndex: 'submission_deadline',
      width: 180,
      valueType: 'dateTime',
      hideInSearch: true,
    },
    {
      title: '创建时间',
      dataIndex: 'created_at',
      width: 180,
      valueType: 'dateTime',
      hideInSearch: true,
    },
    {
      title: '操作',
      width: 180,
      fixed: 'right',
      hideInSearch: true,
      render: (_, record) => (
        <Space size="small">
          <a onClick={() => navigate(`/projects/${record.id}`)}>查看</a>
          <a onClick={() => navigate(`/projects/${record.id}/edit`)}>编辑</a>
          <a onClick={() => handleDelete(record.id)}>删除</a>
        </Space>
      ),
    },
  ];

  // Handle delete
  const handleDelete = (id: string) => {
    // TODO: Implement delete functionality
    console.log('Delete project:', id);
  };

  return (
    <PageContainer
      header={{
        title: '项目管理',
        subTitle: '管理所有投标项目',
      }}
    >
      <ProTable<Project>
        columns={columns}
        rowKey="id"
        request={async (params, sort, filter) => {
          // TODO: Fetch data from API
          console.log('Params:', params, sort, filter);

          // Mock data for now
          return {
            data: [],
            success: true,
            total: 0,
          };
        }}
        search={{
          labelWidth: 'auto',
        }}
        pagination={{
          pageSize: 20,
          showSizeChanger: true,
        }}
        dateFormatter="string"
        headerTitle="项目列表"
        toolBarRender={() => [
          <Button
            key="create"
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => navigate('/projects/new')}
          >
            新建项目
          </Button>,
        ]}
      />
    </PageContainer>
  );
}

export default ProjectListPage;
