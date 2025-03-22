# Contributing to EkoTranslate

Thank you for your interest in contributing to EkoTranslate! This document provides guidelines and instructions for contributing to the project.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Pull Request Process](#pull-request-process)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)
- [Documentation](#documentation)
- [Issue Reporting](#issue-reporting)
- [Feature Requests](#feature-requests)

## Code of Conduct

By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md). Please read it before contributing.

## Getting Started

### Prerequisites

- Android Studio (Giraffe or later)
- JDK 11 or higher
- Kotlin 1.9.0+
- Git

### Setting Up Your Development Environment

1. Fork the repository on GitHub
2. Clone your fork locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/building-with-omeifeAI-hackathon.git
   cd building-with-omeifeAI-hackathon
   ```
3. Add the upstream repository as a remote:
   ```bash
   git remote add upstream https://github.com/abgolor/building-with-omeifeAI-hackathon.git
   ```
4. Create a branch for your work:
   ```bash
   git checkout -b feature/your-feature-name
   ```

### API Keys

To test the application with the Omeife AI translation service:

1. Request an API key from [Omeife AI](https://omeife.ai)
2. Create or edit `local.properties` in the project root:
   ```properties
   OMEIFE_API_KEY=your_api_key_here
   ```

## Development Workflow

1. Keep your branch updated with upstream:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. Commit changes with meaningful messages:
   ```bash
   git commit -m "feat: add new translation feature"
   ```
   We follow [Conventional Commits](https://www.conventionalcommits.org/) for commit messages.

3. Push your changes to your fork:
   ```bash
   git push origin feature/your-feature-name
   ```

## Pull Request Process

1. Update the README.md with details of changes if applicable
2. Ensure all tests pass and code meets quality standards
3. Submit a pull request against the `develop` branch
4. Reference any related issues in your PR description
5. Wait for review by maintainers

### PR Requirements Checklist

- [ ] Code follows project style guidelines
- [ ] Documentation has been updated
- [ ] Tests have been added/updated
- [ ] All checks pass (CI/CD)
- [ ] Self-reviewed the code changes

## Coding Standards

We follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) with the following additions:

- Use meaningful names for classes, functions, and variables
- Keep functions small and focused on a single responsibility
- Comment complex algorithms or business logic
- Prefer extension functions for utility operations
- Use sealed classes for managing state

### Architecture

- Follow Clean Architecture principles
- Use MVVM pattern for presentation layer
- Place business logic in use cases
- Keep UI components as dumb as possible

## Testing Guidelines

- Write unit tests for business logic and use cases
- Create UI tests for critical user flows
- Aim for at least 80% code coverage for non-UI code
- Mock external dependencies in tests

## Documentation

- Document public APIs and complex functions
- Update README.md when adding major features
- Create/update Wiki pages for detailed documentation
- Use KDoc comments for Kotlin code

## Issue Reporting

When reporting issues, please use the issue templates provided in the repository. Include:

- Steps to reproduce the issue
- Expected behavior
- Actual behavior
- Screenshots if applicable
- Device information and OS version

## Feature Requests

Feature requests are welcome. Please use the feature request template and provide:

- Clear description of the feature
- Rationale for the feature
- Possible implementation details if you have them

---

Thank you for contributing to EkoTranslate! Your efforts help make language barriers less of an obstacle in Lagos and beyond.