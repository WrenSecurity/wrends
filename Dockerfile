FROM maven:3.8.6-eclipse-temurin-17 AS project-build

# Install build dependencies
RUN \
  apt-get update && \
  apt-get install -y --no-install-recommends unzip git

# Copy project files
WORKDIR /project
COPY . .

# Perform actual Wren:DS build
ARG MAVEN_BUILD_ARGS
RUN \
  --mount=type=cache,target=/root/.m2 \
  mvn package ${MAVEN_BUILD_ARGS}

# Extract built artifact into target directory
RUN \
  mkdir /build && \
  WRENDS_VERSION=$(mvn -Dexpression=project.version -q -DforceStdout help:evaluate) && \
  unzip opendj-server-legacy/target/package/wrends-${WRENDS_VERSION}.zip -d /build


FROM eclipse-temurin:17-jre

# Set environment variables
ENV \
  BASE_DN="dc=example,dc=com" \
  LDAP_PORT=1389 \
  LDAPS_PORT=1636 \
  ADMIN_CONNECTOR_PORT=4444 \
  ROOT_USER_DN="cn=Directory Manager" \
  ROOT_USER_PASSWORD=password \
  SSL_OPTIONS="--generateSelfSignedCertificate" \
  ADDITIONAL_SETUP_ARGS="--addBaseEntry"

# Create wrends user
ARG WRENDS_UID=1000
ARG WRENDS_GID=1000
RUN addgroup --gid ${WRENDS_GID} wrends && \
  adduser --uid ${WRENDS_UID} --gid ${WRENDS_GID} --system wrends

# Deploy wrends project
COPY --chown=wrends:root --from=project-build /build/wrends /opt/wrends

# Copy management scripts
COPY --chown=wrends:root .docker/docker-entrypoint.sh /opt/wrends
COPY --chown=wrends:root .docker/bootstrap /opt/wrends/bootstrap

USER ${WRENDS_UID}
WORKDIR /opt/wrends

ENV PATH="${PATH}:/opt/wrends/bin"

# Prepare Wren:DS instance directory
ARG INSTANCE_DIR="/opt/wrends/instance"
RUN echo ${INSTANCE_DIR} > /opt/wrends/instance.loc
RUN mkdir -p ${INSTANCE_DIR} ${INSTANCE_DIR}/lib/extensions
VOLUME ${INSTANCE_DIR}

EXPOSE ${LDAP_PORT} ${LDAPS_PORT} ${ADMIN_CONNECTOR_PORT}

ENTRYPOINT ["/opt/wrends/docker-entrypoint.sh"]
