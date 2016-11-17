FROM redhawk/ide-testenv:2.0-nightly-el6-x86_64

COPY . /usr/src/app/graphical-runtime/
WORKDIR /usr/src/app/graphical-runtime

RUN mvn clean install --batch-mode -P develop-2.0

