FROM anapsix/alpine-java:8_jdk
MAINTAINER web@ponkotuy.com

# install sbt
RUN \
  apk add --update curl && \
  mkdir ~/bin && \
  curl -s https://raw.githubusercontent.com/paulp/sbt-extras/master/sbt > ~/bin/sbt && \
  chmod 0755 ~/bin/sbt && \
  apk del curl && \
  rm -rf /var/cache/apk/* /tmp/*

VOLUME ["/root/.ivy2", "/root/.sbt"]

# Copy File
COPY project /myfleet/project/
COPY server /myfleet/server/
COPY library /myfleet/library/

# RUN
EXPOSE 9000
WORKDIR /myfleet
CMD \
  ~/bin/sbt -v server/stage && \
  rm -f server/target/universal/stage/RUNNING_PID && \
  server/target/universal/stage/bin/myfleetgirlsserver \
    -Ddb.default.url="jdbc:mysql://${DB_HOST}:${DB_PORT}/myfleet" \
    -Ddb.default.user="root" \
    -Ddb.default.password=""
