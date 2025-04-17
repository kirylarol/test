FROM node:current-alpine


EXPOSE 3000
ENV BACK_IP = ${BACK_IP}
RUN ls



WORKDIR /app
COPY ["package.json", "package-lock.json*", "./"]
RUN npm cache clean --force && \
    npm install -g npm@latest && \
    npm install

CMD ["npm", "start"]
