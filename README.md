# Morfologik Polish Lemmatizer plugin for OpenSearch

OpenSearch analysis plugin for the Polish language. It integrates
[lucene-analysis-morfologik](https://lucene.apache.org) with OpenSearch,
providing a lemmatizer backed by [Morfologik](http://morfologik.blogspot.com).

Lemmatization reduces inflected word forms to their base (dictionary) form,
which greatly improves recall for Polish-language searches — e.g. "kotów",
"kotom", "koty" are all reduced to "kot".

## Requirements

| Plugin version | OpenSearch | Java  |
|---------------|-----------|-------|
| 3.5.x         | 3.5.x     | 21+   |
| 3.4.x         | 3.4.x     | 21+   |

## Install

### OpenSearch CLI

```bash
opensearch-plugin install \
  https://github.com/Ksawierek/opensearch-analysis-morfologik/releases/download/3.5.0/opensearch-analysis-morfologik-3.5.0.zip
```

### Dockerfile / Containerfile

```dockerfile
FROM opensearchproject/opensearch:3.5.0

RUN opensearch-plugin install \
  https://github.com/Ksawierek/opensearch-analysis-morfologik/releases/download/3.5.0/opensearch-analysis-morfologik-3.5.0.zip
```

## Usage

The plugin registers two analysis components:

| Type           | Name              | Description                                      |
|----------------|-------------------|--------------------------------------------------|
| Analyzer       | `morfologik`      | Full analyzer: tokenizer + lowercase + stemming  |
| Token filter   | `morfologik_stem` | Stemming filter only, for custom analyzer chains |

### morfologik analyzer

```json
PUT /my-index
{
  "settings": {
    "analysis": {
      "analyzer": {
        "polish": {
          "type": "morfologik"
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "content": {
        "type": "text",
        "analyzer": "polish"
      }
    }
  }
}
```

### morfologik_stem token filter

Use the filter in a custom analyzer chain (e.g. combined with a synonym filter):

```json
PUT /my-index
{
  "settings": {
    "analysis": {
      "analyzer": {
        "polish_custom": {
          "type": "custom",
          "tokenizer": "standard",
          "filter": ["lowercase", "polish_stem"]
        }
      },
      "filter": {
        "polish_stem": {
          "type": "morfologik_stem"
        }
      }
    }
  }
}
```

### Custom dictionary

You can supply your own Morfologik dictionary file instead of the built-in
Polish one. Place the `.dict` and `.info` files in the OpenSearch `config/`
directory and reference the `.dict` file via the `dictionary` parameter:

```json
"filter": {
  "my_stem": {
    "type": "morfologik_stem",
    "dictionary": "my-custom.dict"
  }
}
```

### Test analysis via API

```bash
POST /my-index/_analyze
{
  "analyzer": "morfologik",
  "text": "szybkich kotów"
}
```

Expected output contains lemmas: `szybki`, `kot`.

## Building from source

Requirements: JDK 21+ (e.g. Temurin), Gradle wrapper included.

```bash
JAVA_HOME=~/.jdks/temurin-25.0.2 ./gradlew build
```

Run unit tests only (no cluster required):

```bash
JAVA_HOME=~/.jdks/temurin-25.0.2 ./gradlew test
```

Run integration tests (starts an embedded OpenSearch cluster):

```bash
JAVA_HOME=~/.jdks/temurin-25.0.2 ./gradlew integTest
```

The plugin ZIP is produced at:

```
build/distributions/opensearch-analysis-morfologik-<version>.zip
```

## Dependencies

- `org.apache.lucene:lucene-analysis-morfologik`
- `org.carrot2:morfologik-stemming`
- `org.carrot2:morfologik-polish`
- `org.carrot2:morfologik-fsa`

## Useful links

- [Apache Lucene](https://github.com/apache/lucene)
- [Morfologik stemming library](https://github.com/morfologik/morfologik-stemming)
- [Elasticsearch equivalent plugin](https://github.com/allegro/elasticsearch-analysis-morfologik)
