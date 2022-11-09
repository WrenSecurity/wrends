# OpenDJ 3.x

This is OpenDJ 3.x fork maintained by Wren Security.

## Compatibility

This branch is fully compatible with the last OSS version released by ForgeRock AS.


## Building from sources

```console
mvn -s .mvn/settings.xml -Dignore-artifact-sigs install -pl opendj-maven-plugin -am
```

```console
mvn -s .mvn/settings.xml -Dclirr.skip -Dignore-artifact-sigs verify
```
