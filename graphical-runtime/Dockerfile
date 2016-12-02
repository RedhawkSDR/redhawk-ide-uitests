# The docker context when building this file must be the TOP LEVEL of the repository, i.e:
# docker build -f graphical-runtime/Dockerfile .
FROM redhawk/ide-testenv:2.1-nightly-el6-x86_64

COPY . /usr/src/app
WORKDIR /usr/src/app/graphical-runtime

RUN mvn clean install --batch-mode -P develop-2.1

