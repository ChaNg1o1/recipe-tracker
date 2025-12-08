FROM amazoncorretto:17-alpine

RUN apk add --no-cache ttyd util-linux asciinema ncurses

WORKDIR /app

COPY target/RecipeTracker-*.jar app.jar
COPY deploy/demo.cast demo.cast
COPY deploy/menu.sh menu.sh

RUN chmod +x /app/menu.sh

EXPOSE 8000

ENV TERM=xterm-256color
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

CMD ["sh", "-c", "ttyd -p 8000 -W -m 200 -t fontSize=14 /app/menu.sh"]
