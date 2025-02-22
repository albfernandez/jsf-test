# Release Process

This guide provides a chronological steps which goes through release tagging, staging, verification and publishing.


## Check the SNAPSHOT builds and pass the tests

Check that the project builds in java 8 and java 11.

```bash
mvn clean package verify
```

## Set version and build 

```bash
# change release in poms and README.md 
mvn clean verify javadoc:jar 
mvn clean install javadoc:jar 
mvn -Psign clean package javadoc:jar deploy
git add -A
git commit -S -m 'Release <1.1.14>'
git tag -a <1.1.14> -m "Tagging release <1.1.14>"
git push
git push --tags
```


## Prepare next iteration

```bash
# change release in poms
git add -A
git commit -S -m 'Next release cycle'
git push
```

## Create release and upload artifacts to Github

Manually creating the release in Github project page, and upload generated artifacts.
