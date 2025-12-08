# Implementation Plan

## Web Terminal Deployment Feature

- [x] 1. Create deploy directory and menu script
  - [x] 1.1 Create deploy/ directory structure
    - Create deploy/ folder for deployment-related files
    - _Requirements: 1.2, 2.1_
  - [x] 1.2 Create menu.sh script with demo menu functionality
    - Implement three menu options: watch demo, quick experience, exit
    - Add 5-second timeout with auto-select demo mode
    - Add input validation for menu choices
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_
  - [ ]* 1.3 Write property test for menu script output
    - **Property 1: Menu displays all required options**
    - **Validates: Requirements 2.1**
  - [ ]* 1.4 Write property test for menu input validation
    - **Property 2: Menu input validation**
    - **Validates: Requirements 2.1, 2.2, 2.3, 2.4**

- [x] 2. Update Dockerfile for Web terminal deployment
  - [x] 2.1 Update Dockerfile with ttyd and asciinema support
    - Change base image to eclipse-temurin:17-jdk-alpine
    - Add apk install for ttyd, util-linux, asciinema, ncurses
    - Update COPY commands for deploy files
    - Change CMD to use ttyd with menu.sh
    - Update exposed port to 8000
    - Add environment variables for terminal support
    - _Requirements: 1.1, 1.2, 1.3, 1.4_
  - [ ]* 2.2 Write property test for Docker image contents
    - **Property 3: Docker image contains required binaries**
    - **Validates: Requirements 1.4**

- [x] 3. Add demo recording file
  - [x] 3.1 Create placeholder demo.cast file
    - Add a basic asciinema recording file structure
    - Include instructions for recording actual demo
    - _Requirements: 6.1, 6.2_

- [x] 4. Add GitHub Actions workflows
  - [x] 4.1 Create Claude code review workflow
    - Create .github/workflows/claude-code-review.yml
    - Configure PR trigger for opened and synchronized events
    - Set up Claude review with code quality, bugs, security, performance focus
    - _Requirements: 3.1, 3.2, 3.3_
  - [x] 4.2 Create Claude interactive workflow
    - Create .github/workflows/claude.yml
    - Configure triggers for issue_comment, pull_request_review_comment, issues, pull_request_review
    - Set up @claude mention detection
    - Configure response in Simplified Chinese
    - _Requirements: 4.1, 4.2, 4.3_
  - [x] 4.3 Create GitGuardian security scan workflow
    - Create .github/workflows/security-gitguardian.yml
    - Configure push and pull_request triggers
    - Set up GitGuardian scan action
    - _Requirements: 5.1, 5.2_

- [x] 5. Update documentation
  - [x] 5.1 Update README.md with Web terminal deployment instructions
    - Add section for ttyd-based Web deployment
    - Update Docker commands for new port and configuration
    - _Requirements: 1.1, 1.2_

- [x] 6. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.
