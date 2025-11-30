# AI标书智能创作平台 - 开发环境配置指南 - 🔒 IDE配置

### PyCharm配置
```json
{
  "name": "AI标书智能创作平台",
  "type": "Python",
  "python": {
    "version": "3.11",
    "paths": [
      "server"
    ],
    "venvPath": "server/.venv",
    "env": {
      "PYTHONPATH": "server/.venv/bin"
    },
    "environment": {
      "PYTHONPATH": "server/.env.local"
    },
    "django_settings_module": "server.core.config"
  },
  "javascript": {
    "path": "frontend",
    "version": "18.2.0",
    "nodeInterpreter": "/usr/local/bin/node",
    "npm": {
      "executable": "/usr/local/bin/npm",
      "run_script": true,
      "install": true
    }
  }
}
```

### VS Code Python扩展配置
```json
{
  "python.defaultInterpreterPath": "./server/.venv/bin/python",
  "python.analysis.typeChecking": "basic",
  "python.analysis.autoImportCompletions": true,
  "python.analysis.autoSearchPaths": true,
  "python.analysis.typeChecking.mode": "off",
  "python.linting.pylintEnabled": true,
  "python.linting.pycodestyleEnabled": true,
  "python.linting.enabled": true,
  "python.formatting.provider": "black",
  "python.testing.pytestEnabled": true,
  "python.testing.pytestArgs": [
    "server/tests"
  ]
}
```
