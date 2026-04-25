# game-gl

游戏图形渲染模块，使用 LWJGL (Lightweight Java Game Library) 实现 OpenGL 渲染。

## 模块概述

本模块负责游戏的图形渲染、用户输入处理和 UI 交互，基于 GLFW 和 OpenGL 提供高性能的图形界面。

## 技术栈

- **LWJGL 3.3.4** - Java 游戏库
- **GLFW** - 窗口和输入管理
- **OpenGL** - 图形渲染
- **STB** - 字体纹理处理

## 构建依赖

```kotlin
implementation("org.lwjgl:lwjgl:3.3.4")
implementation("org.lwjgl:lwjgl-glfw:3.3.4")
implementation("org.lwjgl:lwjgl-opengl:3.3.4")
implementation("org.lwjgl:lwjgl-stb:3.3.4")
implementation("javazoom:jlayer:1.0.1")
```

## 约定

- 游戏窗口大小：1280x720
- 游戏窗口标题：tty7
- 游戏窗口背景颜色：#0D1117
- 主要颜色：#22CC22
- 文本颜色：#FFFFFF
- 灰度色01: #555555
- 灰度色02: #888888
