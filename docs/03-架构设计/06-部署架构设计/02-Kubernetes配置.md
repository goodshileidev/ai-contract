---
文档类型: 架构文档
需求编号: DOC-2025-11-001
创建日期: 2025-11-29
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-29
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# AI标书智能创作平台 - Kubernetes配置

> **部署平台**: Kubernetes 1.28+
> **编排方式**: Deployment + StatefulSet
> **服务发现**: Kubernetes Service
> **自动扩展**: HorizontalPodAutoscaler (HPA)
> **存储**: PersistentVolume + PersistentVolumeClaim

## 📋 文档导航

本文档是部署架构设计的一部分,其他相关文档:

1. [00-部署架构总览.md](./00-部署架构总览.md) - 部署架构概览和环境规划
2. [01-Docker配置详解.md](./01-Docker配置详解.md) - Docker镜像构建和Docker Compose配置
3. **02-Kubernetes配置.md**(本文档) - Kubernetes完整部署配置
4. [03-部署运维脚本.md](./03-部署运维脚本.md) - 部署脚本、监控日志和安全配置

## ☸️ Kubernetes配置

### 1. Namespace配置

```yaml
# k8s/namespaces/application.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: aibidcomposer
  labels:
    name: aibidcomposer
    environment: production
```

### 2. ConfigMap配置

```yaml
# k8s/configmaps/backend-config.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: backend-config
  namespace: aibidcomposer
data:
  LOG_LEVEL: "INFO"
  WORKERS: "4"
  ENVIRONMENT: "production"
  TZ: "Asia/Shanghai"
  CORS_ORIGINS: "https://www.aibidcomposer.com,https://app.aibidcomposer.com"
```

### 3. Secret配置

```yaml
# k8s/secrets/database-secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: database-secret
  namespace: aibidcomposer
type: Opaque
stringData:
  POSTGRES_PASSWORD: "your_secure_password"
  DATABASE_URL: "postgresql://postgres:your_secure_password@postgres-service:5432/aibidcomposer"
```

```yaml
# k8s/secrets/api-keys-secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: api-keys-secret
  namespace: aibidcomposer
type: Opaque
stringData:
  OPENAI_API_KEY: "sk-your-openai-api-key"
  ANTHROPIC_API_KEY: "your-anthropic-api-key"
  PINECONE_API_KEY: "your-pinecone-api-key"
  SECRET_KEY: "your_secret_key_min_32_characters_long"
```

### 4. PostgreSQL StatefulSet

```yaml
# k8s/statefulsets/postgres-statefulset.yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres
  namespace: aibidcomposer
spec:
  serviceName: postgres-service
  replicas: 3
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:14-alpine
        env:
        - name: POSTGRES_DB
          value: "aibidcomposer"
        - name: POSTGRES_USER
          value: "postgres"
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: database-secret
              key: POSTGRES_PASSWORD
        - name: PGDATA
          value: /var/lib/postgresql/data/pgdata
        ports:
        - containerPort: 5432
          name: postgres
        volumeMounts:
        - name: postgres-storage
          mountPath: /var/lib/postgresql/data
        resources:
          requests:
            memory: "2Gi"
            cpu: "1000m"
          limits:
            memory: "4Gi"
            cpu: "2000m"
        livenessProbe:
          exec:
            command:
            - sh
            - -c
            - pg_isready -U postgres
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          exec:
            command:
            - sh
            - -c
            - pg_isready -U postgres
          initialDelaySeconds: 5
          periodSeconds: 5
  volumeClaimTemplates:
  - metadata:
      name: postgres-storage
    spec:
      accessModes: [ "ReadWriteOnce" ]
      storageClassName: "standard"
      resources:
        requests:
          storage: 100Gi
---
apiVersion: v1
kind: Service
metadata:
  name: postgres-service
  namespace: aibidcomposer
spec:
  ports:
  - port: 5432
    targetPort: 5432
  clusterIP: None
  selector:
    app: postgres
```

### 5. Redis StatefulSet

```yaml
# k8s/statefulsets/redis-statefulset.yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: redis
  namespace: aibidcomposer
spec:
  serviceName: redis-service
  replicas: 6
  selector:
    matchLabels:
      app: redis
  template:
    metadata:
      labels:
        app: redis
    spec:
      containers:
      - name: redis
        image: redis:7-alpine
        command:
        - redis-server
        - --cluster-enabled
        - "yes"
        - --cluster-config-file
        - /data/nodes.conf
        - --cluster-node-timeout
        - "5000"
        - --appendonly
        - "yes"
        - --requirepass
        - $(REDIS_PASSWORD)
        env:
        - name: REDIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: redis-secret
              key: REDIS_PASSWORD
        ports:
        - containerPort: 6379
          name: client
        - containerPort: 16379
          name: gossip
        volumeMounts:
        - name: redis-storage
          mountPath: /data
        resources:
          requests:
            memory: "1Gi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "1000m"
  volumeClaimTemplates:
  - metadata:
      name: redis-storage
    spec:
      accessModes: [ "ReadWriteOnce" ]
      resources:
        requests:
          storage: 10Gi
```

### 6. Java后端Deployment

```yaml
# k8s/deployments/backend-java-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend-java
  namespace: aibidcomposer
spec:
  replicas: 5
  selector:
    matchLabels:
      app: backend-java
  template:
    metadata:
      labels:
        app: backend-java
        version: v1
    spec:
      containers:
      - name: backend-java
        image: aibidcomposer/backend-java:latest
        imagePullPolicy: Always
        ports:
        - containerPort: 8080
          name: http
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            secretKeyRef:
              name: database-secret
              key: JDBC_URL
        - name: SPRING_REDIS_HOST
          value: "redis-service"
        - name: AI_SERVICE_URL
          value: "http://backend-python-service:8001"
        envFrom:
        - configMapRef:
            name: backend-config
        resources:
          requests:
            memory: "1Gi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: backend-java-service
  namespace: aibidcomposer
spec:
  selector:
    app: backend-java
  ports:
  - protocol: TCP
    port: 8080
    targetPort: 8080
  type: ClusterIP
```

### 7. Python AI服务Deployment

```yaml
# k8s/deployments/backend-python-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend-python
  namespace: aibidcomposer
spec:
  replicas: 3
  selector:
    matchLabels:
      app: backend-python
  template:
    metadata:
      labels:
        app: backend-python
        version: v1
    spec:
      containers:
      - name: backend-python
        image: aibidcomposer/backend-python:latest
        imagePullPolicy: Always
        ports:
        - containerPort: 8001
          name: http
        env:
        - name: ELASTICSEARCH_URL
          value: "http://elasticsearch-service:9200"
        - name: REDIS_URL
          valueFrom:
            secretKeyRef:
              name: redis-secret
              key: REDIS_URL
        - name: OPENAI_API_KEY
          valueFrom:
            secretKeyRef:
              name: api-keys-secret
              key: OPENAI_API_KEY
        resources:
          requests:
            memory: "2Gi"
            cpu: "1000m"
          limits:
            memory: "4Gi"
            cpu: "2000m"
        livenessProbe:
          httpGet:
            path: /health
            port: 8001
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /health
            port: 8001
          initialDelaySeconds: 10
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: backend-python-service
  namespace: aibidcomposer
spec:
  selector:
    app: backend-python
  ports:
  - protocol: TCP
    port: 8001
    targetPort: 8001
  type: ClusterIP
```

### 8. Elasticsearch StatefulSet

```yaml
# k8s/statefulsets/elasticsearch-statefulset.yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: elasticsearch
  namespace: aibidcomposer
spec:
  serviceName: elasticsearch-service
  replicas: 3
  selector:
    matchLabels:
      app: elasticsearch
  template:
    metadata:
      labels:
        app: elasticsearch
    spec:
      containers:
      - name: elasticsearch
        image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
        env:
        - name: cluster.name
          value: "aibidcomposer-es-cluster"
        - name: node.name
          valueFrom:
            fieldRef:
              fieldPath: metadata.name
        - name: discovery.seed_hosts
          value: "elasticsearch-0.elasticsearch-service,elasticsearch-1.elasticsearch-service,elasticsearch-2.elasticsearch-service"
        - name: cluster.initial_master_nodes
          value: "elasticsearch-0,elasticsearch-1,elasticsearch-2"
        - name: ES_JAVA_OPTS
          value: "-Xms2g -Xmx2g"
        - name: xpack.security.enabled
          value: "true"
        - name: ELASTIC_PASSWORD
          valueFrom:
            secretKeyRef:
              name: elasticsearch-secret
              key: ELASTIC_PASSWORD
        ports:
        - containerPort: 9200
          name: http
        - containerPort: 9300
          name: transport
        volumeMounts:
        - name: elasticsearch-storage
          mountPath: /usr/share/elasticsearch/data
        resources:
          requests:
            memory: "4Gi"
            cpu: "1000m"
          limits:
            memory: "8Gi"
            cpu: "2000m"
  volumeClaimTemplates:
  - metadata:
      name: elasticsearch-storage
    spec:
      accessModes: [ "ReadWriteOnce" ]
      resources:
        requests:
          storage: 100Gi
---
apiVersion: v1
kind: Service
metadata:
  name: elasticsearch-service
  namespace: aibidcomposer
spec:
  ports:
  - port: 9200
    name: http
  - port: 9300
    name: transport
  clusterIP: None
  selector:
    app: elasticsearch
```

### 9. 旧版后端Deployment (已弃用)

### 10. 前端Deployment

```yaml
# k8s/deployments/frontend-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontend
  namespace: aibidcomposer
spec:
  replicas: 3
  selector:
    matchLabels:
      app: frontend
  template:
    metadata:
      labels:
        app: frontend
    spec:
      containers:
      - name: frontend
        image: aibidcomposer/frontend:latest
        imagePullPolicy: Always
        ports:
        - containerPort: 80
          name: http
        resources:
          requests:
            memory: "128Mi"
            cpu: "100m"
          limits:
            memory: "256Mi"
            cpu: "200m"
        livenessProbe:
          httpGet:
            path: /health
            port: 80
          initialDelaySeconds: 10
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /health
            port: 80
          initialDelaySeconds: 5
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: frontend-service
  namespace: aibidcomposer
spec:
  selector:
    app: frontend
  ports:
  - protocol: TCP
    port: 80
    targetPort: 80
  type: ClusterIP
```

### 11. Ingress配置(多服务路由)

```yaml
# k8s/ingress/ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: aibidcomposer-ingress
  namespace: aibidcomposer
  annotations:
    kubernetes.io/ingress.class: nginx
    cert-manager.io/cluster-issuer: letsencrypt-prod
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/proxy-body-size: "50m"
    nginx.ingress.kubernetes.io/proxy-connect-timeout: "600"
    nginx.ingress.kubernetes.io/proxy-send-timeout: "600"
    nginx.ingress.kubernetes.io/proxy-read-timeout: "600"
spec:
  tls:
  - hosts:
    - www.aibidcomposer.com
    - api.aibidcomposer.com
    secretName: aibidcomposer-tls
  rules:
  - host: www.aibidcomposer.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: frontend-service
            port:
              number: 80
  - host: api.aibidcomposer.com
    http:
      paths:
      # Java业务API路由
      - path: /api/v1/(auth|users|organizations|projects|documents|templates|capabilities|collaboration|approval|export|admin)
        pathType: ImplementationSpecific
        backend:
          service:
            name: backend-java-service
            port:
              number: 8080
      # Python AI API路由
      - path: /api/v1/ai
        pathType: Prefix
        backend:
          service:
            name: backend-python-service
            port:
              number: 8001
      # 默认路由到Java服务
      - path: /
        pathType: Prefix
        backend:
          service:
            name: backend-java-service
            port:
              number: 8080
```

### 12. HPA (水平Pod自动扩缩容)

```yaml
# k8s/hpa/backend-java-hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: backend-java-hpa
  namespace: aibidcomposer
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: backend-java
  minReplicas: 3
  maxReplicas: 20
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
      - type: Percent
        value: 50
        periodSeconds: 60
    scaleUp:
      stabilizationWindowSeconds: 0
      policies:
      - type: Percent
        value: 100
        periodSeconds: 30
      - type: Pods
        value: 2
        periodSeconds: 30
      selectPolicy: Max
---
# k8s/hpa/backend-python-hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: backend-python-hpa
  namespace: aibidcomposer
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: backend-python
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

### 13. PersistentVolume

```yaml
# k8s/persistentvolumes/postgres-pv.yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: postgres-pv
spec:
  capacity:
    storage: 100Gi
  volumeMode: Filesystem
  accessModes:
  - ReadWriteOnce
  persistentVolumeReclaimPolicy: Retain
  storageClassName: standard
  hostPath:
    path: /mnt/data/postgres
```

## 🚀 部署流程

### 生产环境部署

```bash
# 1. 创建命名空间
kubectl create namespace aibidcomposer

# 2. 应用ConfigMaps和Secrets
kubectl apply -f k8s/configmaps/ -n aibidcomposer
kubectl apply -f k8s/secrets/ -n aibidcomposer

# 3. 部署数据服务(StatefulSets)
kubectl apply -f k8s/statefulsets/ -n aibidcomposer

# 4. 等待数据服务就绪
kubectl wait --for=condition=ready pod -l app=postgres -n aibidcomposer --timeout=300s
kubectl wait --for=condition=ready pod -l app=redis -n aibidcomposer --timeout=300s
kubectl wait --for=condition=ready pod -l app=elasticsearch -n aibidcomposer --timeout=300s

# 5. 部署应用服务(Deployments)
kubectl apply -f k8s/deployments/ -n aibidcomposer

# 6. 应用Ingress和HPA
kubectl apply -f k8s/ingress/ -n aibidcomposer
kubectl apply -f k8s/hpa/ -n aibidcomposer

# 7. 验证部署状态
kubectl get pods -n aibidcomposer
kubectl get svc -n aibidcomposer
kubectl get ingress -n aibidcomposer
```

### 滚动更新

```bash
# 更新Java服务
kubectl set image deployment/backend-java \
  backend-java=aibidcomposer/backend-java:v1.2.0 \
  -n aibidcomposer

# 监控更新进度
kubectl rollout status deployment/backend-java -n aibidcomposer

# 回滚(如需要)
kubectl rollout undo deployment/backend-java -n aibidcomposer
```

### 扩容/缩容

```bash
# 手动扩容
kubectl scale deployment backend-java --replicas=10 -n aibidcomposer

# 查看HPA状态
kubectl get hpa -n aibidcomposer

# 查看Pod资源使用
kubectl top pods -n aibidcomposer
```

## 📝 配置说明

### 资源配置

| 服务 | CPU请求 | CPU限制 | 内存请求 | 内存限制 | 副本数 |
|------|--------|--------|---------|---------|--------|
| Java后端 | 500m | 1000m | 1Gi | 2Gi | 5 |
| Python AI | 1000m | 2000m | 2Gi | 4Gi | 3 |
| 前端 | 100m | 200m | 128Mi | 256Mi | 3 |
| PostgreSQL | 1000m | 2000m | 2Gi | 4Gi | 3 |
| Redis | 500m | 1000m | 1Gi | 2Gi | 6 |
| Elasticsearch | 1000m | 2000m | 4Gi | 8Gi | 3 |

### HPA配置

- **Java后端**: 3-20副本,基于CPU 70%和内存80%
- **Python AI**: 2-10副本,基于CPU 70%和内存80%
- **扩容策略**: 立即扩容,每30秒最多扩2个Pod或100%
- **缩容策略**: 稳定5分钟后,每60秒最多缩50%

### 存储配置

- **PostgreSQL**: 100Gi SSD,ReadWriteOnce,3副本
- **Redis**: 10Gi SSD,ReadWriteOnce,6节点集群
- **Elasticsearch**: 100Gi SSD,ReadWriteOnce,3节点集群

## 🔗 下一步

- **部署脚本**: 参见 [03-部署运维脚本.md](./03-部署运维脚本.md)
- **Docker配置**: 参见 [01-Docker配置详解.md](./01-Docker配置详解.md)
- **部署总览**: 参见 [00-部署架构总览.md](./00-部署架构总览.md)

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-29 | 1.0 | claude-sonnet-4-5 (claude-sonnet-4-5-20250929) | 从06-部署架构设计.md拆分创建Kubernetes配置文档 |

---

**文档版本**: v1.0
**创建时间**: 2025年11月29日
**文档状态**: ✅ 已批准
