FROM debian:unstable

# apt-get
RUN apt-get update && apt-get install -y openjdk-8-jdk curl
RUN apt-get clean

# install sbt
RUN mkdir ~/bin
RUN curl -s https://raw.githubusercontent.com/paulp/sbt-extras/master/sbt > ~/bin/sbt \
  && chmod 0755 ~/bin/sbt
VOLUME ~/.ivy2
VOLUME ~/.sbt

# Copy File
RUN mkdir ~/myfleet
ADD project ~/myfleet/project/
ADD server ~/myfleet/server/
ADD library ~/myfleet/library/
ADD client ~/myfleet/client/
WORKDIR ~/myfleet

EXPOSE 9000

RUN ~/bin/sbt compile
CMD server/target/universal/stage/bin/myfleetgirlsserver
