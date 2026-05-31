# PlayWeather

> 原作者：朱江（Copyright (c) 2021 朱江，见 [LICENSE](LICENSE)）
>
> 本仓库为对原项目的**复现/搬运与构建验证**，用于学习与复现记录；功能与资源版权归原作者及其声明所有。

## 1. 项目简介

PlayWeather 是一个 Android 天气应用工程，采用 Kotlin + Jetpack Compose，并包含：

- 天气信息展示（当前/小时/日等）
- 城市列表与搜索
- 地图相关能力（高德 Map2D）
- AppWidget（周预报桌面小部件、今日小部件、Glance 小部件）

## 2. 开发环境

- Android Studio：建议 Hedgehog / Iguana 及以上
- JDK：17
- compileSdk：34
- minSdk：24
- Gradle：使用项目自带 wrapper

> 版本信息参考：`build.gradle.kts` 与 `PlayWeather/app/build.gradle.kts`。

## 3. 运行与构建

### 3.1 直接运行（Debug）

1. 使用 Android Studio 打开 `PlayWeather/` 目录（包含 `settings.gradle.kts` 的那个目录）。
2. 等待 Gradle Sync 完成。
3. 选择 `app` 运行即可。

### 3.2 命令行构建

在仓库根目录执行：

```bash
./gradlew :app:assembleDebug
```

产物一般位于：

- `PlayWeather/app/build/outputs/apk/debug/`

### 3.3 Release 构建（未签名/签名说明）

本项目 `release` 构建开启了混淆与资源压缩（见 `PlayWeather/app/build.gradle.kts`）。

如需生成可安装的 Release APK：

- 若你已有签名信息（keystore / alias / 密码），请在本地通过 `keystore.properties` 或 Android Studio Signing Config 配置。
- **不要**把 keystore 与密码提交到仓库。

常用命令：

```bash
./gradlew :app:assembleRelease
```

产物一般位于：

- `PlayWeather/app/build/outputs/apk/release/`

## 4. 第三方服务与配置

### 4.1 高德 Key

`AndroidManifest.xml` 中包含：

```xml
<meta-data
    android:name="com.amap.api.v2.apikey"
    android:value="..." />
```

请替换为你自己的高德 Key（若原项目 Key 已失效）。

### 4.2 天气数据源

项目包含与天气/空气质量等接口相关的 network/model 模块；如出现接口鉴权失败或返回异常，请检查：

- 请求使用的 key / token 是否需要自行申请
- 接口 host 是否可访问（网络/地区限制）

## 5. 模块结构

- `PlayWeather/app`：主应用（Compose UI、Widget、页面与 ViewModel）
- `PlayWeather/network`：网络请求封装与 service 定义
- `PlayWeather/model`：数据模型、Room 数据库等
- `PlayWeather/utils`：工具类、资源与通用组件
- `PlayWeather/animate`：天气动画相关资源与实现

## 6. 致谢与声明

- 原作者：朱江
- 本仓库用途：复现/学习记录
- 协议：MIT（见 [LICENSE](LICENSE)）

如你是原作者或权利人且希望本仓库调整署名/内容，请联系我进行处理。
