[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/NA35YsxO)
[![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-2e0aaae1b6195c2367325f4f02e2d04e9abb55f0b24a779b69b11b9e10269abc.svg)](https://classroom.github.com/online_ide?assignment_repo_id=22563103&assignment_repo_type=AssignmentRepo)

# A Better Bill Split App

## Tech Stack

**Frontend:** default Next.js project settings
- React
- TypeScript
- Tailwind
- ESLint
- App Router
- Turbopack

Backend:
- Server: Springboot 
- Database: Postgres with Supabase
- Message Queue: Redis

## System Requirements

Frontend:
- Node.js v20.15.0
- Next.js v16.1.6
- React v19.2.3

Backend:
- Java v25.0.2
- Maven v14.6.1
- Springboot v4.1.0-M1
  - GraphQL DGS Code Generation
  - Spring Boot DevTools
  - Spring for GraphQL
  - PostgreSQL Driver
  - Spring Data JPA
- Postgres 
- Redis 

Read more about frontend dependencies [here](./frontend/package-lock.json) and backend dependencies [here](./backend/pom.xml).

## Installation Instructions

frontend: 
- cd frontend
- npm i
- eventually, need to set up API keys i imagine too with local env file

backend set up:
- need to make sure meet some system requs first
  - make sure you have java 25 installed 
    - you can download it here: https://www.oracle.com/java/technologies/downloads/#jdk25-mac
    - you can check with `java -version`
  - make sure you have maven installed (it'll be our dependnecy manager, think of it like npm but for our backend)
    - multiple ways to do that found here: https://maven.apache.org/install.html
      - in my case, i've used HomeBrew before for installing things and have a Mac, so i just did `brew install maven`
      - you can check version with `mvn -v`
  - developers: make sure you have access to team supabase
- need to set up a local env file that lets you replace the DB_URL, DB_USERNAME, and DB_PASSWORD in the [application.properties file](./backend/src/main/resources/application.properties)
- OPEN AND LOG INTO THE DOCKER APP ON YOUR LAPTOP (just having it open is enough for supabase to do its thing with it)
- cd backend

run the database:
- FOLLOW THIS WHOLE TUTORIAL (MULTIPLE COMMANDS) https://medium.com/@ianktoo/my-first-time-setting-up-supabase-locally-and-why-it-almost-broke-me-the-quick-version-a17bab7ca1b0

run the server:
- `mvn spring-boot:run`

- go to http://localhost:8000/graphiql?path=/graphql and type in the following query
```
query {
  getSessionById(id: 1) {
    id
    partySize
  }
}
```

to pause the docker / stop the DB from running, you need to do `npx supabase stop`
note to self: add team to supabase settings and set up local .env

## References

### Setup & Installation
Next.js Project Creation: https://nextjs.org/docs/app/getting-started/installation
Springboot Project Creation: https://start.spring.io/
Connecting Springboot and Postgres: https://medium.com/@AlexanderObregon/using-spring-boot-with-postgresql-for-data-persistence-49e843ab46fc
Setting up Supabase: https://medium.com/@ianktoo/my-first-time-setting-up-supabase-locally-and-why-it-almost-broke-me-the-quick-version-a17bab7ca1b0
Creating Supabase tables: https://supabase.com/docs/guides/local-development/overview
Connecting to database: https://supabase.com/docs/guides/database/connecting-to-postgres
Getting Started with Spring Boot and PostgreSQL: https://dev.to/codereacher_20b8a/getting-started-with-spring-boot-and-postgresql-a-beginner-friendly-guide-2mhb

### GraphQL 
Building a GraphQL service: https://spring.io/guides/gs/graphql-server
Class Notes App: https://github.com/CS-396-Full-Stack-Software-Eng/notes_app/blob/w6_eda_notes_summary_grpc/backend_spring/src/main/java/com/notes/app/data/Note.java
Using Spring Boot with PostgreSQL for Data Persistence: https://medium.com/@AlexanderObregon/using-spring-boot-with-postgresql-for-data-persistence-49e843ab46fc

### General Documentation
Springboot: https://docs.spring.io/spring-boot/index.html

## Contributors (AKA "Team Pancakes")
Brock Brown\
Vivian Chen\
Samreen Ibrahim\
Sidney Robinson\
Joanna Soltys
