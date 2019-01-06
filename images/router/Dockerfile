FROM cassandra:latest

RUN apt-get update
RUN apt-get install -y iputils-ping

COPY partition.sh /usr/local/bin/partition
ENV DIR /project
RUN mkdir -p $DIR
ENV LOG /tmp/router.log

# ENTRYPOINT ["bash"]
CMD exec /bin/bash -c "trap : TERM INT; sleep infinity & wait"
# ENTRYPOINT ["tail", "-f", "/dev/null"]
