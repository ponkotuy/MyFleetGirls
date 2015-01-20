FROM debian:unstable
MAINTAINER web@ponkotuy.com

# apt-get
RUN apt-get update && apt-get install -y curl swftools software-properties-common && apt-get clean

# Install Oracle Java
RUN \
  echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu precise main" | tee -a /etc/apt/sources.list && \
  echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu precise main" | tee -a /etc/apt/sources.list && \
  apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886 && \
  apt-get update
RUN \
  echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  apt-get install -y oracle-java8-installer &&\
  apt-get clean

# install sbt
RUN mkdir ~/bin && \
  curl -s https://raw.githubusercontent.com/paulp/sbt-extras/master/sbt > ~/bin/sbt \
  && chmod 0755 ~/bin/sbt
VOLUME ["/root/.ivy2", "/root/.sbt"]

# Copy File
RUN mkdir ~/myfleet
ADD project ~/myfleet/project/
ADD server ~/myfleet/server/
ADD library ~/myfleet/library/
ADD client ~/myfleet/client/

# RUN
EXPOSE 9000
WORKDIR ~/myfleet
CMD \
  ~/bin/sbt -v stage && \
  bash -c 'server/target/universal/stage/bin/myfleetgirlsserver -Ddb.default.url="jdbc:mysql://${DB_PORT_3306_TCP_ADDR}:${DB_PORT_3306_TCP_PORT}/myfleet" -Ddb.default.user="root" -Ddb.default.password=""'
