FROM debian:unstable
MAINTAINER web@ponkotuy.com

# Add Respository
RUN \
    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 0xcbcb082a1bb943db && \
    echo "deb http://ftp.yz.yamagata-u.ac.jp/pub/dbms/mariadb/repo/10.0/debian sid main" > /etc/apt/sources.list.d/mariadb.list

# apt-get
RUN apt-get update && apt-get install -y curl swftools mariadb-server software-properties-common && apt-get clean

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
VOLUME ["/root/.ivy2", "/root/.sbt", "/var/lib/mysql"]

# MariaDB Settings
RUN \
  echo "mysqld_safe &" > /tmp/config && \
  echo "mysqladmin --silent --wait=30 ping || exit 1" >> /tmp/config && \
  echo "mysql -e 'CREATE DATABASE myfleet;'" >> /tmp/config && \
  bash /tmp/config && \
  rm -f /tmp/config

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
  bash -c "mysqld_safe &" && \
  ~/bin/sbt -v stage && \
  server/target/universal/stage/bin/myfleetgirlsserver
