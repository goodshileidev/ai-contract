# Java Spring Boot - JAVA-002 Part1 (组织管理: 数据+前端) - 📑 文档导航 - 2.1.5: 部署 - Kubernetes 配置（生产环境）

**Namespace**:
```yaml
# k8s/namespaces/aibidcomposer.yaml
# 需求编号: REQ-JAVA-002
apiVersion: v1
kind: Namespace
metadata:
  name: aibidcomposer
  labels:
    name: aibidcomposer
    environment: production
```

**ConfigMap - Java 服务配置**:
```yaml
# k8s/configmaps/backend-java-config.yaml
# 需求编号: REQ-JAVA-002
apiVersion: v1
kind: ConfigMap
metadata:
  name: backend-java-config
  namespace: aibidcomposer
data:
  SPRING_PROFILES_ACTIVE: "prod"
  SPRING_DATASOURCE_URL: "jdbc:postgresql://postgres-service:5432/aibidcomposer"
  SPRING_REDIS_HOST: "redis-service"
  SPRING_RABBITMQ_HOST: "rabbitmq-service"
  MINIO_ENDPOINT: "http://minio-service:9000"
  AI_SERVICE_URL: "http://backend-python-service:8001"
  TZ: "Asia/Shanghai"
  LOG_LEVEL: "INFO"
```

**ConfigMap - Python AI 服务配置**:
```yaml
# k8s/configmaps/backend-python-config.yaml
# 需求编号: REQ-JAVA-002
apiVersion: v1
kind: ConfigMap
metadata:
  name: backend-python-config
  namespace: aibidcomposer
data:
  ELASTICSEARCH_URL: "http://elasticsearch-service:9200"
  JAVA_SERVICE_URL: "http://backend-java-service:8080"
  JWT_ALGORITHM: "HS256"
  TZ: "Asia/Shanghai"
  LOG_LEVEL: "INFO"
  WORKERS: "4"
```

**Secret - 敏感信息**:
```yaml
# k8s/secrets/database-secret.yaml
# 需求编号: REQ-JAVA-002
# 注意: 实际部署时使用 Sealed Secrets 或 HashiCorp Vault
apiVersion: v1
kind: Secret
metadata:
  name: database-secret
  namespace: aibidcomposer
type: Opaque
stringData:
  POSTGRES_PASSWORD: "your_secure_password"
  SPRING_DATASOURCE_USERNAME: "postgres"
  SPRING_DATASOURCE_PASSWORD: "your_secure_password"

---
# k8s/secrets/api-keys-secret.yaml
# 需求编号: REQ-JAVA-002
apiVersion: v1
kind: Secret
metadata:
  name: api-keys-secret
  namespace: aibidcomposer
type: Opaque
stringData:
  OPENAI_API_KEY: "sk-your-openai-api-key"
  ANTHROPIC_API_KEY: "your-anthropic-api-key"
  JWT_SECRET_KEY: "your_secret_key_min_32_characters_long"
  REDIS_PASSWORD: "your_redis_password"
  RABBITMQ_USER: "rabbitmq"
  RABBITMQ_PASSWORD: "your_rabbitmq_password"
  ELASTICSEARCH_PASSWORD: "your_elasticsearch_password"
```

**Deployment - Java Spring Boot 服务**:
```yaml
# k8s/deployments/backend-java-deployment.yaml
# 需求编号: REQ-JAVA-002
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend-java
  namespace: aibidcomposer
  labels:
    app: backend-java
    component: api
spec:
  replicas: 3
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
          protocol: TCP
        env:
        # ConfigMap 引用
        - name: SPRING_PROFILES_ACTIVE
          valueFrom:
            configMapKeyRef:
              name: backend-java-config
              key: SPRING_PROFILES_ACTIVE
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            configMapKeyRef:
              name: backend-java-config
              key: SPRING_DATASOURCE_URL
        - name: SPRING_REDIS_HOST
          valueFrom:
            configMapKeyRef:
              name: backend-java-config
              key: SPRING_REDIS_HOST
        # Secret 引用
        - name: SPRING_DATASOURCE_USERNAME
          valueFrom:
            secretKeyRef:
              name: database-secret
              key: SPRING_DATASOURCE_USERNAME
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: database-secret
              key: SPRING_DATASOURCE_PASSWORD
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: api-keys-secret
              key: JWT_SECRET_KEY
        - name: SPRING_REDIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: api-keys-secret
              key: REDIS_PASSWORD
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
          timeoutSeconds: 5
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
          timeoutSeconds: 3
          failureThreshold: 3
        volumeMounts:
        - name: logs
          mountPath: /app/logs
      volumes:
      - name: logs
        emptyDir: {}
---
apiVersion: v1
kind: Service
metadata:
  name: backend-java-service
  namespace: aibidcomposer
  labels:
    app: backend-java
spec:
  type: ClusterIP
  selector:
    app: backend-java
  ports:
  - protocol: TCP
    port: 8080
    targetPort: 8080
    name: http
```

**Deployment - Python AI 服务**:
```yaml
# k8s/deployments/backend-python-deployment.yaml
# 需求编号: REQ-JAVA-002
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend-python
  namespace: aibidcomposer
  labels:
    app: backend-python
    component: ai
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
          protocol: TCP
        env:
        # ConfigMap 引用
        - name: ELASTICSEARCH_URL
          valueFrom:
            configMapKeyRef:
              name: backend-python-config
              key: ELASTICSEARCH_URL
        - name: JAVA_SERVICE_URL
          valueFrom:
            configMapKeyRef:
              name: backend-python-config
              key: JAVA_SERVICE_URL
        - name: JWT_ALGORITHM
          valueFrom:
            configMapKeyRef:
              name: backend-python-config
              key: JWT_ALGORITHM
        # Secret 引用
        - name: OPENAI_API_KEY
          valueFrom:
            secretKeyRef:
              name: api-keys-secret
              key: OPENAI_API_KEY
        - name: JWT_SECRET_KEY
          valueFrom:
            secretKeyRef:
              name: api-keys-secret
              key: JWT_SECRET_KEY
        - name: REDIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: api-keys-secret
              key: REDIS_PASSWORD
        - name: ELASTICSEARCH_PASSWORD
          valueFrom:
            secretKeyRef:
              name: api-keys-secret
              key: ELASTICSEARCH_PASSWORD
        # 组合配置
        - name: REDIS_URL
          value: "redis://:$(REDIS_PASSWORD)@redis-service:6379/0"
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
          timeoutSeconds: 5
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /health
            port: 8001
          initialDelaySeconds: 15
          periodSeconds: 5
          timeoutSeconds: 3
          failureThreshold: 3
---
apiVersion: v1
kind: Service
metadata:
  name: backend-python-service
  namespace: aibidcomposer
  labels:
    app: backend-python
spec:
  type: ClusterIP
  selector:
    app: backend-python
  ports:
  - protocol: TCP
    port: 8001
    targetPort: 8001
    name: http
```

**HorizontalPodAutoscaler - 自动扩缩容**:
```yaml
# k8s/hpa/backend-java-hpa.yaml
# 需求编号: REQ-JAVA-002
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
# 需求编号: REQ-JAVA-002
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
  maxReplicas: 8
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

**Ingress - 路由配置**:
```yaml
# k8s/ingress/ingress.yaml
# 需求编号: REQ-JAVA-002
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
    - api.aibidcomposer.com
    secretName: aibidcomposer-tls
  rules:
  - host: api.aibidcomposer.com
    http:
      paths:
      # Java 业务 API 路由
      - path: /api/v1/organizations
        pathType: Prefix
        backend:
          service:
            name: backend-java-service
            port:
              number: 8080
      - path: /api/v1/auth
        pathType: Prefix
        backend:
          service:
            name: backend-java-service
            port:
              number: 8080
      # Python AI API 路由
      - path: /api/v1/ai
        pathType: Prefix
        backend:
          service:
            name: backend-python-service
            port:
              number: 8001
      # 默认路由到 Java 服务
      - path: /
        pathType: Prefix
        backend:
          service:
            name: backend-java-service
            port:
              number: 8080
```
