# 发布说明

## 太长不看

当前仓库的正式发布是通过创建 GitHub Release 触发的，核心流程如下：

1. 代码先合入目标分支。
2. 在 GitHub 上创建 Release，并填写版本标签，例如 `v0.2.10`。
3. CI 读取这个标签，临时写入 `build.gradle` 中的版本号。
4. CI 执行 `./gradlew clean build shadowJar --profile --console=plain`。
5. CI 将生成的 `build/libs/*-all.jar` 上传到当前 GitHub Release。

注意：

- 发布前不需要先手动修改 `build.gradle` 里的 `version`。
- Release tag 才是这次发布的实际版本来源。
- CI 中写入的版本号只在本次工作流运行期间生效，不会自动提交回仓库。

## 当前发布入口

当前发布相关 workflow 定义在 `.github/workflows/build-and-release.yaml`。

它有 4 类触发方式：

1. `push` 到 `master` 或 `dev`
2. `pull_request` 的 `opened`、`synchronize`、`reopened`
3. `workflow_dispatch`
4. GitHub `release` 的 `created`

其中只有第 4 类会执行正式发布附件上传。

## 版本规则

当前流程对版本号的处理规则如下：

- 推荐使用语义化版本号，即 `MAJOR.MINOR.PATCH`。
- 推荐的 Git tag / Release tag 格式为 `v0.2.10` 或 `0.2.10`。
- 如果标签以 `v` 开头，CI 会自动去掉这个前缀。
- 去掉前缀后的版本号会被临时写入 `build.gradle`，并用于本次构建产物命名。

## 发布前检查

正式发布前，建议先确认以下事项：

1. 计划纳入本次发布的代码已经合并到目标分支。
2. 本地或 CI 构建已经通过，至少确认 `shadowJar` 可以正常生成。
3. 本次发布准备使用的版本标签已经确定，例如 `v0.2.10`。
4. 如果你希望仓库源码中的 `build.gradle` 也同步体现这个版本号，需要提前单独提交这项修改；当前 CI 不会替你回写。

## 正式发布流程

### 手动操作

1. 打开 GitHub 仓库的 Releases 页面。
2. 创建一个新的 Release。
3. 填写版本标签，例如 `v0.2.10`。
4. 发布 Release。

### CI 自动执行

Release 创建后，`Build Plugin and Upload Artifacts` workflow 会自动执行以下动作：

1. 拉取仓库代码和完整 Git 历史。
2. 安装 Temurin JDK 21。
3. 配置 Gradle 缓存。
4. 读取 `github.event.release.tag_name`。
5. 去掉可选的 `v` 前缀。
6. 将处理后的版本号临时写入 `build.gradle`。
7. 执行 `./gradlew clean build shadowJar --profile --console=plain`。
8. 上传 Gradle profile 报告。
9. 上传 shaded jar 构建产物。
10. 将 `build/libs/*-all.jar` 上传到当前 GitHub Release。

如果当前 Release 下已经存在同名附件，CI 会先删除旧文件，再上传新文件。

## 构建产物

当前正式发布流程上传的产物只有 shaded jar：

- `build/libs/*-all.jar`

## 非发布场景

以下触发方式会执行构建校验，但不会发布 GitHub Release 附件：

- `push`
- `pull_request`
- `workflow_dispatch`

这些场景主要用于验证项目是否还能正常构建。

如果仓库 Secrets 中存在 `SONAR_TOKEN`，那么 `push` 和 `pull_request` 触发时还会额外执行：

`./gradlew sonarqube --console=plain`
