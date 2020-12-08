FROM nginx:1.19.5-alpine

ENV LANG C.UTF-8
ENV TZ Asia/Tokyo

WORKDIR /root

ADD ./infra/nginx/prod/default.conf /etc/nginx/conf.d/default.conf

EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]
