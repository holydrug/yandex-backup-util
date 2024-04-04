[![Java CI with Maven](https://github.com/holydrug/yandex-backup-util/actions/workflows/maven-build.yml/badge.svg)](https://github.com/holydrug/yandex-backup-util/actions/workflows/maven-build.yml)
[![Hits-of-Code](https://hitsofcode.com/github/holydrug/yandex-backup-util)](https://hitsofcode.com/github/holydrug/yandex-backup-util/view)

## Quick Start
To get the latest release from Maven Central, simply add the following to your pom.xml:

```xml
<dependency>
    <groupId>io.github.holydrug</groupId>
    <artifactId>yandex-backup-util</artifactId>
    <version>1.0</version>
</dependency>
```

The releases are also available on [Maven Central Repository](https://central.sonatype.com/artifact/io.github.holydrug/yandex-backup-util)!

## Usage

### OAUTH Token 
To recieve token 👉👉👉👉 [yandex-token-getting-started](https://yandex.ru/dev/disk-api/doc/ru/concepts/quickstart#oauth)

### Tool To upload zip
```java
YandexDiskUploader yandexDiskUploader = new YandexDiskUploader();
yandexDiskUploader.uploadBackupToYandexDisk(
        "path/to/zip",
        "path/to/save/in/yandex",
        "token"
);
```

### Tool To zip dir or file
```java
YandexBackupZipper yandexBackupZipper = new YandexBackupZipper();
yandexBackupZipper.zipFolder(
        Path.of("what/to/zip"),
        Path.of("where/to/locate")
);
```

