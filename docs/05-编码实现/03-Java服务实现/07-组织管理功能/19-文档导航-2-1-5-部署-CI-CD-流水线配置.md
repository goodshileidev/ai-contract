# Java Spring Boot - JAVA-002 Part1 (组织管理: 数据+前端) - 📑 文档导航 - 2.1.5: 部署 - CI/CD 流水线配置

**GitLab CI/CD (.gitlab-ci.yml)**:
```yaml
# .gitlab-ci.yml
# 需求编号: REQ-JAVA-002
# GitLab CI/CD 流水线配置

stages:
  - build
  - test
  - docker
  - deploy

variables:
  MAVEN_OPTS: "-Dmaven.repo.local=.m2/repository"
  DOCKER_REGISTRY: registry.example.com
  JAVA_IMAGE: ${DOCKER_REGISTRY}/aibidcomposer/backend-java
  PYTHON_IMAGE: ${DOCKER_REGISTRY}/aibidcomposer/backend-python

cache:
  paths:
    - .m2/repository
    - node_modules/
    - apps/backend-python/.venv/

# ============ Java 服务构建 ============
build:java:
  stage: build
  image: maven:3.9-eclipse-temurin-17
  script:
    - cd apps/backend-java
    - mvn clean compile -DskipTests
  artifacts:
    paths:
      - apps/backend-java/target/
    expire_in: 1 hour
  only:
    changes:
      - apps/backend-java/**/*

# ============ Java 单元测试 ============
test:java:unit:
  stage: test
  image: maven:3.9-eclipse-temurin-17
  services:
    - postgres:14-alpine
  variables:
    POSTGRES_DB: test_db
    POSTGRES_USER: test_user
    POSTGRES_PASSWORD: test_password
    SPRING_PROFILES_ACTIVE: test
  script:
    - cd apps/backend-java
    - mvn test
  coverage: '/Total.*?([0-9]{1,3})%/'
  artifacts:
    reports:
      junit:
        - apps/backend-java/target/surefire-reports/TEST-*.xml
      coverage_report:
        coverage_format: cobertura
        path: apps/backend-java/target/site/cobertura/coverage.xml
  only:
    changes:
      - apps/backend-java/**/*

# ============ Python 服务测试 ============
test:python:unit:
  stage: test
  image: python:3.11-slim
  services:
    - postgres:14-alpine
    - redis:7-alpine
  variables:
    POSTGRES_DB: test_db
    POSTGRES_USER: test_user
    POSTGRES_PASSWORD: test_password
  before_script:
    - cd apps/backend-python
    - pip install -r requirements/test.txt
  script:
    - pytest --cov=app --cov-report=xml --cov-report=term
  coverage: '/TOTAL.*\s+(\d+%)$/'
  artifacts:
    reports:
      coverage_report:
        coverage_format: cobertura
        path: apps/backend-python/coverage.xml
  only:
    changes:
      - apps/backend-python/**/*

# ============ Docker 镜像构建 - Java ============
docker:build:java:
  stage: docker
  image: docker:24
  services:
    - docker:24-dind
  before_script:
    - docker login -u $CI_REGISTRY_USER -p $CI_REGISTRY_PASSWORD $DOCKER_REGISTRY
  script:
    - cd apps/backend-java
    - docker build -t ${JAVA_IMAGE}:${CI_COMMIT_SHORT_SHA} -t ${JAVA_IMAGE}:latest .
    - docker push ${JAVA_IMAGE}:${CI_COMMIT_SHORT_SHA}
    - docker push ${JAVA_IMAGE}:latest
  only:
    - master
    - develop
  needs:
    - test:java:unit

# ============ Docker 镜像构建 - Python ============
docker:build:python:
  stage: docker
  image: docker:24
  services:
    - docker:24-dind
  before_script:
    - docker login -u $CI_REGISTRY_USER -p $CI_REGISTRY_PASSWORD $DOCKER_REGISTRY
  script:
    - cd apps/backend-python
    - docker build -t ${PYTHON_IMAGE}:${CI_COMMIT_SHORT_SHA} -t ${PYTHON_IMAGE}:latest .
    - docker push ${PYTHON_IMAGE}:${CI_COMMIT_SHORT_SHA}
    - docker push ${PYTHON_IMAGE}:latest
  only:
    - master
    - develop
  needs:
    - test:python:unit

# ============ 部署到测试环境 ============
deploy:staging:
  stage: deploy
  image: bitnami/kubectl:latest
  before_script:
    - kubectl config use-context staging
  script:
    - kubectl set image deployment/backend-java backend-java=${JAVA_IMAGE}:${CI_COMMIT_SHORT_SHA} -n aibidcomposer
    - kubectl set image deployment/backend-python backend-python=${PYTHON_IMAGE}:${CI_COMMIT_SHORT_SHA} -n aibidcomposer
    - kubectl rollout status deployment/backend-java -n aibidcomposer
    - kubectl rollout status deployment/backend-python -n aibidcomposer
  environment:
    name: staging
    url: https://staging.aibidcomposer.com
  only:
    - develop
  when: manual

# ============ 部署到生产环境 ============
deploy:production:
  stage: deploy
  image: bitnami/kubectl:latest
  before_script:
    - kubectl config use-context production
  script:
    - kubectl set image deployment/backend-java backend-java=${JAVA_IMAGE}:${CI_COMMIT_SHORT_SHA} -n aibidcomposer
    - kubectl set image deployment/backend-python backend-python=${PYTHON_IMAGE}:${CI_COMMIT_SHORT_SHA} -n aibidcomposer
    - kubectl rollout status deployment/backend-java -n aibidcomposer --timeout=5m
    - kubectl rollout status deployment/backend-python -n aibidcomposer --timeout=5m
  environment:
    name: production
    url: https://www.aibidcomposer.com
  only:
    - master
  when: manual
```

**GitHub Actions (.github/workflows/ci-cd.yml)**:
```yaml
# .github/workflows/ci-cd.yml
# 需求编号: REQ-JAVA-002
# GitHub Actions CI/CD 流水线

name: CI/CD Pipeline

on:
  push:
    branches: [master, develop]
  pull_request:
    branches: [master, develop]

env:
  REGISTRY: ghcr.io
  JAVA_IMAGE: ghcr.io/${{ github.repository }}/backend-java
  PYTHON_IMAGE: ghcr.io/${{ github.repository }}/backend-python

jobs:
  # ============ Java 构建和测试 ============
  build-test-java:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:14-alpine
        env:
          POSTGRES_DB: test_db
          POSTGRES_USER: test_user
          POSTGRES_PASSWORD: test_password
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
    - uses: actions/checkout@v4

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven

    - name: Build with Maven
      working-directory: apps/backend-java
      run: mvn clean install -DskipTests

    - name: Run Tests
      working-directory: apps/backend-java
      env:
        SPRING_PROFILES_ACTIVE: test
        SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/test_db
        SPRING_DATASOURCE_USERNAME: test_user
        SPRING_DATASOURCE_PASSWORD: test_password
      run: mvn test

    - name: Upload Coverage
      uses: codecov/codecov-action@v3
      with:
        files: apps/backend-java/target/site/jacoco/jacoco.xml

  # ============ Python 测试 ============
  build-test-python:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:14-alpine
        env:
          POSTGRES_DB: test_db
          POSTGRES_USER: test_user
          POSTGRES_PASSWORD: test_password
      redis:
        image: redis:7-alpine

    steps:
    - uses: actions/checkout@v4

    - name: Set up Python
      uses: actions/setup-python@v5
      with:
        python-version: '3.11'
        cache: 'pip'

    - name: Install dependencies
      working-directory: apps/backend-python
      run: |
        pip install -r requirements/test.txt

    - name: Run Tests
      working-directory: apps/backend-python
      run: |
        pytest --cov=app --cov-report=xml

    - name: Upload Coverage
      uses: codecov/codecov-action@v3
      with:
        files: apps/backend-python/coverage.xml

  # ============ Docker 镜像构建 ============
  build-docker:
    needs: [build-test-java, build-test-python]
    runs-on: ubuntu-latest
    if: github.event_name == 'push'

    steps:
    - uses: actions/checkout@v4

    - name: Log in to GitHub Container Registry
      uses: docker/login-action@v3
      with:
        registry: ${{ env.REGISTRY }}
        username: ${{ github.actor }}
        password: ${{ secrets.GITHUB_TOKEN }}

    - name: Build and push Java image
      uses: docker/build-push-action@v5
      with:
        context: apps/backend-java
        push: true
        tags: |
          ${{ env.JAVA_IMAGE }}:${{ github.sha }}
          ${{ env.JAVA_IMAGE }}:latest

    - name: Build and push Python image
      uses: docker/build-push-action@v5
      with:
        context: apps/backend-python
        push: true
        tags: |
          ${{ env.PYTHON_IMAGE }}:${{ github.sha }}
          ${{ env.PYTHON_IMAGE }}:latest

  # ============ 部署到 Kubernetes ============
  deploy:
    needs: build-docker
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/master' && github.event_name == 'push'
    environment: production

    steps:
    - uses: actions/checkout@v4

    - name: Set up kubectl
      uses: azure/setup-kubectl@v3

    - name: Configure kubectl
      run: |
        echo "${{ secrets.KUBE_CONFIG }}" | base64 -d > kubeconfig
        export KUBECONFIG=kubeconfig

    - name: Deploy Java service
      run: |
        kubectl set image deployment/backend-java \
          backend-java=${{ env.JAVA_IMAGE }}:${{ github.sha }} \
          -n aibidcomposer
        kubectl rollout status deployment/backend-java -n aibidcomposer

    - name: Deploy Python service
      run: |
        kubectl set image deployment/backend-python \
          backend-python=${{ env.PYTHON_IMAGE }}:${{ github.sha }} \
          -n aibidcomposer
        kubectl rollout status deployment/backend-python -n aibidcomposer
```
