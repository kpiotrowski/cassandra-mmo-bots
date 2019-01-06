FROM cassandra:latest

USER root

RUN apt-get update
RUN apt-get install -y iputils-ping


# Copy cassandra config files
COPY cassandra.yaml  /etc/cassandra/
COPY cassandra-env.sh /etc/cassandra/

# Copy scripts to run after cassandra is running
ENV CQL /project
RUN mkdir -p $CQL

# Set cassandra seeds
ENV CASSANDRA_SEEDS "10.0.0.3,10.10.10.3"

COPY docker-entrypoint.sh /usr/local/bin/

# COPY cassandra_init.sh /usr/local/bin/
ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["cassandra", "-f", "-R"]
