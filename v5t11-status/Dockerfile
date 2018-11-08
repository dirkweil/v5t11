FROM gedoplan/v5t11-selectrix-adapter

# Add deployment
COPY --chown=jboss:jboss maven/* /opt/jboss/wildfly/standalone/deployments/

# Setup WildFly
COPY --chown=jboss:jboss src/main/wildfly/* /opt/jboss/wildfly-setup/
RUN cd /opt/jboss/wildfly-setup \
    && sh $JBOSS_HOME/bin/jboss-cli.sh --file=wildfly-setup.cli \
    && cd .. \
    && rm -fr wildfly-setup \
    && rm -fr $JBOSS_HOME/standalone/configuration/standalone_xml_history \
    && sh $JBOSS_HOME/bin/add-user.sh -a -sc $JBOSS_HOME/standalone/configuration -u anonymous -p anonymous_123 -g guest -s \
    && sh $JBOSS_HOME/bin/add-user.sh -sc $JBOSS_HOME/standalone/configuration -u admin -p admin_123 -s

# v5t11-Konfiguration    
COPY src/main/v5t11 v5t11/

EXPOSE 5445 8080 9990

# Start WildFly in full profile
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-c", "standalone-full.xml", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]