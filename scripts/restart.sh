#!/bin/bash

until cqlsh -f $CQL/truncate_tables.cql > /dev/null 2>&1 ; do echo "Couldn't truncate tables" | tee -a $LOG 1>&2; sleep 2; done
until cqlsh -f $CQL/resources.cql > /dev/null 2>&1 ; do echo "Couldn't insert resources" | tee -a $LOG 1>&2; sleep 2; done
