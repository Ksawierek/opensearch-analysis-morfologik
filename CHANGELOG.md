# Changelog

All notable changes to this project will be documented in this file.

## [3.6.0] - 2026-04-20
- Upgraded to OpenSearch 3.6.0

## [3.5.0] - 2026-03-09

### Changed
- Upgraded to OpenSearch 3.5.0
- Upgraded Lucene to 10.3.2 (`lucene-analysis-morfologik`)
- Replaced deprecated `task` syntax with `tasks.register` in `build.gradle`
- Added `resolutionStrategy` to resolve `com.google.errorprone:error_prone_annotations` version conflict between `log4j-core` and OpenSearch test framework

### Added
- Unit tests for `MorfologikAnalyzerProvider`, `MorfologikTokenFilterFactory`, and edge cases in `MorfologikTests`
- YAML REST tests covering lemmatization of nouns and verbs, and `morfologik_stem` filter in a custom analyzer chain
- Expanded README with `curl` examples, component selection guidance, and version compatibility table

## [3.4.0] - 2025-10-01

### Changed
- Upgraded to OpenSearch 3.4.0

## [3.3.2] - 2025-07-01

### Changed
- Upgraded to OpenSearch 3.3.2
