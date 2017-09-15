# stackoverflow-search

## Installation & Usage

For configuration purposes this project uses [environ](https://github.com/weavejester/environ) library. Needed parameters are set in `profiles.clj` file. You can specify yours if you want. Also you can redefine them by setting corresponding environment variables.

### With docker and docker-compose

#### Prerequisites
[Install docker and docker-compose](https://docs.docker.com/compose/install/).

#### Usage
To run tests
```
docker-compose up test
```

To start the server in development mode
```
docker-compose up web
```
You can start make requests http://localhost:3000/search?tag=clojure&tag=java.

To start the server in production mode
```
docker-compose up web-prod
```


### Without docker

#### Prerequisites
- [Clojure 1.8.0](https://clojure.org/guides/getting_started).
- [Leiningen](https://github.com/technomancy/leiningen)

#### Usage
To run tests
```
lein test
```

To start the server in development mode
```
lein ring server-headless
```
You can start make requests http://localhost:3000/search?tag=clojure&tag=java.

To start the server in production mode
```
lein ring uberjar
PORT=80 java -jar target/stackoverflow-search-0.1.0-SNAPSHOT-standalone.jar
```
