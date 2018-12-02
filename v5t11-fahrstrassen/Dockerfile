FROM openjdk:8

RUN useradd -m moba

USER moba

COPY --chown=moba:moba maven /home/moba/app

EXPOSE 8080

CMD ["java", "-cp", "/home/moba/app/*", "de.gedoplan.v5t11.fahrstrassen.FahrstrassenMain"]
