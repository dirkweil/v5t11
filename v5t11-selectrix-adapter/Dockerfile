# TODO Create custom image for ARM
FROM jboss/wildfly:14.0.1.Final

# Add deployment
COPY --chown=jboss:jboss maven/* /opt/jboss/wildfly/standalone/deployments/

# Setup WildFly
COPY --chown=jboss:jboss src/main/wildfly/* /opt/jboss/wildfly-setup/

RUN cd /opt/jboss/wildfly-setup \
    && sh $JBOSS_HOME/bin/jboss-cli.sh --file=wildfly-setup.cli \
    && cd .. \
    && rm -fr wildfly-setup 
