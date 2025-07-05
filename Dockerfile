# TODO: dockerize the final app

FROM ubuntu:latest
LABEL authors="jakhongir"

ENTRYPOINT ["top", "-b"]