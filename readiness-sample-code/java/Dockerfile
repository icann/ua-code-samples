FROM gradle:7.3.3-jdk11

WORKDIR /backend

COPY ua-sample-code-lib ua-sample-code-lib
COPY ua-sample-code-web ua-sample-code-web

WORKDIR ua-sample-code-web

CMD ["/bin/bash"]

RUN gradle build install

EXPOSE 4567
CMD [ "./build/install/ua-sample-code-web/bin/ua-sample-code-web" ]
