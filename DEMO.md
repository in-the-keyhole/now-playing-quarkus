### Create a Project
---
- [ ] Create Quarkus project from the starter - https://code.quarkus.io/
    - [ ] quarkus-resteasy
    - [ ] quarkus-resteasy-jackson
    - [ ] quarkus-smallrye-openapi
    - [ ] quarkus-smallrye-health

    - [ ] Unzip the downloaded archive

    or

    ```
    quarkus create app \
        --extensions=resteasy,resteasy-jackson,smallrye-openapi,smallrye-health \
        --package-name=com.keyholesoftware \
        com.keyholesoftware:quarkus-jug
    ```

    - [ ] Open in IDE & walk through the generated source

---
### Developer (Joy) Experience
---

- [ ] Talk about "Dev Mode" and start the application using:
    ```
    ./mvnw quarkus:dev
    ```
    or
    ```
    quarkus dev
    ```

- [ ] Show 'Live Coding' features
    - [ ] Defaults to restart on next invocation 
    - [ ] Force restart [s]
    - [ ] w Instrumentation [i]
- [ ] Show 'Continuous Testing'
    - [ ] Re-run all [r]
    - [ ] Re-run failed tests [f]
- [ ] Show 'Dev Mode' menu [h]
- [ ] Show log level toggling [j]
- [ ] Open the Dev UI [d]
- [ ] Walk thru Dev UI
    - [ ] Extensions 
        - [ ] Health
        - [ ] OpenAPI / Swagger
    - [ ] Configuration
        - [ ] All properties (Filterable)
        - [ ] "Show only my properties"

---
### Dev Services
---
- [ ] Add OIDC extension / Copy mvn cmd from starter for quarkus-oidc
    ```
    ./mvnw quarkus:add-extension \
        -Dextensions="io.quarkus:quarkus-oidc"
    ```
    or
    ```
    quarkus ext add oidc
    ```
- [ ] Live Coding honors adding/configuring extensions (including Dev Services) - Demonstrate automatically starting the Dev Service (using Testcontainers)
- [ ] Show new Keycloak container running in Docker
- [ ] Show Extension now in DEV UI
    - [ ] Show link to documentation

- [ ] Implement OIDC
    - [ ] Add annotation to GreetingResource
        ```
        @RolesAllowed("admin")
        ```

    - [ ] Show 401 (in Postman)

    - [ ] Demonstrate Keycloak 'dev mode'
        - [ ] Show link to Keycloak dev mode interface
        - [ ] Login as bob
        - [ ] Show 403
        - [ ] Logout / in as alice
        - [ ] Show 200

---
### Expand on Quarkus Extensions and Dev Services
---
- [ ] Stop dev mode and close VS Code
- [ ] Open VS Code on `now-playing-quarkus`
    - [ ] Talk about the app features and extensions used
        - [ ] It's a ReST API for movie data
        - [ ] Postgresql / JPA / Flyway
        - [ ] Redis cache
        - [ ] MicroProfile
            - [ ] OpenAPI
            - [ ] Health
            - [ ] Metrics
            - [ ] Fault Tolerance
            - [ ] OIDC
      
    - [ ] Run in dev mode
        ```
        ./mvnw quarkus:dev
        ```

    - [ ] Show the dev services containers that were started
    - [ ] Demo running app in Postman
    
---
### Packaging & Run Modes
---
- [ ] Stop dev mode [q]


> #### JVM Mode

- [ ] Package the app

    ```
    ./mvnw clean package -DskipTests
    ```
    or
    ```
    quarkus build --no-tests
    ```


- [ ] Show produced package layers in target/quarkus-app dir  (discuss docker filesystem layers)
    ```
    tree ./target/quarkus-app
    ```

- [ ] Show no running containers
    ```
    docker ps
    ```

- [ ] Start k8s cluster & k9s

   ```
   k3d cluster start quarkus-demo
   ```

    ```
    k9s
    ```

- [ ] Show running "prod" containers in k9s
- [ ] Port forward containers so we can run locally
    - [ ] keycloak (8180)
    - [ ] postgresql (5432)
    - [ ] redis-master (6379)

- [ ] Run in JVM mode 
    ```
    java -jar -Dquarkus.profile=local target/quarkus-app/quarkus-run.jar
    ```

- [ ] Run JVM Mode in Docker (created during package)
    ```
    docker run -i --rm -p 8080:8080 \
        -equarkus.datasource.jdbc.url=jdbc:postgresql://host.docker.internal:5432/now-playing \
        -equarkus.oidc.auth-server-url=http://host.docker.internal:8180/realms/quarkus-demo \
        -equarkus.redis.hosts=redis://:thisshouldbesecret@host.docker.internal/ \
        -equarkus.profile=local \
        keyholesoftware/now-playing-quarkus:1.0.0-SNAPSHOT
    ```
    - [ ] Point out startup time

> #### Native Mode

- [ ] Build native image - start it locally, but it will take a long time (~7-10 mins)
    ```
    ./mvnw clean package \
        -Pnative \
        -DskipTests \
        -Dquarkus.native.container-build=true
    ```
    - [ ] Discuss build time & pre-built image

- [ ] Run OSX native runner from demo
    ```
    ./demo/now-playing-quarkus-1.0.0-SNAPSHOT-runner -Dquarkus.profile=local
    ```

- [ ] Discuss pre-built native image 
    ```
    docker images
    ```
    ```
    keyholesoftware/now-playing-springboot:1.0.0-SNAPSHOT-native
    ```

    - [ ] Run native docker image locally
      ```
      docker run -i --rm -p 8080:8080 \
        -equarkus.datasource.jdbc.url=jdbc:postgresql://host.docker.internal:5432/now-playing \
        -equarkus.oidc.auth-server-url=http://host.docker.internal:8180/realms/quarkus-demo \
        -equarkus.redis.hosts=redis://:thisshouldbesecret@host.docker.internal/ \
        -equarkus.profile=local \
        keyholesoftware/now-playing-quarkus:1.0.0-SNAPSHOT-native
      ```
      
##### Kubernetes

- [ ] Deploy to k8s
    ```
    ./mvnw clean package \
        -DskipTests \
        -Dquarkus.kubernetes.deploy=true
    ```
    - [ ] Point out start up time

- [ ] Deploy JVM Mode image to k8s
    ```
    kubectl apply -f src/main/kubernetes/kubernetes-jvm.yml
    ```
    - [ ] Point out start up time

- [ ] Deploy Spring  Boot image to k8s
    ```
    cd ../now-playing-springboot
    kubectl apply -f src/main/kubernetes/kubernetes.yml
    ```
    - [ ] Point out start up time


- [ ] Show memory usage in Grafana


###### To get JWT (access token)

- [ ] Port forward the keycloak svc (8180)
- [ ] Get JWT on clipboard
    ```
    curl --insecure -X POST http://localhost:8180/realms/quarkus-demo/protocol/openid-connect/token \
        --user now-playing:secret \
        -H 'content-type: application/x-www-form-urlencoded' \
        -H 'host: keycloak' \
        -d 'username=bob&password=bob&grant_type=password' | jq --raw-output '.access_token' | tr -d '\n' | pbcopy
    ```
