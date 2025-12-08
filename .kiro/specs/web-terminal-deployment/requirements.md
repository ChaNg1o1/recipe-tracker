# Requirements Document

## Introduction

本文档定义了将 RecipeTracker 部署仓库（recipetracker-deploy）的 Web 终端部署功能合并到主仓库的需求。目标是让主仓库具备完整的 Web 终端部署能力，包括 ttyd 集成、演示菜单、asciinema 录制播放以及 GitHub Actions 工作流。

## Glossary

- **RecipeTracker**: 基于 Java 的食谱管理系统，提供控制台交互界面
- **ttyd**: 一个将终端程序共享为 Web 应用的工具，通过 WebSocket 实现实时终端交互
- **xterm.js**: 在浏览器中运行的终端模拟器，用于渲染 ttyd 输出
- **asciinema**: 终端录制和播放工具，用于创建终端演示
- **demo.cast**: asciinema 录制的演示文件格式
- **menu.sh**: 演示菜单脚本，提供观看演示和快速体验两种模式
- **GitHub Actions**: GitHub 的 CI/CD 自动化工作流服务
- **Claude Code Review**: 使用 Claude AI 进行代码审查的 GitHub Action

## Requirements

### Requirement 1

**User Story:** As a developer, I want to deploy RecipeTracker as a Web terminal application, so that users can access it through a browser without installing Java locally.

#### Acceptance Criteria

1. WHEN the Docker container starts THEN the system SHALL expose a Web terminal interface on port 8000
2. WHEN a user accesses the Web terminal URL THEN the system SHALL display the demo menu with options for watching demo or experiencing the application
3. WHEN the ttyd process receives user input THEN the system SHALL forward the input to the Java application and return the output in real-time
4. WHEN the container is built THEN the system SHALL include ttyd, asciinema, and required dependencies

### Requirement 2

**User Story:** As a user, I want to choose between watching a pre-recorded demo or experiencing the application directly, so that I can quickly understand the system's capabilities.

#### Acceptance Criteria

1. WHEN the menu displays THEN the system SHALL show three options: watch demo, quick experience, and exit
2. WHEN the user selects "watch demo" THEN the system SHALL play the asciinema recording
3. WHEN the user selects "quick experience" THEN the system SHALL launch the Java application directly
4. WHEN the user selects "exit" THEN the system SHALL terminate the session gracefully
5. WHEN no input is received within 5 seconds THEN the system SHALL automatically select the demo mode

### Requirement 3

**User Story:** As a developer, I want automated code review on pull requests, so that code quality is maintained consistently.

#### Acceptance Criteria

1. WHEN a pull request is opened or synchronized THEN the system SHALL trigger Claude code review
2. WHEN Claude reviews the code THEN the system SHALL provide feedback on code quality, potential bugs, security concerns, and performance considerations
3. WHEN the review is complete THEN the system SHALL post comments on the pull request

### Requirement 4

**User Story:** As a developer, I want to interact with Claude through issue and PR comments, so that I can get AI assistance during development.

#### Acceptance Criteria

1. WHEN a comment containing "@claude" is posted on an issue or PR THEN the system SHALL trigger Claude to respond
2. WHEN an issue is opened with "@claude" in the title or body THEN the system SHALL trigger Claude to assist
3. WHEN Claude responds THEN the system SHALL post the response as a comment in Simplified Chinese

### Requirement 5

**User Story:** As a security-conscious developer, I want automated secret scanning, so that sensitive information is not accidentally committed.

#### Acceptance Criteria

1. WHEN code is pushed or a pull request is created THEN the system SHALL scan for secrets using GitGuardian
2. WHEN secrets are detected THEN the system SHALL report the findings in the workflow results

### Requirement 6

**User Story:** As a developer, I want the demo recording file included in the repository, so that users can watch the application demonstration.

#### Acceptance Criteria

1. WHEN the demo.cast file is present THEN the system SHALL be able to play it using asciinema
2. WHEN the demo plays THEN the system SHALL display the terminal recording with proper timing and colors
