FROM lughconsultoria/dpm

ARG VERSION_SNKW


RUN ["downloadSankhyaBinaries"]

RUN mkdir -p "/home/sankhya/wildfly/standalone/deployments/sankhyaw.ear/projeto"
VOLUME ["/home/sankhya/wildfly/standalone/deployments/sankhyaw.ear/projeto"]
