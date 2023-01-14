FROM consol/ubuntu-xfce-vnc

USER 0

RUN apt-get update && apt-get install -y openjdk-8-jre
    
USER 1000 

COPY --chown=1000 maven app
RUN echo "#!/bin/sh" >v5t11-stellwerk.sh \
    && echo 'java -cp "./app/*" de.gedoplan.v5t11.stellwerk.StellwerkMain' >v5t11-stellwerk.sh \
    && chmod 775 v5t11-stellwerk.sh

ENV VNC_RESOLUTION="1920x1080"

