# nocom-http

Backend server for NoComment

## Building

1. Copy sensitive-template.properties and rename it sensitive.properties
2. (Optional) Fill in the details for each option.
3. Run `./gradlew build`

## Setting up postgres and ssh-tunnel configurations

To connect to the postgres database, the webserver will need details on how to reach
and login to the database. There are two options to do this.

### Via configuration file

1. Copy `application.yml` from `src/main/resources` into the work directory.
2. Remove everything except `nocom.ssh-tunnel` and `nocom.datasource.postgres`
3. Fill in the details. If you don't need an ssh tunnel for port-forwarding, then 
exclude those properties. Otherwise, set `nocom.ssh-tunnel.enabled` to `true`.
4. When running your jar, make sure to add the config file into `spring.config.location`
(This will be explained more down below).

### Via system variables

Create these system path variables before running the jar
* POSTGRES_HOSTNAME
* POSTGRES_PORT
* POSTGRES_DATABASE
* POSTGRES_USERNAME
* POSTGRES_PASSWORD

Note: This is only for postgres. It will not work if you need a ssh-tunnel.

## Running the application in development

There are two methods to run the application while in development mode.

### In the IDE

IntelliJ will automatically detect the spring boot application and create a
NocomApplication. In order to set it up properly you must provide the correct
profile and (optionally) override configuration file.

```
-Dspring.profiles.active=dev -Dspring.config.location=classpath:/application.yml,file:./config.yml
```

Replace `./config.yml` with whatever you named the configuration file. If you didn't
create a configuration file, then you can exclude `-Dspring.config.location`.

### Via Gradle

This is very simple, you only need to run `./gradle bootRun` and it will handle
setting the vm parameters for you. However, it requires that you name the configuration
file `config.yml`.

### Running the application in production

In production, the jar should be run using `java -jar`. Move or create the configuration
file into the directory that the jar is in.

The result should look something like this

```shell script
java -jar nocom-http-1.0-SNAPSHOT.jar -Dspring.config.location=classpath:/application.yml,file:./config.yml
```

TODO: Adding users into production builds
