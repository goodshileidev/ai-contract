# AI标书智能创作平台 - 开发环境配置指南 - 🐳️ CI/CD配置

### GitHub Actions配置
```yaml
# .github/workflows/ci.yml - GitHub Actions配置
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop, 'feature/*', 'hotfix/*']
  pull_request:
    branches: [main, develop]

jobs:
  # 前端测试
  frontend-test:
    runs-on: ubuntu-latest
    container: node:18
    steps:
      - uses: actions/checkout@v3
      - name: Checkout code
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
      - name: Install dependencies
        run: npm ci
      - name: Run linting
        run: npm run lint
      - name: Run type checking
        run: npm run type-check
      - name: Run unit tests
        run: npm run test
      - name: Run E2E tests
        run: npm run test:e2e2
      - name: Generate coverage report
        run: npm run test:cov

  # 后端测试
  backend-test:
    runs-on: ubuntu-latest
    container: python:3.11
    steps:
      - uses: actions/checkout@v3
      - name: Checkout code
      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.11'
      - name: Install dependencies
        run: pip install -r requirements.txt
      - name: Run linting
        run: |
          black --check .
          isort --check .
          flake8 .
      - name: Run type checking
        run: mypy server/
      - name: Run tests
        run: pytest server/tests/ -v --cov=tests/coverage-report
      - name: Run E2E tests
        run: pytest tests/e2e2/ -v

  # 构建和部署
  build-and-deploy:
    needs: [frontend-test, backend-test]
    runs-on: ubuntu-latest
    if: github.event_name == 'push' && github.ref == 'refs/heads/main'
    steps:
      - uses: actions/checkout@v3
      - name: Build frontend
        run: |
          npm run build
      - name: Build backend image
        run: |
          docker build -t aibidcomposer-api:${{github.sha} .
      - name: Push to registry
        run: |
          echo ${{ secrets.DOCKERHUB_TOKEN }} | docker login -u ${{ secrets.DOCKERHUB_USERNAME }} --password-stdin
          docker push aibidcomposer-api:${{github.sha}
      - name: Deploy to staging
        run: |
          docker-compose -f docker-compose.staging.yml up -d

# 生产部署
  deploy-production:
    needs: [build-and-deploy]
    runs-on: ubuntu-latest
    if: github.event_name == 'push' && github.ref == 'refs/heads/main'
    steps:
      - uses: actions/checkout@v3
      - name: Deploy to production
        run: |
          docker-compose -f docker-compose.prod.yml up -d
```

### GitLab CI/CD配置
```yaml
# .gitlab-ci.yml - GitLab CI/CD配置
stages:
  - test
  - build
  - deploy

variables:
  DOCKER_DRIVER: docker
  DOCKER_HOST: tcp://docker:2375
  DOCKER_CERT_PATH: /certs/
  DOCKER_TLS_VERIFY: "true"
  FRONTEND_URL: http://localhost:3000
  BACKEND_URL: http://localhost:8000

cache:
  paths:
    - node_modules/
    - server/.venv/
    - .npm/
    - .cache/pip/
    - .cache/pytest/

test:
  stage: test
  script:
    - echo "Running frontend tests..."
    - cd frontend
    - npm ci
    - npm run test

    - echo "Running backend tests..."
    - cd server
    - python -m pytest tests/ -v --cov=tests/coverage
  coverage: '/coverage'
  artifacts:
    reports:
      reports:
        junit: reports/
        coverage: coverage/
    expire_in: 1 week

build:
  stage: build
  script:
    - echo "Building frontend..."
    - cd frontend
    - npm run build

    - echo "Building backend..."
    - docker build -t $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA_SHORT .
  cache:
    paths:
      - frontend/node_modules/
      - server/.venv/
  artifacts:
    paths:
      - frontend/dist/
      -server/
    expire_in: 1 week

deploy:
  stage: deploy
  script:
    - echo "Deploying to staging..."
    - docker-compose -f docker-compose.staging.yml up -d
    - echo "Deployment completed!"
  environment:
    - FRONTEND_URL: $FRONTEND_URL
    - BACKEND_URL: $BACKEND_URL
  when: manual
```

### Jenkins配置
```yaml
pipeline {
  agent any
  stages:
    - checkout
    - test
    - build
    - deploy

  environment:
    NODE_VERSION: 18
    PYTHON_VERSION: 3.11
    DOCKER_HOST: localhost

  stages:
    - Checkout
    - Test
    - Build
    - Deploy

  Checkout:
    stage: checkout
    steps:
      - checkout scm
      - echo "Checking out $BRANCH branch"

  Test:
    stage: test
    stages:
      - Frontend Tests
      - Backend Tests
      - Integration Tests

    Frontend Tests:
      stage: Frontend Tests
      agent: node
      steps:
        - sh 'npm install'
        - sh 'npm run test'
        - sh 'npm run test:ui'
        - sh 'npm run test:e2e2'

    Backend Tests:
      stage: Backend Tests
      agent: python
      steps:
        - sh 'pip install -r requirements.txt'
        - sh 'python -m pytest tests/ -v --cov=tests/'
        - sh 'python -m pytest tests/e2e2/ -v'

  Build:
    stage: build
    stages:
      - Frontend Build
      - Backend Build
      - Image Build

    Frontend Build:
      stage: Frontend Build
      agent: node
      steps:
        - sh 'npm install'
        - sh 'npm run build'

    Backend Build:
      stage: Backend Build
      agent: python
      steps:
        - sh 'pip install -r requirements.txt'
        - docker build -t aibidcomposer-api:${BUILD_NUMBER} .

    Image Build:
      stage: Image Build
      agent: shell
      steps:
        - echo "Building Docker images..."
        - docker build -t aibidcomposer-${BUILD_NUMBER} .

  Deploy:
    stage: deploy
    stage: deploy
    agent: shell
    script:
      - echo "Deploying application..."
      - docker-compose -f docker-compose.prod.yml up -d
```
