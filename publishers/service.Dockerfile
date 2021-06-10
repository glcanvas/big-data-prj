FROM adoptopenjdk:11-jdk-hotspot


ENV GRADLE_HOME /opt/gradle

ENV SAVER saver
ENV LOADER loader
ENV CRON cron

RUN set -o errexit -o nounset \
    && echo "Adding gradle user and group" \
    && groupadd --system --gid 1000 gradle \
    && useradd --system --gid gradle --uid 1000 --shell /bin/bash --create-home gradle \
    && mkdir /home/gradle/.gradle \
    && chown --recursive gradle:gradle /home/gradle \
    \
    && echo "Symlinking root Gradle cache to gradle Gradle cache" \
    && ln -s /home/gradle/.gradle /root/.gradle

VOLUME /home/gradle/.gradle

WORKDIR /home/gradle

RUN apt-get update \
    && apt-get install --yes --no-install-recommends \
        fontconfig \
        unzip \
        wget \
        \
        bzr \
        git \
        git-lfs \
    && rm -rf /var/lib/apt/lists/*

ENV GRADLE_VERSION 7.0.2
ARG GRADLE_DOWNLOAD_SHA256=0e46229820205440b48a5501122002842b82886e76af35f0f3a069243dca4b3c
RUN set -o errexit -o nounset \
    && echo "Downloading Gradle" \
    && wget --no-verbose --output-document=gradle.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
    \
    && echo "Checking download hash" \
    && echo "${GRADLE_DOWNLOAD_SHA256} *gradle.zip" | sha256sum --check - \
    \
    && echo "Installing Gradle" \
    && unzip gradle.zip \
    && rm gradle.zip \
    && mv "gradle-${GRADLE_VERSION}" "${GRADLE_HOME}/" \
    && ln --symbolic "${GRADLE_HOME}/bin/gradle" /usr/bin/gradle \
    && echo "Testing Gradle installation" \
    && gradle --version \
    && mkdir /opt/etc \
    && mkdir /opt/apps

RUN rm -rf /opt/apps \
    && rm -rf /root/big-data-prj \
    && pwd \
    && ls -al /opt \
    && echo "===============" \
    && ls -al /root

RUN rm -rf /var/data
RUN mkdir /var/data

RUN cd /root \
    && git clone https://github.com/glcanvas/big-data-prj.git \
    && cd /root/big-data-prj \
    && gradle \
    && gradle createAllJar

RUN mkdir /opt/apps

RUN mv /root/big-data-prj/${SAVER}/build/libs/${SAVER}-1-all.jar /opt/apps/${SAVER}.jar \
    && mv /root/big-data-prj/${LOADER}/build/libs/${LOADER}-1-all.jar /opt/apps/${LOADER}.jar \
    && mv /root/big-data-prj/${CRON}/build/libs/${CRON}-1-all.jar /opt/apps/${CRON}.jar \
    && cd /opt/apps

COPY ./microservice-launcher.sh /opt/apps/microservice-launcher.sh

RUN chmod u+x /opt/apps/microservice-launcher.sh

CMD ["sh", "-c", "/opt/apps/microservice-launcher.sh"]